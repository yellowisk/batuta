package br.unicamp.ic.mc322.model.lists;

import br.unicamp.ic.mc322.model.BuyingTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes ShoppingList")
public class ShoppingListTest {

    private ShoppingList list;
    private BuyingTask item1;
    private BuyingTask item2;
    private BuyingTask item3;

    @BeforeEach
    void setUp() {
        list = new ShoppingList("Compras do mês", "Supermercado");

        LocalDate deadline = LocalDate.of(2026, 5, 10);

        // 2 * 10.50 = 21.00
        item1 = new BuyingTask("Arroz", "5kg", deadline, 10.50f, 2.0f, "pacote");
        // 3 * 4.00 = 12.00
        item2 = new BuyingTask("Leite Integral", "", deadline, 4.00f, 3.0f, "litro");
        // 1.5 * 8.00 = 12.00
        item3 = new BuyingTask("Banana Prata", "Quanto mais madura melhor", deadline, 8.00f, 1.5f, "kg");
    }

    @Nested
    @DisplayName("Cálculo de Custo Total")
    class TotalCostTests {

        @Test
        @DisplayName("Deve retornar 0 quando a lista estiver vazia")
        void mustReturnZeroWhenEmpty() {
            assertEquals(0.0f, list.getTotalCost(), 0.0001f);
        }

        @Test
        @DisplayName("Deve calcular o total como a soma de custo * quantidade dos itens")
        void mustCalculateTotalAsSumOfCostTimesQuantity() {
            list.addElement(item1); // 21
            list.addElement(item2); // 12
            list.addElement(item3); // 12

            // esperado: 21.00 + 12.00 + 12.00 = 45.00
            assertEquals(45.0f, list.getTotalCost(), 0.0001f);
        }

        @Test
        @DisplayName("Deve atualizar o total após adicionar, remover e limpar itens")
        void mustUpdateTotalAfterAddRemoveAndClear() {
            list.addElement(item1); // 21
            list.addElement(item2); // 12
            assertEquals(33.0f, list.getTotalCost(), 0.0001f);

            list.removeElement(0);
            assertEquals(12.0f, list.getTotalCost(), 0.0001f);

            list.clearList();
            assertEquals(0.0f, list.getTotalCost(), 0.0001f);
        }

        @Test
        @DisplayName("Deve atualizar o total quando o custo ou a quantidade de um item for alterado")
        void mustUpdateTotalWhenItemCostOrQuantityChanges() {
            list.addElement(item1); // 10.50 * 2.0 = 21
            list.addElement(item2); // 4.00 * 3.0 = 12.00
            assertEquals(33.0f, list.getTotalCost(), 0.0001f);

            // Altera custo de item1 para 15.00 -> (15.00 * 2.0) + 12.00 = 42.00
            item1.setCost(15.00f);
            assertEquals(42.0f, list.getTotalCost(), 0.0001f);

            // Altera quantidade de item2 para 5.0 -> 30.00 + (4.00 * 5.0) = 50.00
            item2.setQuantity(5.0f);
            assertEquals(50.0f, list.getTotalCost(), 0.0001f);
        }
    }
}