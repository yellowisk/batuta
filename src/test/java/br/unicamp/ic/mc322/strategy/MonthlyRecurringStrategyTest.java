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

@DisplayName("Testes MonthlyRecurringStrategy")
public class MonthlyRecurringStrategyTest {

    private MonthlyRecurringStrategy m;

    @BeforeEach
    void setUp() {
        m = new MonthlyRecurringStrategy();
    }

    @Nested
    @DisplayName("Próxima Ocorrência")
    class NextOccurrenceTests {

        @Test
        @DisplayName("Deve somar um mês mantendo o dia do mês")
        void shouldAddOneMonth() {
            assertEquals(LocalDate.of(2026, 12, 19),
                    m.getNextOccurrence(LocalDate.of(2026, 11, 19)));
            // Virada de ano
            assertEquals(LocalDate.of(2027, 1, 10),
                    m.getNextOccurrence(LocalDate.of(2026, 12, 10)));

            assertThrows(IllegalArgumentException.class,
                    () -> m.getNextOccurrence(null));
        }

        @Test
        @DisplayName("Deve usar o último dia do mês quando o mês seguinte for mais curto")
        void shouldClampToLastDayOfMonth() {
            // 31/01 -> 28/02 (ano comum)
            assertEquals(LocalDate.of(2027, 2, 28),
                    m.getNextOccurrence(LocalDate.of(2027, 1, 31)));

            // 31/01 -> 29/02 (ano bissexto)
            assertEquals(LocalDate.of(2028, 2, 29),
                    m.getNextOccurrence(LocalDate.of(2028, 1, 31)));

            // 31/03 -> 30/04
            assertEquals(LocalDate.of(2027, 4, 30),
                    m.getNextOccurrence(LocalDate.of(2027, 3, 31)));
        }
    }

    @Nested
    @DisplayName("Próximas Ocorrências")
    class NextOccurrencesTests {

        @Test
        @DisplayName("Deve gerar os próximos meses sem perder o dia original após um mês mais curto")
        void shouldKeepOriginalDay() {
            List<LocalDate> occurrences = m.getNextOccurrences(
                    LocalDate.of(2027, 1, 31), 4);

            assertEquals(List.of(
                    LocalDate.of(2027, 2, 28),
                    LocalDate.of(2027, 3, 31),
                    LocalDate.of(2027, 4, 30),
                    LocalDate.of(2027, 5, 31)), occurrences);

            assertTrue(m.getNextOccurrences(
                    LocalDate.of(2027, 1, 31), 0).isEmpty());

            assertThrows(IllegalArgumentException.class,
                    () -> m.getNextOccurrences(null, 3));
            assertThrows(IllegalArgumentException.class,
                    () -> m.getNextOccurrences(LocalDate.of(2027, 1, 31), -1));
        }
    }
}
