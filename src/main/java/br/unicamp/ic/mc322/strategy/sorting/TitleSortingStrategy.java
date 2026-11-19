package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Element;

public class TitleSortingStrategy implements SortingStrategy<Element> {

    private boolean descending;

    public TitleSortingStrategy() {
        this(false);
    }

    public TitleSortingStrategy(boolean descending) {
        this.descending = descending;
    }

    @Override
    public int compare(Element a, Element b) {
        int cmp = a.getTitle().compareToIgnoreCase(b.getTitle());
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