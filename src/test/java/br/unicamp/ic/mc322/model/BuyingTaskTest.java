package br.unicamp.ic.mc322.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BuyingTaskTest {

    private static final float DELTA = 0.0001f;

    @Test
    void shouldCreateBuyingTaskAndCalculateInitialTotalCost() {
        LocalDate deadline = LocalDate.now().plusDays(3);
        BuyingTask task = new BuyingTask("Comprar café", "Café em grãos torra média", deadline, 35.50f, 2.0f, "pacotes");

        assertEquals("Comprar café", task.getTitle());
        assertEquals("Café em grãos torra média", task.getDesc());
        assertEquals(deadline, task.getDeadline());
        assertEquals(35.50f, task.getCost(), DELTA);
        assertEquals(2.0f, task.getQuantity(), DELTA);
        assertEquals("pacotes", task.getUnit());
        assertEquals(71.00f, task.getTotalCost(), DELTA);
        assertFalse(task.isCompleted());
        assertFalse(task.isStarred());
    }

    @Test
    void shouldBeAnInstanceOfTaskAndElement() {
        BuyingTask task = new BuyingTask("Comprar leite", "Integral", LocalDate.now().plusDays(1), 5.0f, 3.0f, "litros");

        assertInstanceOf(Task.class, task);
        assertInstanceOf(Element.class, task);
    }

    @Test
    void shouldThrowExceptionForInvalidConstructorArguments() {
        LocalDate deadline = LocalDate.now().plusDays(2);

        // Custo zero ou negativo
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", deadline, -1.0f, 2.0f, "un")
        );
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", deadline, 0.0f, 2.0f, "un")
        );

        // Quantidade zero ou negativa
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", deadline, 10.0f, 0.0f, "un")
        );
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", deadline, 10.0f, -2.5f, "un")
        );

        // Unidade nula ou vazia
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", deadline, 10.0f, 1.0f, null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", deadline, 10.0f, 1.0f, " ")
        );

        // Data de entrega nula (herdada de Task)
        assertThrows(IllegalArgumentException.class, () ->
            new BuyingTask("Item", "Desc", null, 10.0f, 1.0f, "un")
        );
    }

    @Test
    void shouldRecalculateTotalCostWhenCostChanges() {
        BuyingTask task = new BuyingTask("Comprar maçãs", "Fuji", LocalDate.now().plusDays(2), 8.0f, 2.5f, "kg");
        assertEquals(20.0f, task.getTotalCost(), DELTA);

        task.setCost(10.0f);

        assertEquals(10.0f, task.getCost(), DELTA);
        assertEquals(25.0f, task.getTotalCost(), DELTA);
    }

    @Test
    void shouldThrowExceptionWhenSettingNonPositiveCost() {
        BuyingTask task = new BuyingTask("Comprar maçãs", "Fuji", LocalDate.now().plusDays(2), 8.0f, 2.5f, "kg");

        assertThrows(IllegalArgumentException.class, () ->
            task.setCost(0.0f)
        );
        assertThrows(IllegalArgumentException.class, () ->
            task.setCost(-0.01f)
        );
    }

    @Test
    void shouldRecalculateTotalCostWhenQuantityChanges() {
        BuyingTask task = new BuyingTask("Comprar arroz", "Tipo 1", LocalDate.now().plusDays(4), 22.0f, 1.0f, "sacos");
        assertEquals(22.0f, task.getTotalCost(), DELTA);

        task.setQuantity(3.0f);

        assertEquals(3.0f, task.getQuantity(), DELTA);
        assertEquals(66.0f, task.getTotalCost(), DELTA);
    }

    @Test
    void shouldThrowExceptionWhenSettingNonPositiveQuantity() {
        BuyingTask task = new BuyingTask("Comprar arroz", "Tipo 1", LocalDate.now().plusDays(4), 22.0f, 1.0f, "sacos");

        assertThrows(IllegalArgumentException.class, () ->
            task.setQuantity(0.0f)
        );
        assertThrows(IllegalArgumentException.class, () ->
            task.setQuantity(-1.0f)
        );
    }

    @Test
    void shouldUpdateUnitAndValidateInvalidUnits() {
        BuyingTask task = new BuyingTask("Comprar tecido", "Algodão", LocalDate.now().plusDays(5), 15.0f, 2.0f, "metros");

        task.setUnit("rolos");
        assertEquals("rolos", task.getUnit());

        assertThrows(IllegalArgumentException.class, () ->
            task.setUnit(null)
        );
        assertThrows(IllegalArgumentException.class, () ->
            task.setUnit("")
        );
    }

    @Test
    void shouldInheritTaskBehaviorLikeCompletionAndNotes() {
        BuyingTask task = new BuyingTask("Comprar arroz", "Tipo 1", LocalDate.now().plusDays(1), 90.0f, 1.0f, "un");
        Note note = new Note("Promoção", "Verificar cupom de desconto");

        task.addNote(note);
        assertEquals(note, task.getNote());

        task.setCompleted(true);
        assertTrue(task.isDeadlineMet());
    }
}