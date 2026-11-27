package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Note;
import br.unicamp.ic.mc322.model.lists.NoteList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes DateSortingStrategy")
public class DateSortingStrategyTest {

    private DateSortingStrategy strategy;
    private NoteList<Note> list;
    private Note noteOldest;
    private Note noteMiddle;
    private Note noteNewest;

    @BeforeEach
    void setUp() {
        strategy = new DateSortingStrategy();
        list = new NoteList<>("Titulo teste", "Desc teste");
        list.setCurrSortingStrategy(strategy);

        noteOldest = new Note("Nota 1", "Desc 1");
        noteOldest.setCreationDate(LocalDateTime.of(2026, 1, 10, 10, 0));

        noteMiddle = new Note("Nota 2", "Desc 2");
        noteMiddle.setCreationDate(LocalDateTime.of(2026, 1, 15, 10, 0));

        noteNewest = new Note("Nota 3", "Desc 3");
        noteNewest.setCreationDate(LocalDateTime.of(2026, 1, 20, 10, 0));
    }

    @Nested
    @DisplayName("Ordenação")
    class OrderingTests {

        @Test
        @DisplayName("Deve ordenar elementos pela data de criação (crescente e decrescente)")
        void mustSortByCreationDate() {
            list.addElement(noteNewest);
            list.addElement(noteOldest);
            list.addElement(noteMiddle);

            // CRESCENTE (mais antigo -> mais recente)
            // esperado: [noteOldest, noteMiddle, noteNewest]
            assertEquals(List.of(noteOldest, noteMiddle, noteNewest), list.getSortedElements());

            // DECRESCENTE (mais recente -> mais antigo)
            // esperado: [noteNewest, noteMiddle, noteOldest]
            list.toggleSortOrder();
            assertEquals(List.of(noteNewest, noteMiddle, noteOldest), list.getSortedElements());
        }

        @Test
        @DisplayName("Deve manter a ordem de inserção quando houver empate na data de criação")
        void mustPreserveInsertionOrderOnTie() {
            LocalDateTime date = LocalDateTime.of(2026, 2, 1, 12, 0);

            Note firstInserted = new Note("Primeira", "Desc");
            firstInserted.setCreationDate(date);

            Note secondInserted = new Note("Segunda", "Desc");
            secondInserted.setCreationDate(date);

            list.addElement(firstInserted);
            list.addElement(secondInserted);
            list.addElement(noteOldest);

            // esperado: [noteOldest, firstInserted, secondInserted]
            assertEquals(List.of(noteOldest, firstInserted, secondInserted), list.getSortedElements());
        }
    }

    @Nested
    @DisplayName("Independência entre Listas")
    class SharedStrategyTests {

        @Test
        @DisplayName("Inverter uma lista não deve afetar outra lista que usa a mesma estratégia")
        void mustNotAffectOtherListSharingSameStrategy() {
            NoteList<Note> list2 = new NoteList<>("Lista 2", "Desc");
            list2.setCurrSortingStrategy(strategy);

            list.addElement(noteNewest);
            list.addElement(noteOldest);

            list2.addElement(noteNewest);
            list2.addElement(noteOldest);

            // Inverte apenas a primeira lista
            list.toggleSortOrder();

            assertEquals(List.of(noteNewest, noteOldest), list.getSortedElements()); // decrescente
            assertEquals(List.of(noteOldest, noteNewest), list2.getSortedElements()); // mantém crescente
        }
    }
}