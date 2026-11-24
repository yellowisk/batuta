package br.unicamp.ic.mc322.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import br.unicamp.ic.mc322.exception.SaveWriteException;
import br.unicamp.ic.mc322.exception.SaveReadException;
import br.unicamp.ic.mc322.service.House;

public class HousePersistence {
    public static final Path DEFAULT_PATH = Paths.get("house.xml");
    private final Path file;

    public HousePersistence(){
        this(DEFAULT_PATH);
    }

    public HousePersistence(Path file){
        this.file = file;
    }

    public Path getFile(){
        return file;
    }

    public boolean saveExists(){
       return Files.isRegularFile(file);
    }

    public Optional<LocalDateTime> lastSaveDate() {
        if(!saveExists()){
            return Optional.empty();
        }
        try {
            var instant= Files.getLastModifiedTime(file).toInstant();
            LocalDateTime realDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return Optional.of(realDateTime);
        } catch (IOException e) {
             return Optional.empty();
        }
    }

    public void save(House h){
        if (Files.isDirectory(file)) {
            throw new SaveWriteException("Erro ao salvar: " + file + " é uma pasta, não um arquivo.");
        }
        Document doc = new HouseXmlWriter().write(h);

        Path tempFile = null;
        try {
            Path parentDir = file.toAbsolutePath().getParent();
            Files.createDirectories(parentDir);
            tempFile = Files.createTempFile(parentDir, "house_", ".tmp");

            try (OutputStream out = Files.newOutputStream(tempFile)) {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }

            replace(tempFile, file);
        } catch (IOException | TransformerException e) {
            throw new SaveWriteException("Erro ao salvar a casa em " + file + ": " + e.getMessage(), e);
        } finally {
            deleteIfLeftOver(tempFile);
        }
    }

    public House load() {
        byte[] content;
        try {
            content = Files.readAllBytes(file);
        } catch (NoSuchFileException e) {
            throw new SaveReadException("Nenhuma casa salva em " + file + ".", e);
        } catch (IOException e) {
            throw new SaveReadException("Erro ao ler " + file + ": " + e.getMessage(), e);
        }
        if (new String(content, StandardCharsets.UTF_8).isBlank()) {
            throw new SaveReadException("O arquivo " + file + " está vazio.");
        }

        Document doc;
        try {
            doc = HouseXml.newParser().parse(new ByteArrayInputStream(content));
        } catch (SAXException e) {
            throw new SaveReadException("O arquivo " + file + " não é um XML válido: " + e.getMessage(), e);
        } catch (IOException | ParserConfigurationException e) {
            throw new SaveReadException("Erro ao ler " + file + ": " + e.getMessage(), e);
        }

        try {
            return new HouseXmlReader().read(doc);
        } catch (SaveReadException e) {
            throw e;
        } catch (RuntimeException e) {
            // Número, data ou valor que quebra uma regra (e.g., custo negativo)
            throw new SaveReadException("Dados inválidos em " + file + ": " + e.getMessage(), e);
        }
    }

    private void replace(Path newFile, Path target) throws IOException {
        try {
            Files.move(newFile, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(newFile, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteIfLeftOver(Path tempFile) {
        if (tempFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException ignored) {
            // Só sobra um .tmp na pasta... o save de verdade já foi trocado ou continua o antigo
        }
    }
}
