package br.unicamp.ic.mc322.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes IntervalRecurringStrategy")
public class IntervalRecurringStrategyTest {

    private IntervalRecurringStrategy everyThreeDays;

    @BeforeEach
    void setUp() {
        everyThreeDays = new IntervalRecurringStrategy(3);
    }

    @Nested
    @DisplayName("Construtor e Inicialização")
    class ConstructorTests {

        @Test
        @DisplayName("Deve guardar o intervalo e barrar intervalos menores que 1 dia")
        void shouldInitializeInterval() {
            assertEquals(3, everyThreeDays.getIntervalDays());
            assertEquals(1, new IntervalRecurringStrategy(1).getIntervalDays());

            assertThrows(IllegalArgumentException.class,
                    () -> new IntervalRecurringStrategy(0));
            assertThrows(IllegalArgumentException.class,
                    () -> new IntervalRecurringStrategy(-5));
        }
    }

    @Nested
    @DisplayName("Próxima Ocorrência")
    class NextOccurrenceTests {

        @Test
        @DisplayName("Deve somar o intervalo de dias, inclusive atravessando o mês")
        void shouldAddInterval() {
            assertEquals(LocalDate.of(2026, 11, 22),
                    everyThreeDays.getNextOccurrence(LocalDate.of(2026, 11, 19)));
            assertEquals(LocalDate.of(2026, 12, 2),
                    everyThreeDays.getNextOccurrence(LocalDate.of(2026, 11, 29)));

            assertThrows(IllegalArgumentException.class,
                    () -> everyThreeDays.getNextOccurrence(null));
        }
    }

    @Nested
    @DisplayName("Próximas Ocorrências")
    class NextOccurrencesTests {

        @Test
        @DisplayName("Deve gerar as próximas datas separadas pelo intervalo")
        void shouldGenerateSpacedDates() {
            List<LocalDate> occurrences = everyThreeDays.getNextOccurrences(
                    LocalDate.of(2026, 11, 19), 4);

            assertEquals(List.of(
                    LocalDate.of(2026, 11, 22),
                    LocalDate.of(2026, 11, 25),
                    LocalDate.of(2026, 11, 28),
                    LocalDate.of(2026, 12, 1)), occurrences);

            assertTrue(everyThreeDays.getNextOccurrences(
                    LocalDate.of(2026, 11, 19), 0).isEmpty());

            assertThrows(IllegalArgumentException.class,
                    () -> everyThreeDays.getNextOccurrences(null, 3));
            assertThrows(IllegalArgumentException.class,
                    () -> everyThreeDays.getNextOccurrences(LocalDate.of(2026, 11, 19), -1));
        }
    }
}
