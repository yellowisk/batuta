package br.unicamp.ic.mc322.model;

import br.unicamp.ic.mc322.model.lists.ElementList;
import br.unicamp.ic.mc322.model.lists.NoteList;
import br.unicamp.ic.mc322.model.lists.ShoppingList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes House")
public class HouseTest {

    private House house;
    private NoteList<Note> notes;
    private ShoppingList shopping;

    @BeforeEach
    void setUp() {
        house = new House("República Batuta");
        notes = new NoteList<>("Avisos", null);
        shopping = new ShoppingList("Mercado", null);
    }

    @Nested
    @DisplayName("Nome")
    class NameTests {

        @Test
        @DisplayName("Deve criar a casa sem listas e barrar nome nulo ou vazio")
        void shouldRequireName() {
            assertEquals("República Batuta", house.getName());
            assertTrue(house.getLists().isEmpty());

            assertThrows(IllegalArgumentException.class, () -> new House(null));
            assertThrows(IllegalArgumentException.class, () -> new House(""));
            assertThrows(IllegalArgumentException.class, () -> new House("   "));
        }

        @Test
        @DisplayName("Deve renomear a casa e manter o nome anterior quando o novo é inválido")
        void shouldRenameHouse() {
            house.setName("Casa A8A");
            assertEquals("Casa A8A", house.getName());

            assertThrows(IllegalArgumentException.class, () -> house.setName(" "));
            assertEquals("Casa A8A", house.getName());
        }
    }

    @Nested
    @DisplayName("Listas")
    class ListTests {

        @Test
        @DisplayName("Deve adicionar e remover listas de tipos diferentes, mantendo a ordem")
        void shouldAddAndRemoveLists() {
            house.addList(notes);
            house.addList(shopping);
            assertEquals(List.of(notes, shopping), house.getLists());

            house.removeList(notes);
            assertEquals(List.of(shopping), house.getLists());
        }

        @Test
        @DisplayName("Deve barrar lista nula ou repetida")
        void shouldRejectNullOrDuplicateList() {
            house.addList(notes);

            assertThrows(IllegalArgumentException.class, () -> house.addList(null));
            assertThrows(IllegalArgumentException.class, () -> house.addList(notes));
            assertEquals(1, house.getLists().size());
        }

        @Test
        @DisplayName("Não deve permitir alterar as listas por fora da casa")
        void shouldNotExposeInternalList() {
            List<ElementList<?>> lists = house.getLists();

            assertThrows(UnsupportedOperationException.class, () -> lists.add(notes));
        }
    }
}
