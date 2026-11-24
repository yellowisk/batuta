package br.unicamp.ic.mc322.strategy.recurring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes Contrato da Interface RecurringStrategy")
public class RecurringStrategyTest {

    private List<RecurringStrategy> strategies;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        strategies = List.of(
                new WeeklyRecurringStrategy(),
                new MonthlyRecurringStrategy(),
                new IntervalRecurringStrategy(3));
        today = LocalDate.of(2026, 11, 19);
    }

    @Nested
    @DisplayName("Próxima Ocorrência")
    class NextOccurrenceTests {

        @Test
        @DisplayName("Toda estratégia deve retornar uma data estritamente depois da data de referência")
        void shouldReturnLaterDate() {
            for (RecurringStrategy strategy : strategies) {
                assertTrue(strategy.getNextOccurrence(today).isAfter(today));

                assertThrows(IllegalArgumentException.class,
                        () -> strategy.getNextOccurrence(null));
            }
        }
    }

    @Nested
    @DisplayName("Próximas Ocorrências - getNextOccurrences()")
    class NextOccurrencesTests {

        @Test
        @DisplayName("Deve retornar count datas em ordem crescente, começando pela próxima ocorrência")
        void shouldReturnOccurrencesInOrder() {
            for (RecurringStrategy strategy : strategies) {
                List<LocalDate> occurrences = strategy.getNextOccurrences(today, 5);

                assertEquals(5, occurrences.size());
                assertEquals(strategy.getNextOccurrence(today), occurrences.get(0));

                for (int i = 1; i < occurrences.size(); i++)
                    assertTrue(occurrences.get(i).isAfter(occurrences.get(i - 1)));

                assertTrue(strategy.getNextOccurrences(today, 0).isEmpty());
            }
        }

        @Test
        @DisplayName("Deve barrar data nula e quantidade negativa")
        void shouldRejectInvalidArguments() {
            for (RecurringStrategy strategy : strategies) {
                assertThrows(IllegalArgumentException.class,
                        () -> strategy.getNextOccurrences(null, 1));
                assertThrows(IllegalArgumentException.class,
                        () -> strategy.getNextOccurrences(today, -1));
            }
        }
    }
}
