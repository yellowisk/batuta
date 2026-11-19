package br.unicamp.ic.mc322.model;

import br.unicamp.ic.mc322.strategy.IntervalRecurringStrategy;
import br.unicamp.ic.mc322.strategy.MonthlyRecurringStrategy;
import br.unicamp.ic.mc322.strategy.RecurringStrategy;
import br.unicamp.ic.mc322.strategy.WeeklyRecurringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes RecurringTask")
public class RecurringTaskTest {

    private RecurringStrategy weekly;
    private LocalDate deadline;
    private RecurringTask washDishes;

    @BeforeEach
    void setUp() {
        weekly = new WeeklyRecurringStrategy();
        deadline = LocalDate.of(2026, 11, 19);
        washDishes = new RecurringTask("Lavar louça", "Lavar e secar depois", deadline, weekly);
    }

    @Nested
    @DisplayName("Construtor e Inicialização")
    class ConstructorTests {

        @Test
        @DisplayName("Deve inicializar com os dados da tarefa e a estratégia de recorrência")
        void shouldInitializeRecurringTask() {
            assertEquals("Lavar louça", washDishes.getTitle());
            assertEquals("Lavar e secar depois", washDishes.getDesc());
            assertEquals(deadline, washDishes.getDeadline());

            assertSame(weekly, washDishes.getCurrRecurringStrategy());

            assertTrue(washDishes instanceof Task);

            assertThrows(IllegalArgumentException.class,
                    () -> new RecurringTask("Lavar louça", "Lavar toda a louça da pia", deadline, null));
            assertThrows(IllegalArgumentException.class,
                    () -> new RecurringTask("", "Lavar toda a louça da pia", deadline, weekly));
        }
    }

    @Nested
    @DisplayName("Troca de Estratégia de Recorrência")
    class StrategySwapTests {

        @Test
        @DisplayName("Deve trocar a estratégia e barrar estratégia nula mantendo a anterior")
        void shouldSwapStrategy() {
            RecurringStrategy m = new MonthlyRecurringStrategy();
            washDishes.setCurrRecurringStrategy(m);

            assertSame(m, washDishes.getCurrRecurringStrategy());

            assertThrows(IllegalArgumentException.class,
                    () -> washDishes.setCurrRecurringStrategy(null));
            assertSame(m, washDishes.getCurrRecurringStrategy());
        }
    }

    @Nested
    @DisplayName("Cálculo da Próxima Ocorrência a partir do Prazo")
    class NextOccurrenceTests {

        @Test
        @DisplayName("A próxima ocorrência deve mudar conforme a estratégia atual da tarefa")
        void shouldComputeNextOccurrenceFromStrategy() {
            // semanal: 19/11 -> 26/11
            assertEquals(LocalDate.of(2026, 11, 26),
                    washDishes.getCurrRecurringStrategy().getNextOccurrence(washDishes.getDeadline()));

            // mensal: 19/11 -> 19/12
            washDishes.setCurrRecurringStrategy(new MonthlyRecurringStrategy());
            assertEquals(LocalDate.of(2026, 12, 19),
                    washDishes.getCurrRecurringStrategy().getNextOccurrence(washDishes.getDeadline()));

            // a cada 3 dias: 19/11 -> 22/11
            washDishes.setCurrRecurringStrategy(new IntervalRecurringStrategy(3));
            assertEquals(LocalDate.of(2026, 11, 22),
                    washDishes.getCurrRecurringStrategy().getNextOccurrence(washDishes.getDeadline()));
        }
    }
}
