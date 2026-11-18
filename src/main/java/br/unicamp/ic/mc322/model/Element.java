package br.unicamp.ic.mc322.model;

import java.time.LocalDateTime;

public abstract class Element {
    private String title;
    private String desc;
    private final LocalDateTime creationDate;

    public Element(String title, String desc) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser nulo nem vazio.");
        }

        if (desc == null || desc.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser nula nem vazia.");
        }
        this.title = title;
        this.desc = desc;
        this.creationDate = LocalDateTime.now(); //
    }

    public Element(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser nulo nem vazio.");
        }

        this.title = title;
        this.creationDate = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título não pode ser nulo nem vazio.");
        }
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        if (desc == null || desc.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser nula nem vazia.");
        }
        this.desc = desc;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
