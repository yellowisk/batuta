package br.unicamp.ic.mc322.persistence;

import br.unicamp.ic.mc322.exception.SaveReadException;
import br.unicamp.ic.mc322.exception.SaveWriteException;
import br.unicamp.ic.mc322.model.Note;
import br.unicamp.ic.mc322.model.lists.NoteList;
import br.unicamp.ic.mc322.model.House;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("Testes HousePersistence: Erros")
public class HousePersistenceErrorsTest {

    // Atributos que toda tarefa precisa ter no arquivo
    private static final String TASK_ATTRS =
            "creationDate=\"2026-11-19T08:30\" deadline=\"2026-11-30\" isStarred=\"false\" isCompleted=\"false\"";

    @TempDir
    Path folder;

    private Path file;
    private HousePersistence persistence;

    @BeforeEach
    void setUp() {
        file = folder.resolve("house.xml");
        persistence = new HousePersistence(file);
    }

    private void write(String content) throws Exception {
        Files.writeString(file, content);
    }

    private void writeHouse(String lists) throws Exception {
        write("<house version=\"1.0\" name=\"Rep\">" + lists + "</house>");
    }

    // Salva uma casa válida e troca um trecho do arquivo para estragá-lo
    private void saveAndBreak(String from, String to) throws Exception {
        persistence.save(TestHouses.full());
        String xml = Files.readString(file);
        assertTrue(xml.contains(from), "trecho não encontrado no save: " + from);
        write(xml.replace(from, to));
    }

    private SaveReadException assertLoadFails() {
        return assertThrows(SaveReadException.class, () -> persistence.load());
    }

    // Tipo que o leitor do XML não conhece
    private static class UnknownNote extends Note {
        UnknownNote() {
            super("Ixtranha", null);
        }
    }

    @Nested
    @DisplayName("Arquivo")
    class FileTests {

