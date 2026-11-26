package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Task;
import br.unicamp.ic.mc322.model.lists.TodoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes DeadlineSortingStrategy")
public class DeadlineSortingStrategyTest {

    private DeadlineSortingStrategy strategy;
    private TodoList<Task> list;
    private Task taskSoonest;
    private Task taskMiddle;
    private Task taskLatest;

    @BeforeEach
    void setUp() {
        strategy = new DeadlineSortingStrategy();
        list = new TodoList<>("Titulo teste", "Desc teste");
        list.setCurrSortingStrategy(strategy);

        taskSoonest = new Task("Tarefa 1", "Desc 1", LocalDate.of(2026, 3, 10));
        taskMiddle = new Task("Tarefa 2", "Desc 2", LocalDate.of(2026, 3, 15));
        taskLatest = new Task("Tarefa 3", "Desc 3", LocalDate.of(2026, 3, 20));
    }

    @Nested
    @DisplayName("Ordenação e Empates")
    class OrderingTests {

        @Test
        @DisplayName("Deve ordenar tarefas pelo prazo (crescente e decrescente)")
        void mustSortByDeadline() {
            list.addElement(taskLatest);
            list.addElement(taskSoonest);
            list.addElement(taskMiddle);

            // CRESCENTE (prazo mais próximo -> mais distante)
            // esperado: [taskSoonest, taskMiddle, taskLatest]
            assertEquals(List.of(taskSoonest, taskMiddle, taskLatest), list.getSortedElements());

            // DECRESCENTE (prazo mais distante -> mais próximo)
            // esperado: [taskLatest, taskMiddle, taskSoonest]
            list.toggleSortOrder();
            assertEquals(List.of(taskLatest, taskMiddle, taskSoonest), list.getSortedElements());
        }

        @Test
        @DisplayName("Deve manter a ordem de inserção quando houver empate no prazo")
        void mustPreserveInsertionOrderOnTie() {
            LocalDate deadline = LocalDate.of(2026, 4, 1);

            Task firstInserted = new Task("Primeira", "Desc", deadline);
            Task secondInserted = new Task("Segunda", "Desc", deadline);

            list.addElement(firstInserted);
            list.addElement(secondInserted);
            list.addElement(taskSoonest);

            // esperado: [taskSoonest, firstInserted, secondInserted]
            assertEquals(List.of(taskSoonest, firstInserted, secondInserted), list.getSortedElements());
        }
    }

    @Nested
    @DisplayName("Independência entre Listas")
    class SharedStrategyTests {

        @Test
        @DisplayName("Inverter uma lista não deve afetar outra lista que usa a mesma estratégia")
        void mustNotAffectOtherListSharingSameStrategy() {
            TodoList<Task> list2 = new TodoList<>("Outra lista", "Desc");
            list2.setCurrSortingStrategy(strategy);

            list.addElement(taskLatest);
            list.addElement(taskSoonest);

            list2.addElement(taskLatest);
            list2.addElement(taskSoonest);

            // Inverte apenas a primeira lista
            list.toggleSortOrder();

            assertEquals(List.of(taskLatest, taskSoonest), list.getSortedElements()); // decrescente
            assertEquals(List.of(taskSoonest, taskLatest), list2.getSortedElements()); // mantém crescente
        }
    }
}