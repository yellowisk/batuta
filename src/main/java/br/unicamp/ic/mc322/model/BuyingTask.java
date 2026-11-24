package br.unicamp.ic.mc322.model;

import java.time.LocalDate;

public class BuyingTask extends Task {
    private float cost;
    private float quantity;
    private float totalCost;
    private String unit;

    public BuyingTask(String title, String desc, LocalDate deadline, float cost, float quantity, String unit) {
        super(title, desc, deadline);
        validateCost(cost);
        validateQuantity(quantity);
        validateUnit(unit);
        this.cost = cost;
        this.quantity = quantity;
        this.unit = unit;
        this.totalCost = calcTotalCost();
    }

    public float calcTotalCost() {
        return cost * quantity;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        validateCost(cost);
        this.cost = cost;
        this.totalCost = calcTotalCost();
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
        this.totalCost = calcTotalCost();
    }

    public float getTotalCost() {
        return totalCost;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        validateUnit(unit);
        this.unit = unit;
    }

    // Infinto e negativo não, mas custo zero é válido (item grátis)
    private static void validateCost(float cost) {
        if (!Float.isFinite(cost) || cost < 0) {
            throw new IllegalArgumentException("O custo deve ser um valor maior ou igual a zero.");
        }
    }

    private static void validateQuantity(float quantity) {
        if (!Float.isFinite(quantity) || quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser um valor maior que zero.");
        }
    }

    private static void validateUnit(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("A unidade não pode ser nula ou vazia.");
        }
    }
}
