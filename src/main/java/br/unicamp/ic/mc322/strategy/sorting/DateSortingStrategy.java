package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Element;

public class DateSortingStrategy implements SortingStrategy<Element> {

    @Override
    public int compare(Element a, Element b) {
        return a.getCreationDate().compareTo(b.getCreationDate());
    }
}
