package br.unicamp.ic.mc322.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldCreateTaskWithTitleDescAndDeadline() {
        LocalDate deadline = LocalDate.now().plusDays(5);
        Task task = new Task("Task com descrição", "Implementar testes", deadline);

        assertEquals("Task com descrição", task.getTitle());
        assertEquals("Implementar testes", task.getDesc());
        assertEquals(deadline, task.getDeadline());
        assertFalse(task.isStarred());
        assertFalse(task.isCompleted());
        assertNull(task.getNote());
    }

    @Test
    void shouldCreateTaskWithTitleAndDeadlineOnly() {
        LocalDate deadline = LocalDate.now().plusDays(2);
        Task task = new Task("Task sem descrição", deadline);

        assertEquals("Task sem descrição", task.getTitle());
        assertEquals(deadline, task.getDeadline());
        assertFalse(task.isStarred());
        assertFalse(task.isCompleted());
        assertNull(task.getNote());
        assertNull(task.getDesc());
    }

    @Test
    void shouldThrowExceptionWhenDeadlineIsNullInConstructors() {
        assertThrows(IllegalArgumentException.class, () ->
            new Task("Título", "Descrição", null)
        );

        assertThrows(IllegalArgumentException.class, () ->
            new Task("Título", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingNullDeadline() {
        Task task = new Task("Título", LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () ->
            task.setDeadline(null)
        );
    }

    @Test
    void shouldUpdateDeadlineSuccessfully() {
        Task task = new Task("Título", LocalDate.now().plusDays(1));
        LocalDate newDeadline = LocalDate.now().plusDays(10);

        task.setDeadline(newDeadline);

        assertEquals(newDeadline, task.getDeadline());
    }

    @Test
    void isOverdueShouldReturnTrueOnlyWhenPendingAndDeadlinePassed() {
        Task futureTask = new Task("Futura", LocalDate.now().plusDays(3));
        Task todayTask = new Task("Hoje", LocalDate.now());
        Task pastTask = new Task("Atrasada", LocalDate.now().minusDays(1));

        assertFalse(futureTask.isOverdue());

        // Prazo hoje ainda não está atrasado
        assertFalse(todayTask.isOverdue());
        assertTrue(pastTask.isOverdue());
    }

    @Test
    void isOverdueShouldReturnFalseWhenCompleted() {
        Task pastTask = new Task("Atrasada", LocalDate.now().minusDays(1));
        pastTask.setCompleted(true);

        assertFalse(pastTask.isOverdue());

        // Reabrir a tarefa volta a deixá-la atrasada
        pastTask.setCompleted(false);
        assertTrue(pastTask.isOverdue());
    }

    @Test
    void shouldAddReplaceAndRemoveNote() {
        Task task = new Task("Estudar Java", LocalDate.now().plusDays(1));
        Note firstNote = new Note("Nota 1", "Ler sobre JUnit 5");
        Note secondNote = new Note("Nota 2", "Praticar assertions");

        task.addNote(firstNote);
        assertEquals(firstNote, task.getNote());

        task.addNote(secondNote);
        assertEquals(secondNote, task.getNote());

        task.removeNote();
        assertNull(task.getNote());
    }

    @Test
    void shouldThrowExceptionWhenAddingNullNote() {
        Task task = new Task("Estudar Java", LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () ->
            task.addNote(null)
        );
    }

    @Test
    void shouldToggleStarredAndCompletedFlags() {
        Task task = new Task("Tarefa", LocalDate.now().plusDays(1));

        task.setStarred(true);
        assertTrue(task.isStarred());
        task.setStarred(false);
        assertFalse(task.isStarred());

        task.setCompleted(true);
        assertTrue(task.isCompleted());
        task.setCompleted(false);
        assertFalse(task.isCompleted());
    }
}
