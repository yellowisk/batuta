package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Task;

public class DeadlineSortingStrategy implements SortingStrategy<Task> {

    @Override
    public int compare(Task a, Task b) {
        return a.getDeadline().compareTo(b.getDeadline());
    }
}
