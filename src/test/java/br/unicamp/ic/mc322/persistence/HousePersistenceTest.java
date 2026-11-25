package br.unicamp.ic.mc322.persistence;

import br.unicamp.ic.mc322.model.BuyingTask;
import br.unicamp.ic.mc322.model.Element;
import br.unicamp.ic.mc322.model.Note;
import br.unicamp.ic.mc322.model.RecurringTask;
import br.unicamp.ic.mc322.model.Task;
import br.unicamp.ic.mc322.model.lists.CleaningRoutineList;
import br.unicamp.ic.mc322.model.lists.ElementList;
import br.unicamp.ic.mc322.model.lists.NoteList;
import br.unicamp.ic.mc322.model.lists.ShoppingList;
import br.unicamp.ic.mc322.model.lists.TodoList;
import br.unicamp.ic.mc322.model.House;
import br.unicamp.ic.mc322.strategy.recurring.IntervalRecurringStrategy;
import br.unicamp.ic.mc322.strategy.recurring.MonthlyRecurringStrategy;
import br.unicamp.ic.mc322.strategy.recurring.WeeklyRecurringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static br.unicamp.ic.mc322.persistence.TestHouses.CREATED;
import static br.unicamp.ic.mc322.persistence.TestHouses.DEADLINE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes HousePersistence: Salvar e Carregar")
public class HousePersistenceTest {

    private static final float DELTA = 0.0001f;

    @TempDir
    Path folder;

    private Path file;
    private HousePersistence persistence;

    @BeforeEach
    void setUp() {
        file = folder.resolve("house.xml");
        persistence = new HousePersistence(file);
    }

    private House saveAndLoad(House house) {
        persistence.save(house);
        return persistence.load();
    }

    @Nested
    @DisplayName("Arquivo de Save")
    class SaveFileTests {

        @Test
        @DisplayName("Sem save, saveExists é falso e não há data do último save")
        void shouldReportNoSave() {
            assertFalse(persistence.saveExists());
            assertTrue(persistence.lastSaveDate().isEmpty());
        }

        @Test
        @DisplayName("Deve criar o arquivo, inclusive as pastas que faltam")
        void shouldCreateFileAndFolders() throws Exception {
            HousePersistence nested = new HousePersistence(folder.resolve("a/b/house.xml"));

            nested.save(TestHouses.empty());

            assertTrue(nested.saveExists());
            assertTrue(nested.lastSaveDate().isPresent());
            String xml = Files.readString(nested.getFile());
            assertTrue(xml.contains("<house") && xml.contains("version=\"1.0\""), xml);
        }

        @Test
        @DisplayName("Não deve deixar arquivo temporário sobrando na pasta")
        void shouldNotLeaveTempFile() {
            persistence.save(TestHouses.full());

            assertArrayEquals(new String[]{"house.xml"}, folder.toFile().list());
        }

        @Test
        @DisplayName("Salvar de novo deve substituir o save anterior")
        void shouldReplacePreviousSave() {
            persistence.save(TestHouses.full());
            persistence.save(TestHouses.empty());

            House loaded = persistence.load();
            assertEquals("República Batuta", loaded.getName());
            assertTrue(loaded.getLists().isEmpty());
        }
    }

    @Nested
    @DisplayName("Casa e Listas")
    class HouseAndListTests {

        @Test
        @DisplayName("Casa vazia deve voltar sem listas")
        void shouldRoundTripEmptyHouse() {
            House loaded = saveAndLoad(TestHouses.empty());

            assertEquals("República Batuta", loaded.getName());
            assertTrue(loaded.getLists().isEmpty());
        }

        @Test
        @DisplayName("Nome com acentos e caracteres especiais do XML deve voltar igual")
        void shouldRoundTripSpecialCharacters() {
            assertEquals("Rep & <Casa> \"Azul\"", saveAndLoad(TestHouses.full()).getName());
        }

        @Test
        @DisplayName("Listas devem voltar na mesma ordem, com tipo, título, descrição e conclusão")
        void shouldRoundTripLists() {
            List<ElementList<?>> lists = saveAndLoad(TestHouses.full()).getLists();

            assertEquals(4, lists.size());
            assertAll(
                    () -> assertInstanceOf(NoteList.class, lists.get(0)),
                    () -> assertEquals("Avisos", lists.get(0).getTitle()),
                    () -> assertEquals("Combinados da casa", lists.get(0).getDesc()),
                    () -> assertInstanceOf(TodoList.class, lists.get(1)),
                    () -> assertNull(lists.get(1).getDesc()),
                    () -> assertTrue(((TodoList<?>) lists.get(1)).isCompleted()),
                    () -> assertInstanceOf(ShoppingList.class, lists.get(2)),
                    () -> assertFalse(((TodoList<?>) lists.get(2)).isCompleted()),
                    () -> assertInstanceOf(CleaningRoutineList.class, lists.get(3)));
        }

