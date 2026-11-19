package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Task;

public class DeadlineSortingStrategy implements SortingStrategy<Task> {

    private boolean descending;

    public DeadlineSortingStrategy() {
        this(false);
    }

    public DeadlineSortingStrategy(boolean descending) {
        this.descending = descending;
    }

    @Override
    public int compare(Task a, Task b) {
        int cmp = a.getDeadline().compareTo(b.getDeadline());
        return descending ? -cmp : cmp;
    }

    @Override
    public boolean isDescending() {
        return descending;
    }

    @Override
    public void setDescending(boolean descending) {
        this.descending = descending;
    }
}