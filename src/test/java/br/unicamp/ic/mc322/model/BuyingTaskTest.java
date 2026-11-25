package br.unicamp.ic.mc322.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes BuyingTask")
public class BuyingTaskTest {

    private static final float DELTA = 0.0001f;

    private LocalDate deadline;
    private BuyingTask coffee;

    @BeforeEach
    void setUp() {
        deadline = LocalDate.of(2026, 11, 30);
        coffee = new BuyingTask("Comprar café", "3 Corações se tiver", deadline, 35.50f, 2.0f, "pacotes");
    }

    @Nested
    @DisplayName("Construtor e Inicialização")
    class ConstructorTests {

        @Test
        @DisplayName("Deve inicializar com os dados do item e calcular o custo total")
        void shouldInitializeBuyingTask() {
            assertEquals("Comprar café", coffee.getTitle());
            assertEquals("3 Corações se tiver", coffee.getDesc());
            assertEquals(deadline, coffee.getDeadline());
            assertEquals(35.50f, coffee.getCost(), DELTA);
            assertEquals(2.0f, coffee.getQuantity(), DELTA);
            assertEquals("pacotes", coffee.getUnit());
            assertEquals(71.00f, coffee.getTotalCost(), DELTA);
            assertFalse(coffee.isCompleted());
            assertFalse(coffee.isStarred());

            assertNull(new BuyingTask("Sacolas", null, deadline, 1.0f, 1.0f, "un").getDesc());
        }

        @Test
        @DisplayName("Deve barrar custo, quantidade, unidade, título e prazo inválidos")
        void shouldRejectInvalidArguments() {
            // Custo negativo ou que não é número
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, -1.0f, 2.0f, "un"));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, Float.NaN, 2.0f, "un"));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, Float.POSITIVE_INFINITY, 2.0f, "un"));

            // Quantidade zero, negativa ou que não é número
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, 10.0f, 0.0f, "un"));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, 10.0f, -2.5f, "un"));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, 10.0f, Float.NaN, "un"));

            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, 10.0f, 1.0f, null));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, deadline, 10.0f, 1.0f, " "));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask(" ", null, deadline, 10.0f, 1.0f, "un"));
            assertThrows(IllegalArgumentException.class,
                    () -> new BuyingTask("Item", null, null, 10.0f, 1.0f, "un"));
        }
    }

    @Nested
    @DisplayName("Custo")
    class CostTests {

        @Test
        @DisplayName("Deve recalcular o total ao trocar o custo")
        void shouldRecalculateTotalWhenCostChanges() {
            coffee.setCost(40.0f);

            assertEquals(40.0f, coffee.getCost(), DELTA);
            assertEquals(80.0f, coffee.getTotalCost(), DELTA);
        }

        @Test
        @DisplayName("Deve aceitar custo zero")
        void shouldAcceptZeroCost() {
            BuyingTask bags = new BuyingTask("Sacolas", null, deadline, 0.0f, 3.0f, "un");
            assertEquals(0.0f, bags.getTotalCost(), DELTA);

            coffee.setCost(0.0f);
            assertEquals(0.0f, coffee.getTotalCost(), DELTA);
        }

        @Test
        @DisplayName("Deve barrar custo inválido mantendo custo e total anteriores")
        void shouldRejectInvalidCost() {
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setCost(-0.01f));
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setCost(Float.NaN));

            assertEquals(35.50f, coffee.getCost(), DELTA);
            assertEquals(71.00f, coffee.getTotalCost(), DELTA);
        }
    }

    @Nested
    @DisplayName("Quantidade")
    class QuantityTests {

        @Test
        @DisplayName("Deve recalcular o total ao trocar a quantidade, inclusive fracionada")
        void shouldRecalculateTotalWhenQuantityChanges() {
            coffee.setQuantity(3.0f);
            assertEquals(3.0f, coffee.getQuantity(), DELTA);
            assertEquals(106.50f, coffee.getTotalCost(), DELTA);

            BuyingTask apples = new BuyingTask("Maçã", null, deadline, 8.0f, 2.5f, "kg");
            assertEquals(20.0f, apples.getTotalCost(), DELTA);
        }

        @Test
        @DisplayName("Custo e quantidade trocados em sequência devem manter o total consistente")
        void shouldKeepTotalConsistent() {
            coffee.setCost(10.0f);
            coffee.setQuantity(4.0f);

            assertEquals(coffee.getCost() * coffee.getQuantity(), coffee.getTotalCost(), DELTA);
            assertEquals(coffee.calcTotalCost(), coffee.getTotalCost(), DELTA);
        }

        @Test
        @DisplayName("Deve barrar quantidade inválida mantendo quantidade e total anteriores")
        void shouldRejectInvalidQuantity() {
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setQuantity(0.0f));
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setQuantity(-1.0f));
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setQuantity(Float.POSITIVE_INFINITY));

            assertEquals(2.0f, coffee.getQuantity(), DELTA);
            assertEquals(71.00f, coffee.getTotalCost(), DELTA);
        }
    }

    @Nested
    @DisplayName("Unidade")
    class UnitTests {

        @Test
        @DisplayName("Deve trocar a unidade e barrar unidade inválida mantendo a anterior")
        void shouldUpdateUnit() {
            coffee.setUnit("kg");
            assertEquals("kg", coffee.getUnit());

            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setUnit(null));
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setUnit(""));
            assertThrows(IllegalArgumentException.class,
                    () -> coffee.setUnit("   "));
            assertEquals("kg", coffee.getUnit());
        }
    }

    @Nested
    @DisplayName("Comportamento Herdado de Task")
    class InheritedTests {

        @Test
        @DisplayName("Deve receber lembrete e ser concluído como qualquer tarefa")
        void shouldBehaveAsTask() {
            Note note = new Note("Promoção", "Verificar cupom de desconto");
            coffee.addNote(note);
            assertSame(note, coffee.getNote());

            coffee.setStarred(true);
            coffee.setCompleted(true);
            assertTrue(coffee.isStarred());
            assertTrue(coffee.isCompleted());
            assertFalse(coffee.isOverdue());
        }
    }
}
