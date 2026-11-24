package br.unicamp.ic.mc322.model.lists;

import br.unicamp.ic.mc322.model.BuyingTask;

public class ShoppingList extends TodoList<BuyingTask>{
    private float totalCost;

    public ShoppingList(String title, String desc) {
        super(title, desc);
    }

    public float calcTotalCost() {
        float sum = 0;
        for (BuyingTask item : this.getElements()) {
            sum += item.getTotalCost();
        }
        this.totalCost = sum;
        return sum;
    }

    /* Recalcula a cada leitura, pois itens podem mudar custo
    ou quantidade depois de adicionados */
    public float getTotalCost() {
        return calcTotalCost();
    }
}
