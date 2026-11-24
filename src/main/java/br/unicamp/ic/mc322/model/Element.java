package br.unicamp.ic.mc322.model;

import java.time.LocalDateTime;

public abstract class Element {
    private String title;
    private String desc;
    private LocalDateTime creationDate;

    protected Element(String title, String desc) {
        setTitle(title);
        setDesc(desc);
        this.creationDate = LocalDateTime.now();
    }

    protected Element(String title) {
        this(title, null);
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

    // Descrição é opcional
    public void setDesc(String desc) {
        this.desc = (desc == null || desc.isBlank()) ? null : desc;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    // Usado ao carregar a casa salva, para restaurar a data original
    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("A data de criação não pode ser nula.");
        }
        this.creationDate = creationDate;
    }
}
