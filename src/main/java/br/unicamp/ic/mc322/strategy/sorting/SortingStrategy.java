package br.unicamp.ic.mc322.strategy.sorting;

import br.unicamp.ic.mc322.model.Element;
import java.util.Comparator;

public interface SortingStrategy<T extends Element> extends Comparator<T> {
}
