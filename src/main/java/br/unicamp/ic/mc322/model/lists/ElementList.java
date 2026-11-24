package br.unicamp.ic.mc322.model.lists;

import br.unicamp.ic.mc322.model.Element;
import br.unicamp.ic.mc322.strategy.sorting.SortingStrategy;
import br.unicamp.ic.mc322.strategy.sorting.DateSortingStrategy;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementList<T extends Element> {
    private String title;
    private String desc;
    private List<T> elements = new ArrayList<>();
    private SortingStrategy<? super T> currSortingStrategy = new DateSortingStrategy();
    protected ElementList(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    /**
     * Ordena uma duplicata da lista de elementos conforme
     * a estratégia atual e a retorna. Dessa forma a lista original se
     * mantém inalterada.
     * Antigo 'sort()', que alterava a lista original.
     * @return Lista ordenada.
     */
    public List<T> getSortedElements() {
        List<T> copy = new ArrayList<>(elements);
        if (currSortingStrategy != null) {
            copy.sort(currSortingStrategy);
        }
        return copy;
    }

    /**
     * Alterna entre ordenação Ascendente e Decrescente.
     */
    public void toggleSortOrder() {
        if (currSortingStrategy != null) {
            currSortingStrategy.setDescending(!currSortingStrategy.isDescending());
        }
    }

    public void addElement(T e) {
        elements.add(e);
    }

    /* O índice é a posição que o usuário vê nas telas,
    ou seja, na lista ordenada (getSortedElements) */
    public void removeElement(int i) {
        elements.remove(getSortedElements().get(i));
    }
    public void clearList() {
        elements.clear();
    }

    public abstract boolean canBeDeleted();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setCurrSortingStrategy(SortingStrategy<? super T> currSortingStrategy) {
        this.currSortingStrategy = currSortingStrategy;
    }
}
