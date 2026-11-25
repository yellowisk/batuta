package br.unicamp.ic.mc322.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes Task")
public class TaskTest {

    private LocalDate deadline;
    private Task study;

    @BeforeEach
    void setUp() {
        deadline = LocalDate.of(2026, 11, 30);
        study = new Task("Estudar Java", "Implementar testes", deadline);
    }

    @Nested
    @DisplayName("Construtor e Inicialização")
    class ConstructorTests {

        @Test
        @DisplayName("Deve inicializar com título, descrição e prazo, sem favorito, pendente e sem lembrete")
        void shouldInitializeTask() {
            assertEquals("Estudar Java", study.getTitle());
            assertEquals("Implementar testes", study.getDesc());
            assertEquals(deadline, study.getDeadline());
            assertFalse(study.isStarred());
            assertFalse(study.isCompleted());
            assertNull(study.getNote());
            assertNotNull(study.getCreationDate());
        }

        @Test
        @DisplayName("Deve criar tarefa sem descrição")
        void shouldInitializeTaskWithoutDescription() {
            Task task = new Task("Pagar luz", deadline);

            assertEquals("Pagar luz", task.getTitle());
            assertNull(task.getDesc());
            assertEquals(deadline, task.getDeadline());

            assertNull(new Task("Pagar luz", "   ", deadline).getDesc());
        }

        @Test
        @DisplayName("Deve barrar título inválido e prazo nulo nos dois construtores")
        void shouldRejectInvalidArguments() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Task(null, "Descrição", deadline));
            assertThrows(IllegalArgumentException.class,
                    () -> new Task("", "Descrição", deadline));
            assertThrows(IllegalArgumentException.class,
                    () -> new Task("Título", "Descrição", null));
            assertThrows(IllegalArgumentException.class,
                    () -> new Task("   ", deadline));
            assertThrows(IllegalArgumentException.class,
                    () -> new Task("Título", null));
        }
    }

    @Nested
    @DisplayName("Prazo, Favorito e Conclusão")
    class StateTests {

        @Test
        @DisplayName("Deve trocar o prazo e barrar prazo nulo mantendo o anterior")
        void shouldUpdateDeadline() {
            LocalDate newDeadline = LocalDate.of(2026, 12, 10);
            study.setDeadline(newDeadline);
            assertEquals(newDeadline, study.getDeadline());

            assertThrows(IllegalArgumentException.class,
                    () -> study.setDeadline(null));
            assertEquals(newDeadline, study.getDeadline());
        }

        @Test
        @DisplayName("Deve marcar e desmarcar favorito e conclusão")
        void shouldToggleStarredAndCompleted() {
            study.setStarred(true);
            assertTrue(study.isStarred());
            study.setStarred(false);
            assertFalse(study.isStarred());

            study.setCompleted(true);
            assertTrue(study.isCompleted());
            study.setCompleted(false);
            assertFalse(study.isCompleted());
        }
    }

    @Nested
    @DisplayName("Tarefa Atrasada")
    class OverdueTests {

        @Test
        @DisplayName("Deve estar atrasada só quando pendente e com prazo antes de hoje")
        void shouldBeOverdueOnlyWhenPendingAndPastDeadline() {
            LocalDate today = LocalDate.now();

            assertFalse(new Task("Futura", today.plusDays(3)).isOverdue());
            assertFalse(new Task("Hoje", today).isOverdue());
            assertTrue(new Task("Atrasada", today.minusDays(1)).isOverdue());
        }

        @Test
        @DisplayName("Tarefa concluída nunca está atrasada; reabrir volta a deixá-la atrasada")
        void shouldNotBeOverdueWhenCompleted() {
            Task late = new Task("Atrasada", LocalDate.now().minusDays(1));

            late.setCompleted(true);
            assertFalse(late.isOverdue());

            late.setCompleted(false);
            assertTrue(late.isOverdue());
        }
    }

    @Nested
    @DisplayName("Lembrete")
    class NoteTests {

        @Test
        @DisplayName("Deve adicionar, trocar e remover o lembrete")
        void shouldAddReplaceAndRemoveNote() {
            Note first = new Note("Nota 1", "Ler sobre JUnit");
            Note second = new Note("Nota 2", "Praticar assertions");

            study.addNote(first);
            assertSame(first, study.getNote());

            study.addNote(second);
            assertSame(second, study.getNote());

            study.removeNote();
            assertNull(study.getNote());

            study.removeNote();
            assertNull(study.getNote());
        }

        @Test
        @DisplayName("Deve barrar lembrete nulo mantendo o anterior")
        void shouldRejectNullNote() {
            Note note = new Note("Nota", null);
            study.addNote(note);

            assertThrows(IllegalArgumentException.class,
                    () -> study.addNote(null));
            assertSame(note, study.getNote());
        }

        @Test
        @DisplayName("Remover o lembrete não deve alterar a tarefa")
        void shouldKeepTaskWhenRemovingNote() {
            LocalDateTime created = study.getCreationDate();
            study.setStarred(true);
            study.addNote(new Note("Nota", null));

            study.removeNote();

            assertEquals("Estudar Java", study.getTitle());
            assertEquals(deadline, study.getDeadline());
            assertEquals(created, study.getCreationDate());
            assertTrue(study.isStarred());
            assertFalse(study.isCompleted());
        }
    }
}
