package br.unicamp.ic.mc322.model;

import br.unicamp.ic.mc322.strategy.recurring.RecurringStrategy;

import java.time.LocalDate;

public class RecurringTask extends Task {
    private RecurringStrategy currRecurringStrategy;

    public RecurringTask(String title, String desc, LocalDate deadline, RecurringStrategy currRecurringStrategy) {
        super(title, desc, deadline);
        setCurrRecurringStrategy(currRecurringStrategy);
    }

    public RecurringStrategy getCurrRecurringStrategy() {
        return currRecurringStrategy;
    }

    public void setCurrRecurringStrategy(RecurringStrategy currRecurringStrategy) {
        if (currRecurringStrategy == null)
            throw new IllegalArgumentException("A estratégia de recorrência não pode ser nula.");

        this.currRecurringStrategy = currRecurringStrategy;
    }
}
