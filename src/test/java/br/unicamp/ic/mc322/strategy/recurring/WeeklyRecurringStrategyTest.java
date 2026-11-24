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

@DisplayName("Testes WeeklyRecurringStrategy")
public class WeeklyRecurringStrategyTest {

    private WeeklyRecurringStrategy w;

    @BeforeEach
    void setUp() {
        w = new WeeklyRecurringStrategy();
    }

    @Nested
    @DisplayName("Próxima Ocorrência")
    class NextOccurrenceTests {

        @Test
        @DisplayName("Deve somar uma semana mantendo o dia da semana")
        void shouldAddOneWeek() {
            LocalDate thursday = LocalDate.of(2026, 11, 19);
            LocalDate next = w.getNextOccurrence(thursday);

            assertEquals(LocalDate.of(2026, 11, 26), next);
            assertEquals(thursday.getDayOfWeek(), next.getDayOfWeek());
            assertEquals(LocalDate.of(2026, 12, 3),
                    w.getNextOccurrence(LocalDate.of(2026, 11, 26)));

            assertThrows(IllegalArgumentException.class,
                    () -> w.getNextOccurrence(null));
        }
    }

    @Nested
    @DisplayName("Próximas Ocorrências")
    class NextOccurrencesTests {

        @Test
        @DisplayName("Deve gerar as próximas semanas, inclusive atravessando a virada de ano")
        void shouldGenerateNextWeeks() {
            List<LocalDate> occurrences = w.getNextOccurrences(
                    LocalDate.of(2026, 12, 24), 3);

            assertEquals(List.of(
                    LocalDate.of(2026, 12, 31),
                    LocalDate.of(2027, 1, 7),
                    LocalDate.of(2027, 1, 14)), occurrences);

            assertTrue(w.getNextOccurrences(
                    LocalDate.of(2026, 12, 24), 0).isEmpty());

            assertThrows(IllegalArgumentException.class,
                    () -> w.getNextOccurrences(null, 3));
            assertThrows(IllegalArgumentException.class,
                    () -> w.getNextOccurrences(LocalDate.of(2026, 12, 24), -1));
        }
    }
}
