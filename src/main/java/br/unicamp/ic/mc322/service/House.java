package br.unicamp.ic.mc322.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import br.unicamp.ic.mc322.model.lists.ElementList;

public class House {
    private String name;
    private final List<ElementList<?>> lists;

    public House(String n){
        setName(n);
        this.lists = new ArrayList<>();
    }

    public void addList(ElementList<?> l){
        if (l == null) {
            throw new IllegalArgumentException("A lista não pode ser nula.");
        }
        if (lists.contains(l)) {
            throw new IllegalArgumentException("Essa lista já está na casa.");
        }
        this.lists.add(l);
    }

    public void removeList(ElementList<?> l){
        this.lists.remove(l);
    }

    public List<ElementList<?>> getLists() {
        return Collections.unmodifiableList(lists);
    }

    public String getName(){
        return name;
    }

    public void setName(String n) {
        if (n == null || n.isBlank()) {
            throw new IllegalArgumentException("O nome da casa não pode ser nulo nem vazio.");
        }
        name = n;
    }
}
