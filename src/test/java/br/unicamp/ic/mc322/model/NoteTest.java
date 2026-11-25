package br.unicamp.ic.mc322.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes Note")
public class NoteTest {

    private Note wifi;

    @BeforeEach
    void setUp() {
        wifi = new Note("Senha Wi-Fi", "batuta123");
    }

    @Nested
    @DisplayName("Construtor e Inicialização")
    class ConstructorTests {

        @Test
        @DisplayName("Deve inicializar com título, descrição e data de criação automática")
        void shouldInitializeNote() {
            LocalDateTime before = LocalDateTime.now();
            Note note = new Note("Lixo", "Terça e quinta");
            LocalDateTime after = LocalDateTime.now();

            assertEquals("Lixo", note.getTitle());
            assertEquals("Terça e quinta", note.getDesc());
            assertFalse(note.getCreationDate().isBefore(before));
            assertFalse(note.getCreationDate().isAfter(after));
        }

        @Test
        @DisplayName("Deve aceitar nota sem descrição")
        void shouldAllowMissingDescription() {
            assertNull(new Note("Senha Wi-Fi", null).getDesc());
            assertNull(new Note("Senha Wi-Fi", "").getDesc());
            assertNull(new Note("Senha Wi-Fi", "   ").getDesc());
        }

        @Test
        @DisplayName("Deve barrar título nulo ou vazio")
        void shouldRejectInvalidTitle() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Note(null, "batuta123"));
            assertThrows(IllegalArgumentException.class,
                    () -> new Note("", "batuta123"));
            assertThrows(IllegalArgumentException.class,
                    () -> new Note("   ", "batuta123"));
        }
    }

    @Nested
    @DisplayName("Edição")
    class EditTests {

        @Test
        @DisplayName("Deve editar o título e barrar título inválido mantendo o anterior")
        void shouldEditTitle() {
            wifi.setTitle("Wi-Fi da sala");
            assertEquals("Wi-Fi da sala", wifi.getTitle());

            assertThrows(IllegalArgumentException.class,
                    () -> wifi.setTitle(" "));
            assertEquals("Wi-Fi da sala", wifi.getTitle());
        }

        @Test
        @DisplayName("Deve editar e apagar a descrição")
        void shouldEditAndClearDescription() {
            wifi.setDesc("rede 5G");
            assertEquals("rede 5G", wifi.getDesc());

            wifi.setDesc(null);
            assertNull(wifi.getDesc());
        }
    }
}
