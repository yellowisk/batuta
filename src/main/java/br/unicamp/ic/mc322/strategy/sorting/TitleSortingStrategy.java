package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Element;

public class TitleSortingStrategy implements SortingStrategy<Element> {

    @Override
    public int compare(Element a, Element b) {
        return a.getTitle().compareToIgnoreCase(b.getTitle());
    }
}
