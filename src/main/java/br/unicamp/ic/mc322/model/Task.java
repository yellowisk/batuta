package br.unicamp.ic.mc322.model;

import java.time.LocalDate;

public class Task extends Element {
    private LocalDate deadline;
    private boolean isStarred;
    private boolean isCompleted;
    private Note note;

    public Task(String title, String desc, LocalDate deadline) {
        super(title, desc);
        setDeadline(deadline);
    }

    public Task(String title, LocalDate deadline) {
        super(title);
        setDeadline(deadline);
    }

    public boolean isOverdue() {
        return !isCompleted && LocalDate.now().isAfter(deadline);
    }

    public void addNote(Note n) {
        if (n == null) throw new IllegalArgumentException("A nota não pode ser nula.");
        this.note = n;
    }

    public void removeNote() {
        this.note = null;
    }

    public Note getNote() {
        return note;
    }

    public void setDeadline(LocalDate d) {
        if (d == null) {
            throw new IllegalArgumentException("A data de entrega não pode ser nula.");
        }
        this.deadline = d;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setStarred(boolean s) {
        this.isStarred = s;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setCompleted(boolean c) {
        this.isCompleted = c;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

}