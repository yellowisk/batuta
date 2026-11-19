package br.unicamp.ic.mc322.model;

/**
 * Classe para a lista de tarefas Task.
 * @param <T>
 */
public class TodoList<T extends Task> extends ElementList<T> {
    /**
     * Flag de conclusão da lista (não dos elementos)
     */
    private boolean isCompleted = false;

    public TodoList(String title, String desc) {
        super(title, desc);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
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