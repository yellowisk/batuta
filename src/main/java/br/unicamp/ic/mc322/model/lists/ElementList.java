package br.unicamp.ic.mc322.model.lists;

import br.unicamp.ic.mc322.model.Element;
import br.unicamp.ic.mc322.strategy.SortingStrategy;

import java.util.Collections;
import java.util.List;

public abstract class ElementList<T extends Element> {
    private String title;
    private String desc;
    private List<T> elements;
    private SortingStrategy<? super T> currSortingStrategy;

    protected ElementList(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public void sort() {
        Collections.sort(elements, currSortingStrategy);
    }

    public void addElement(T e) {
        elements.add(e);
    }

    public void removeElement(int i) {
        elements.remove(i);
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

    public void setElements(List<T> elements) {
        this.elements = elements;
    }

    public void setCurrSortingStrategy(SortingStrategy<? super T> currSortingStrategy) {
        this.currSortingStrategy = currSortingStrategy;
    }
}
