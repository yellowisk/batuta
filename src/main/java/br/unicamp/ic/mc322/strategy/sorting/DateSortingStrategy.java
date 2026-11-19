package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Element;

public class DateSortingStrategy implements SortingStrategy<Element> {

    private boolean descending;

    public DateSortingStrategy() {
        this(false);
    }

    public DateSortingStrategy(boolean descending) {
        this.descending = descending;
    }

    @Override
    public int compare(Element a, Element b) {
        int cmp = a.getCreationDate().compareTo(b.getCreationDate());
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