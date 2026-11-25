package br.unicamp.ic.mc322.persistence;

import br.unicamp.ic.mc322.model.BuyingTask;
import br.unicamp.ic.mc322.model.Note;
import br.unicamp.ic.mc322.model.RecurringTask;
import br.unicamp.ic.mc322.model.Task;
import br.unicamp.ic.mc322.model.lists.CleaningRoutineList;
import br.unicamp.ic.mc322.model.lists.NoteList;
import br.unicamp.ic.mc322.model.lists.ShoppingList;
import br.unicamp.ic.mc322.model.lists.TodoList;
import br.unicamp.ic.mc322.model.House;
import br.unicamp.ic.mc322.strategy.recurring.IntervalRecurringStrategy;
import br.unicamp.ic.mc322.strategy.recurring.MonthlyRecurringStrategy;
import br.unicamp.ic.mc322.strategy.recurring.WeeklyRecurringStrategy;
import br.unicamp.ic.mc322.model.Element;

import java.time.LocalDate;
import java.time.LocalDateTime;

/* Casas prontas para os testes de persistência,
 com datas fixas para comparar depois de carregar */
final class TestHouses {
    static final LocalDate DEADLINE = LocalDate.of(2026, 11, 30);
    static final LocalDateTime CREATED = LocalDateTime.of(2026, 11, 19, 8, 30);

    private TestHouses() {
    }

    static House empty() {
        return new House("República Batuta");
    }

    static House full() {
        House house = new House("Rep & <Casa> \"Azul\"");

        NoteList<Note> notes = new NoteList<>("Avisos", "Combinados da casa");
        notes.addElement(created(new Note("Senha Wi-Fi", "batuta123")));
        Note trash = new Note("Lixo", null);
        trash.setCreationDate(CREATED.minusDays(1));
        notes.addElement(trash);

        TodoList<Task> todo = new TodoList<>("Contas", null);
        Task bill = created(new Task("Pagar luz", "Vence dia 30", DEADLINE));
        bill.setStarred(true);
        bill.addNote(created(new Note("Boleto", "Está no e-mail")));
        Task rent = created(new Task("Pagar aluguel", DEADLINE.plusDays(5)));
        rent.setCompleted(true);
        todo.addElement(bill);
        todo.addElement(rent);
        todo.setCompleted(true);

        ShoppingList shopping = new ShoppingList("Mercado", "Compras do mês");
        shopping.addElement(created(new BuyingTask("Café", "3 Corações se tiver", DEADLINE, 10.0f, 3.0f, "pacotes")));
        shopping.addElement(created(new BuyingTask("Maçã", null, DEADLINE, 8.0f, 2.5f, "kg")));
        shopping.addElement(created(new BuyingTask("Sacolas", null, DEADLINE, 0.0f, 1.0f, "un")));

        CleaningRoutineList cleaning = new CleaningRoutineList("Limpeza", null);
        cleaning.addElement(created(new RecurringTask("Louça", "Toda semana", DEADLINE, new WeeklyRecurringStrategy())));
        cleaning.addElement(created(new RecurringTask("Geladeira", null, DEADLINE, new MonthlyRecurringStrategy())));
        cleaning.addElement(created(new RecurringTask("Banheiro", null, DEADLINE, new IntervalRecurringStrategy(3))));

        house.addList(notes);
        house.addList(todo);
        house.addList(shopping);
        house.addList(cleaning);
        return house;
    }

    private static <T extends Element> T created(T element) {
        element.setCreationDate(CREATED);
        return element;
    }
}
