package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Note;
import br.unicamp.ic.mc322.model.lists.NoteList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes TitleSortingStrategy")
public class TitleSortingStrategyTest {

    private TitleSortingStrategy strategy;
    private NoteList<Note> list;
    private Note noteA;
    private Note noteB;
    private Note noteC;

    @BeforeEach
    void setUp() {
        strategy = new TitleSortingStrategy();
        list = new NoteList<>("Titulo teste", "Desc teste");
        list.setCurrSortingStrategy(strategy);

        noteA = new Note("NOTA A", "Desc A");
        noteB = new Note("nota b", "Desc B");
        noteC = new Note("nOtA c", "Desc C");
    }

    @Nested
    @DisplayName("Ordenação e Empates")
    class OrderingTests {

        @Test
        @DisplayName("Deve ordenar elementos alfabeticamente ignorando maiúsculas/minúsculas (crescente e decrescente)")
        void mustSortByTitleIgnoringCase() {
            list.addElement(noteC);
            list.addElement(noteA);
            list.addElement(noteB);

            // CRESCENTE
            // esperado: [NOTA A, nota b, nOtA c]
            assertEquals(List.of(noteA, noteB, noteC), list.getSortedElements());

            // DECRESCENTE (Z -> A)
            // esperado: [nOtA c, nota b, NOTA A]
            list.toggleSortOrder();
            assertEquals(List.of(noteC, noteB, noteA), list.getSortedElements());
        }

        @Test
        @DisplayName("Deve manter a ordem de inserção quando houver empate no título")
        void mustPreserveInsertionOrderOnTie() {
            Note firstInserted = new Note("Mesmo titulo", "Desc 1");
            Note secondInserted = new Note("mesmo titulo", "Desc 2");

            list.addElement(noteC);
            list.addElement(firstInserted);
            list.addElement(secondInserted);

            // esperado: [nOtA c, firstInserted, secondInserted]
            assertEquals(List.of(noteC, firstInserted, secondInserted), list.getSortedElements());
        }
    }

    @Nested
    @DisplayName("Independência entre Listas")
    class SharedStrategyTests {

        @Test
        @DisplayName("Inverter uma lista não deve afetar outra lista que usa a mesma estratégia")
        void mustNotAffectOtherListSharingSameStrategy() {
            NoteList<Note> list2 = new NoteList<>("Outra lista", "Desc");
            list2.setCurrSortingStrategy(strategy);

            list.addElement(noteC);
            list.addElement(noteA);

            list2.addElement(noteC);
            list2.addElement(noteA);

            // Inverte apenas a primeira lista
            list.toggleSortOrder();

            assertEquals(List.of(noteC, noteA), list.getSortedElements()); // decrescente
            assertEquals(List.of(noteA, noteC), list2.getSortedElements()); // mantém crescente
        }
    }
}