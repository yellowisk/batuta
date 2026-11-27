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
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes NoteList")
public class NoteListTest {

    private NoteList<Note> list;
    private Note noteOldest;
    private Note noteMiddle;
    private Note noteNewest;

    @BeforeEach
    void setUp() {
        list = new NoteList<>("Notas", "");

        noteOldest = new Note("Nota C", "Desc C");
        noteOldest.setCreationDate(LocalDateTime.of(2026, 1, 10, 10, 0));

        noteMiddle = new Note("Nota A", "Desc A");
        noteMiddle.setCreationDate(LocalDateTime.of(2026, 1, 15, 10, 0));

        noteNewest = new Note("Nota B", "Desc B");
        noteNewest.setCreationDate(LocalDateTime.of(2026, 1, 20, 10, 0));
    }

    @Nested
    @DisplayName("Operações de CRUD")
    class CrudAndSortingTests {

        @Test
        @DisplayName("Deve adicionar e remover notas corretamente")
        void mustAddAndRemoveNotes() {
            list.addElement(noteOldest);
            list.addElement(noteNewest);

            assertEquals(List.of(noteOldest, noteNewest), list.getElements());

            // inicial: [noteOldest, noteNewest]
            // esperado: [noteNewest]
            list.removeElement(0);
            assertEquals(List.of(noteNewest), list.getElements());

            list.clearList();
            assertTrue(list.getElements().isEmpty());
        }
    }

    @Nested
    @DisplayName("Ordenação")
    class SortingTests {

        @Test
        @DisplayName("Deve ordenar notas por data de criação e por título sem alterar a lista original")
        void mustSortNotesWithoutMutatingOriginalList() {
            list.addElement(noteNewest);
            list.addElement(noteOldest);
            list.addElement(noteMiddle);

            // Ordenação: data de criação crescente
            // esperado: [noteOldest, noteMiddle, noteNewest]
            assertEquals(List.of(noteOldest, noteMiddle, noteNewest), list.getSortedElements());

            // Ordenação: título crescente
            // esperado: [Nota A, Nota B, Nota C]
            list.setCurrSortingStrategy(new TitleSortingStrategy());
            assertEquals(List.of(noteMiddle, noteNewest, noteOldest), list.getSortedElements());

            // Lista original permanece na ordem de inserção
            assertEquals(List.of(noteNewest, noteOldest, noteMiddle), list.getElements());
        }
    }
}