        @Test
        @DisplayName("Arquivo que não existe deve lançar SaveReadException")
        void shouldRejectMissingFile() {
            assertTrue(assertLoadFails().getMessage().contains("Nenhuma casa salva"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\n\t\n"})
        @DisplayName("Arquivo vazio ou só com espaços deve lançar SaveReadException")
        void shouldRejectBlankFile(String content) throws Exception {
            write(content);

            assertTrue(assertLoadFails().getMessage().contains("vazio"));
        }

        @Test
        @DisplayName("Arquivo que não é XML deve lançar SaveReadException com a causa do parser")
        void shouldRejectNonXml() throws Exception {
            write("isso não é xml");

            assertInstanceOf(SAXException.class, assertLoadFails().getCause());
        }

        @Test
        @DisplayName("Arquivo cortado no meio deve lançar SaveReadException")
        void shouldRejectTruncatedFile() throws Exception {
            persistence.save(TestHouses.full());
            String xml = Files.readString(file);
            write(xml.substring(0, xml.length() / 2));

            assertInstanceOf(SAXException.class, assertLoadFails().getCause());
        }

        @Test
        @DisplayName("XML inválido não deve imprimir nada no terminal")
        void shouldNotPrintParserErrors() throws Exception {
            write("<house");
            PrintStream originalErr = System.err;
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            System.setErr(new PrintStream(err));
            try {
                assertLoadFails();
            } finally {
                System.setErr(originalErr);
            }

            assertEquals("", err.toString());
        }
    }

    @Nested
    @DisplayName("Estrutura do XML")
    class StructureTests {

        @Test
        @DisplayName("Raiz diferente de <house> ou versão desconhecida devem lançar SaveReadException")
        void shouldRejectWrongRootOrVersion() throws Exception {
            write("<casa version=\"1.0\" name=\"Rep\"/>");
            assertLoadFails();

            write("<house version=\"2.0\" name=\"Rep\"/>");
            assertLoadFails();
        }

        @Test
        @DisplayName("Tag inesperada deve lançar SaveReadException em vez de ser ignorada")
        void shouldRejectUnexpectedTag() throws Exception {
            writeHouse("<meta/>");

            assertTrue(assertLoadFails().getMessage().contains("<meta>"));
        }

        @Test
        @DisplayName("Tipo de lista ou de elemento desconhecido deve lançar SaveReadException")
        void shouldRejectUnknownTypes() throws Exception {
            saveAndBreak("type=\"NoteList\"", "type=\"BookList\"");
            assertLoadFails();

            saveAndBreak("type=\"Note\"", "type=\"Book\"");
            assertLoadFails();
        }

        @Test
        @DisplayName("Elemento numa lista de outro tipo deve lançar SaveReadException em vez de sumir")
        void shouldRejectElementInWrongList() throws Exception {
            writeHouse("<list type=\"ShoppingList\" title=\"M\" isCompleted=\"false\">"
                    + "<element type=\"Task\" title=\"t\" " + TASK_ATTRS + "/></list>");
            assertLoadFails();

            /* Uma lista de tarefas avulsas não aceita item de compra, mesmo sendo uma Task */
            writeHouse("<list type=\"TodoList\" title=\"T\" isCompleted=\"false\">"
                    + "<element type=\"BuyingTask\" title=\"t\" cost=\"1\" quantity=\"1\" unit=\"un\" "
                    + TASK_ATTRS + "/></list>");
            assertLoadFails();
        }

        @Test
        @DisplayName("Atributo obrigatório faltando deve lançar SaveReadException")
        void shouldRejectMissingAttribute() throws Exception {
            saveAndBreak("deadline=\"2026-11-30\"", "");

            assertTrue(assertLoadFails().getMessage().contains("deadline"));
        }

        @Test
        @DisplayName("Lembrete que não é nota, ou mais de um lembrete, deve lançar SaveReadException")
        void shouldRejectInvalidReminder() throws Exception {
            String openList = "<list type=\"TodoList\" title=\"T\" isCompleted=\"false\">";
            String openTask = "<element type=\"Task\" title=\"t\" " + TASK_ATTRS + ">";
            String note = "<element type=\"Note\" title=\"n\" creationDate=\"2026-11-19T08:30\"/>";
            String otherTask = "<element type=\"Task\" title=\"x\" " + TASK_ATTRS + "/>";
            String close = "</element></list>";

            // Tarefa com dois lembretes
            writeHouse(openList + openTask + note + note + close);
            assertLoadFails();

            // Tarefa cujo lembrete é outra tarefa
            writeHouse(openList + openTask + otherTask + close);
            assertLoadFails();
        }
    }

    @Nested
    @DisplayName("Valores Inválidos")
    class InvalidValueTests {

        @ParameterizedTest
        @ValueSource(strings = {"maybe", "TRUE", "1", ""})
        @DisplayName("Booleano diferente de true/false deve lançar SaveReadException")
        void shouldRejectInvalidBoolean(String value) throws Exception {
            saveAndBreak("isStarred=\"true\"", "isStarred=\"" + value + "\"");

            assertLoadFails();
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc", "NaN", "Infinity", "-1.0"})
        @DisplayName("Custo que não é número válido ou é negativo deve lançar SaveReadException")
        void shouldRejectInvalidCost(String value) throws Exception {
            saveAndBreak("cost=\"10.0\"", "cost=\"" + value + "\"");

            assertLoadFails();
        }

        @Test
        @DisplayName("Data inválida deve lançar SaveReadException")
        void shouldRejectInvalidDate() throws Exception {
            saveAndBreak("deadline=\"2026-11-30\"", "deadline=\"2026-13-40\"");
            assertLoadFails();

            saveAndBreak("creationDate=\"2026-11-19T08:30\"", "creationDate=\"ontem\"");
            assertLoadFails();
        }

        @Test
        @DisplayName("Estratégia de recorrência desconhecida, faltando ou com intervalo inválido deve lançar SaveReadException")
        void shouldRejectInvalidStrategy() throws Exception {
            saveAndBreak("strategyType=\"WeeklyRecurringStrategy\"", "strategyType=\"DailyRecurringStrategy\"");
            assertLoadFails();

            saveAndBreak("strategyType=\"WeeklyRecurringStrategy\"", "");
            assertTrue(assertLoadFails().getMessage().contains("sem estratégia"));

            saveAndBreak("strategyIntervalDays=\"3\"", "strategyIntervalDays=\"0\"");
            assertLoadFails();
        }

        @Test
        @DisplayName("Dado que quebra uma regra do modelo deve lançar SaveReadException com a causa")
        void shouldRejectInvalidModelData() throws Exception {
            saveAndBreak("unit=\"kg\"", "unit=\" \"");
            assertInstanceOf(IllegalArgumentException.class, assertLoadFails().getCause());

            saveAndBreak("title=\"Lixo\"", "title=\"\"");
            assertInstanceOf(IllegalArgumentException.class, assertLoadFails().getCause());

            write("<house version=\"1.0\" name=\"  \"/>");
            assertInstanceOf(IllegalArgumentException.class, assertLoadFails().getCause());
        }
    }

    @Nested
    @DisplayName("Falhas ao Salvar")
    class SaveFailureTests {

        @Test
        @DisplayName("Salvar casa nula deve lançar SaveWriteException")
        void shouldRejectNullHouse() {
            assertThrows(SaveWriteException.class, () -> persistence.save(null));
        }

        @Test
        @DisplayName("Salvar numa pasta deve lançar SaveWriteException sem deixar temporário")
        void shouldRejectDirectoryPath() throws Exception {
            Path dir = Files.createDirectory(folder.resolve("pasta"));

            assertThrows(SaveWriteException.class,
                    () -> new HousePersistence(dir).save(TestHouses.full()));

            // A pasta de teste deve ter só a subpasta, sem nenhum .tmp
            assertArrayEquals(new String[]{"pasta"}, folder.toFile().list());
        }

        @Test
        @DisplayName("Tipo que o leitor não conhece deve ser recusado ao salvar, mantendo o save anterior")
        void shouldRejectUnsupportedType() throws Exception {
            persistence.save(TestHouses.full());
            String previous = Files.readString(file);

            House house = TestHouses.empty();
            NoteList<Note> notes = new NoteList<>("Avisos", null);
            notes.addElement(new UnknownNote());
            house.addList(notes);

            assertThrows(SaveWriteException.class, () -> persistence.save(house));
            assertEquals(previous, Files.readString(file));
        }

        @Test
        @DisplayName("Falha de escrita no disco não deve estragar o save anterior")
        void shouldKeepPreviousSaveOnWriteFailure() throws Exception {
            assumeTrue(folder.getFileSystem().supportedFileAttributeViews().contains("posix"),
                    "Permissões POSIX não existem no Windows");
            persistence.save(TestHouses.full());
            String previous = Files.readString(file);

            // Assim o save novo não consegue criar arquivo temporário
            folder.toFile().setWritable(false);
            try {
                assumeFalse(Files.isWritable(folder),
                        "Rodando como root a pasta continua gravável, então não dá para fazer a escrita falhar");

                assertThrows(SaveWriteException.class, () -> persistence.save(TestHouses.empty()));
                assertEquals(previous, Files.readString(file));
            } finally {
                folder.toFile().setWritable(true);
            }
        }
    }
}
