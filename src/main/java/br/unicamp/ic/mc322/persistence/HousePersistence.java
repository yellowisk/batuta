package br.unicamp.ic.mc322.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import br.unicamp.ic.mc322.exception.SaveWriteException;
import br.unicamp.ic.mc322.exception.SaveReadException;
import br.unicamp.ic.mc322.model.lists.ElementList;
import br.unicamp.ic.mc322.service.House;

public class HousePersistence {
    public static final Path DEFAULT_PATH = Paths.get("house.xml");
    private Path file;
    public HousePersistence(){
        this.file= DEFAULT_PATH;
    }
    public HousePersistence(Path file){
        this.file = file;
    }

    public Path getFile(){
        return file;
    }
    public boolean saveExists(){
       return (Files.exists(file) && Files.isRegularFile(file));
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
        if(h == null){
            throw new SaveWriteException ("Erro ao tentar salvar uma casa inexistente.");

        }
        Path tempFile= null;
        try{

            Document doc = new HouseXmlWriter().write(h);

            Path parentDir = file.toAbsolutePath().getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            tempFile = Files.createTempFile(parentDir, "house_", ".tmp");

            try (OutputStream out = Files.newOutputStream(tempFile)) {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }

            replace(tempFile, file);

        } catch (SaveWriteException e) {
            throw e;
        } catch (Exception e) {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception ignored) {
                }
            }
            throw new SaveWriteException("Erro ao salvar o arquivo XML da casa.", e);
        }
    }
    
    public House load() {
        if(!saveExists()){
            throw new SaveReadException("Erro ao não encontrar arquivo:" + file);
        }
        try (InputStream in = Files.newInputStream(file)) {
        DocumentBuilder builder = HouseXml.newParser();

        Document doc = builder.parse(in);
        doc.getDocumentElement().normalize();

        return new HouseXmlReader().read(doc);

        } catch (SaveReadException e) {
            throw e;
        } catch (Exception e) {
            throw new SaveReadException("Erro ao carregar os dados da casa a partir do arquivo XML.", e);
        }
    }
    private void replace(Path newFile, Path target){
        try {
            Files.move(newFile, target,StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new SaveWriteException ("Erro ao substituir o arquivo XML.", e);
        }
    }
}
