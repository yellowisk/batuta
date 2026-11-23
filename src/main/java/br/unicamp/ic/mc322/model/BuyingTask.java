package br.unicamp.ic.mc322.model;

import java.time.LocalDate;

public class BuyingTask extends Task{
    private float cost;
    private float quantity;
    private float totalCost;
    private String unit;

    public BuyingTask(String title, String desc, LocalDate deadline, float cost, float quantity, String unit) {
        super(title, desc, deadline);
        if (cost <= 0) {
            throw new IllegalArgumentException("O custo não pode ser negativo.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("A unidade não pode ser nula ou vazia.");
        }
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
        if (cost <= 0) {
            throw new IllegalArgumentException("O custo não pode ser negativo.");
        }
        this.cost = cost;
        this.totalCost = calcTotalCost();
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
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
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("A unidade não pode ser nula ou vazia.");
        }
        this.unit = unit;
    }

    
}
