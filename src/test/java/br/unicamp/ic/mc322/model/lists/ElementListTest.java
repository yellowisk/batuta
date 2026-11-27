package br.unicamp.ic.mc322.model.lists;

import br.unicamp.ic.mc322.model.Note;
import br.unicamp.ic.mc322.strategy.sorting.TitleSortingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes ElementList")
public class ElementListTest {

    private NoteList<Note> list;
    private Note noteOldest;
    private Note noteMiddle;
    private Note noteNewest;

    @BeforeEach
    void setUp() {
        list = new NoteList<>("Titulo teste", "Desc teste");

        noteOldest = new Note("Nota C", "Desc C");
        noteOldest.setCreationDate(LocalDateTime.of(2026, 1, 10, 10, 0));

        noteMiddle = new Note("Nota A", "Desc A");
        noteMiddle.setCreationDate(LocalDateTime.of(2026, 1, 15, 10, 0));

        noteNewest = new Note("Nota B", "Desc B");
        noteNewest.setCreationDate(LocalDateTime.of(2026, 1, 20, 10, 0));
    }

    @Nested
    @DisplayName("Operações de CRUD e Estado da Lista")
    class CrudAndStateTests {

        @Test
        @DisplayName("Deve adicionar elementos na ordem de inserção")
        void mustAddElementsCorrectly() {
            list.addElement(noteNewest);
            list.addElement(noteOldest);

            // esperado: [noteNewest, noteOldest]
            assertEquals(2, list.getElements().size());
            assertEquals(List.of(noteNewest, noteOldest), list.getElements());
        }

        @Test
        @DisplayName("Deve remover elemento pela posição exibida na lista ordenada (crescente e decrescente)")
        void mustRemoveElementBySortedPosition() {
            list.addElement(noteNewest);
            list.addElement(noteOldest);
            list.addElement(noteMiddle);

            // CRESCENTE
            // inicial: [noteOldest, noteMiddle, noteNewest]
            // esperado: [noteMiddle, noteNewest]
            list.removeElement(0);
            assertEquals(List.of(noteMiddle, noteNewest), list.getSortedElements());

            // DECRESCENTE
            // inicial: [noteNewest, noteMiddle]
            // esperado: [noteMiddle]
            list.toggleSortOrder();
            list.removeElement(0);
            assertEquals(List.of(noteMiddle), list.getSortedElements());

        }

        @Test
        @DisplayName("Deve limpar os elementos mantendo o título e a descrição da lista")
        void mustClearListKeepingTitleAndDesc() {
            list.addElement(noteOldest);
            list.addElement(noteMiddle);

            list.clearList();

            assertTrue(list.getElements().isEmpty());
            assertEquals("Titulo teste", list.getTitle());
            assertEquals("Desc teste", list.getDesc());
        }

        @Test
        @DisplayName("Deve permitir exclusão apenas quando a lista estiver vazia")
        void mustAllowDeletionOnlyWhenEmpty() {
            assertTrue(list.canBeDeleted());

            list.addElement(noteOldest);
            assertFalse(list.canBeDeleted());

            list.clearList();
            assertTrue(list.canBeDeleted());
        }
    }

    @Nested
    @DisplayName("Ordenação e Direção")
    class SortingTests {

        @Test
        @DisplayName("Deve usar data de criação crescente como ordenação padrão sem alterar a lista original")
        void mustSortByCreationDateAscendingByDefault() {
            list.addElement(noteNewest);
            list.addElement(noteOldest);
            list.addElement(noteMiddle);

            assertFalse(list.isDescending());

            assertEquals(List.of(noteOldest, noteMiddle, noteNewest), list.getSortedElements()); // ordem cronológica
            assertEquals(List.of(noteNewest, noteOldest, noteMiddle), list.getElements()); // ordem inserção
        }

        @Test
        @DisplayName("Deve manter o critério e a direção escolhidos para elementos adicionados depois")
        void mustApplyCurrentStrategyAndDirectionToNewlyAddedElements() {
            list.addElement(noteMiddle); // "Nota A"
            list.addElement(noteOldest); // "Nota C"

            // Ordenação alfabética decrescente
            list.setCurrSortingStrategy(new TitleSortingStrategy());
            list.toggleSortOrder();

            // inicial: [Nota A, Nota C]
            // esperado: [Nota C, Nota A]
            assertEquals(List.of(noteOldest, noteMiddle), list.getSortedElements());

            list.addElement(noteNewest); // "Nota B"

            // esperado: [Nota C, Nota B, Nota A]
            assertEquals(List.of(noteOldest, noteNewest, noteMiddle), list.getSortedElements());
        }
    }
}