        @Test
        @DisplayName("Lista carregada deve voltar com a ordenação padrão")
        void shouldLoadWithDefaultSorting() {
            ElementList<?> notes = saveAndLoad(TestHouses.full()).getLists().get(0);

            assertFalse(notes.isDescending());
            assertEquals("Lixo", notes.getSortedElements().get(0).getTitle());
        }

        @Test
        @DisplayName("Carregar duas vezes deve dar casas independentes")
        void shouldLoadIndependentCopies() {
            persistence.save(TestHouses.full());

            House first = persistence.load();
            House second = persistence.load();

            assertNotSame(first.getLists().get(0), second.getLists().get(0));
            first.getLists().get(0).clearList();
            assertEquals(2, second.getLists().get(0).getElements().size());
        }
    }

    @Nested
    @DisplayName("Elementos")
    class ElementTests {

        private List<ElementList<?>> lists;

        @BeforeEach
        void setUp() {
            lists = saveAndLoad(TestHouses.full()).getLists();
        }

        @Test
        @DisplayName("Notas devem voltar com título, descrição opcional e data de criação")
        void shouldRoundTripNotes() {
            List<? extends Element> notes = lists.get(0).getElements();

            assertEquals(2, notes.size());
            assertAll(
                    () -> assertInstanceOf(Note.class, notes.get(0)),
                    () -> assertEquals("Senha Wi-Fi", notes.get(0).getTitle()),
                    () -> assertEquals("batuta123", notes.get(0).getDesc()),
                    () -> assertEquals(CREATED, notes.get(0).getCreationDate()),
                    () -> assertEquals("Lixo", notes.get(1).getTitle()),
                    () -> assertEquals(CREATED.minusDays(1), notes.get(1).getCreationDate()),
                    () -> assertNull(notes.get(1).getDesc()));
        }

        @Test
        @DisplayName("Tarefas devem voltar com prazo, estrela, conclusão e lembrete")
        void shouldRoundTripTasks() {
            Task bill = (Task) lists.get(1).getElements().get(0);
            Task rent = (Task) lists.get(1).getElements().get(1);

            assertAll(
                    () -> assertEquals(Task.class, bill.getClass()),
                    () -> assertEquals("Pagar luz", bill.getTitle()),
                    () -> assertEquals("Vence dia 30", bill.getDesc()),
                    () -> assertEquals(DEADLINE, bill.getDeadline()),
                    () -> assertEquals(CREATED, bill.getCreationDate()),
                    () -> assertTrue(bill.isStarred()),
                    () -> assertFalse(bill.isCompleted()),
                    () -> assertEquals("Boleto", bill.getNote().getTitle()),
                    () -> assertEquals("Está no e-mail", bill.getNote().getDesc()),
                    () -> assertEquals(CREATED, bill.getNote().getCreationDate()),
                    () -> assertNull(rent.getDesc()),
                    () -> assertTrue(rent.isCompleted()),
                    () -> assertNull(rent.getNote()));
        }

        @Test
        @DisplayName("Itens de compra devem voltar com custo, quantidade, unidade e totais")
        void shouldRoundTripBuyingTasks() {
            ShoppingList shopping = (ShoppingList) lists.get(2);
            BuyingTask coffee = shopping.getElements().get(0);
            BuyingTask apple = shopping.getElements().get(1);
            BuyingTask bags = shopping.getElements().get(2);

            assertAll(
                    () -> assertEquals(10.0f, coffee.getCost(), DELTA),
                    () -> assertEquals(3.0f, coffee.getQuantity(), DELTA),
                    () -> assertEquals("pacotes", coffee.getUnit()),
                    () -> assertEquals(30.0f, coffee.getTotalCost(), DELTA),
                    () -> assertEquals(2.5f, apple.getQuantity(), DELTA),
                    () -> assertEquals("kg", apple.getUnit()),
                    () -> assertEquals(0.0f, bags.getCost(), DELTA),
                    () -> assertEquals(50.0f, shopping.getTotalCost(), DELTA));
        }

        @Test
        @DisplayName("Tarefas recorrentes devem voltar com a mesma estratégia e intervalo")
        void shouldRoundTripRecurringTasks() {
            List<RecurringTask> tasks = ((CleaningRoutineList) lists.get(3)).getElements();

            assertAll(
                    () -> assertInstanceOf(WeeklyRecurringStrategy.class, tasks.get(0).getCurrRecurringStrategy()),
                    () -> assertInstanceOf(MonthlyRecurringStrategy.class, tasks.get(1).getCurrRecurringStrategy()),
                    () -> assertEquals(3,
                            assertInstanceOf(IntervalRecurringStrategy.class, tasks.get(2).getCurrRecurringStrategy())
                                    .getIntervalDays()),
                    () -> assertEquals("Toda semana", tasks.get(0).getDesc()),
                    () -> assertEquals(DEADLINE, tasks.get(2).getDeadline()));
        }
    }
}
