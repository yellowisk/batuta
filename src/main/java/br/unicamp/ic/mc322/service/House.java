package br.unicamp.ic.mc322.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import br.unicamp.ic.mc322.model.lists.ElementList;
public class House {
    private String name;
    private  List<ElementList<?>> lists;

    public House(String n){
        name = n;
        this.lists = new ArrayList<>();
    }
    public void addList(ElementList<?> l){
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
        name=n;
    }
}
