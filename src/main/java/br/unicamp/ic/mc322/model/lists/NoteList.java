package br.unicamp.ic.mc322.model.lists;

import br.unicamp.ic.mc322.model.Note;

/**
 * Classe para a lista de notas Note.
 * @param <T>
 */
public class NoteList<T extends Note> extends ElementList<T> {
    public NoteList(String title, String desc) {
        super(title, desc);
    }

    /**
     * A lista poderá ser deletada se estiver vazia.
     * @return true se estiver vazia
     */
    @Override
    public boolean canBeDeleted() {
        return getElements() == null || getElements().isEmpty();
    }
}
