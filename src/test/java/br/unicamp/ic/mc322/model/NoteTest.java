package br.unicamp.ic.mc322.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void shouldCreateNoteWithTitleAndDescription() {
        Note note = new Note("Nota", "Lorem Ipsum");

        assertEquals("Nota", note.getTitle());
        assertEquals("Lorem Ipsum", note.getDesc());
    }

    @Test
    void shouldBeAnInstanceOfElement() {
        Note note = new Note("Nota", "Lorem Ipsum");

        assertInstanceOf(Element.class, note);
    }

    @Test
    void shouldUpdateInheritedFieldsIfSettersExist() {
        Note note = new Note("Título Inicial", "Descrição Inicial");

        note.setTitle("Título Atualizado");
        note.setDesc("Descrição Atualizada");

        assertEquals("Título Atualizado", note.getTitle());
        assertEquals("Descrição Atualizada", note.getDesc());
    }
}