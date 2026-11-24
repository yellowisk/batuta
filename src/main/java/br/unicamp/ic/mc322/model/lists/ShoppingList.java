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
            sum += item.getCost();
        }
        this.totalCost = sum;
        return sum;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }
}
