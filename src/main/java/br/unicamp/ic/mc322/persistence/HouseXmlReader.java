package br.unicamp.ic.mc322.persistence;

import br.unicamp.ic.mc322.exception.SaveReadException;
import org.w3c.dom.Document;
import br.unicamp.ic.mc322.model.lists.*;
import br.unicamp.ic.mc322.model.*;
import br.unicamp.ic.mc322.strategy.recurring.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HouseXmlReader {
    HouseXmlReader() {}

    House read(Document d){
        if (d == null) {
            throw new SaveReadException("Documento XML vazio.");
        }

        org.w3c.dom.Element root = d.getDocumentElement();
        if (!"house".equals(root.getTagName())) {
            throw new SaveReadException("Tag raiz inválida: esperada <house>.");
        }

        String version = root.getAttribute("version");
        if (!HouseXml.VERSION.equals(version)) {
            throw new SaveReadException("Versão do XML incompatível: " + version);
        }

        House house = new House(root.getAttribute("name"));
        for (org.w3c.dom.Element listXml : children(root, "list")) {
            house.addList(readList(listXml));
        }
        return house;
    }

    private ElementList<?> readList(org.w3c.dom.Element xml){
        String type = xml.getAttribute("type");
        String title = xml.getAttribute("title");
        String desc = optional(xml, "desc");

        switch (type) {
            case "NoteList" -> {
                NoteList<Note> list = new NoteList<>(title, desc);
                readElements(xml, list, Note.class);
                return list;
            }
            case "TodoList" -> {
                TodoList<Task> list = new TodoList<>(title, desc);
                list.setCompleted(readBoolean(xml, "isCompleted"));
                readElements(xml, list, Task.class);
                return list;
            }
            case "ShoppingList" -> {
                ShoppingList list = new ShoppingList(title, desc);
                list.setCompleted(readBoolean(xml, "isCompleted"));
                readElements(xml, list, BuyingTask.class);
                return list;
            }
            case "CleaningRoutineList" -> {
                CleaningRoutineList list = new CleaningRoutineList(title, desc);
                list.setCompleted(readBoolean(xml, "isCompleted"));
                readElements(xml, list, RecurringTask.class);
                return list;
            }
            default -> throw new SaveReadException("Tipo de lista desconhecido no XML: " + type);
        }
    }

    private <T extends Element> void readElements(org.w3c.dom.Element listXml, ElementList<T> list, Class<T> type) {
        for (org.w3c.dom.Element elXml : children(listXml, "element")) {
            Element el = readElement(elXml);

            if (el.getClass() != type)
                throw new SaveReadException("Elemento do tipo " + el.getClass().getSimpleName()
                        + " não pode ficar numa " + list.getClass().getSimpleName() + ".");

            list.addElement(type.cast(el));
        }
    }

    private Element readElement(org.w3c.dom.Element xml){
        String type = xml.getAttribute("type");
        String title = xml.getAttribute("title");
        String desc = optional(xml, "desc");

        Element element = switch (type) {
            case "Note" -> new Note(title, desc);
            case "Task", "BuyingTask", "RecurringTask" -> readTask(xml, type, title, desc);
            default -> throw new SaveReadException("Tipo de elemento desconhecido no XML: " + type);
        };

        element.setCreationDate(LocalDateTime.parse(required(xml, "creationDate")));
        return element;
    }

    private Task readTask(org.w3c.dom.Element xml, String type, String title, String desc) {
        LocalDate deadline = LocalDate.parse(required(xml, "deadline"));

        Task task = switch (type) {
            case "BuyingTask" -> new BuyingTask(title, desc, deadline, readFloat(xml, "cost"),
                    readFloat(xml, "quantity"), xml.getAttribute("unit"));
            case "RecurringTask" -> new RecurringTask(title, desc, deadline, readStrategy(xml));
            default -> new Task(title, desc, deadline);
        };

        task.setStarred(readBoolean(xml, "isStarred"));
        task.setCompleted(readBoolean(xml, "isCompleted"));


        List<org.w3c.dom.Element> nested = children(xml, "element");
        if (nested.size() > 1) {
            throw new SaveReadException("A tarefa \"" + title + "\" tem mais de um lembrete.");
        }
        if (!nested.isEmpty()) {
            if (!(readElement(nested.getFirst()) instanceof Note note)) {
                throw new SaveReadException("O lembrete da tarefa \"" + title + "\" não é uma nota.");
            }
            task.addNote(note);
        }
        return task;
    }

    private RecurringStrategy readStrategy(org.w3c.dom.Element xml) {
        String strategyType = xml.getAttribute("strategyType");
        return switch (strategyType) {
            case "WeeklyRecurringStrategy" -> new WeeklyRecurringStrategy();
            case "MonthlyRecurringStrategy" -> new MonthlyRecurringStrategy();
            case "IntervalRecurringStrategy" ->
                    new IntervalRecurringStrategy(Integer.parseInt(required(xml, "strategyIntervalDays")));
            case "" -> throw new SaveReadException("Tarefa recorrente sem estratégia de recorrência.");
            default -> throw new SaveReadException("Estratégia de recorrência desconhecida: " + strategyType);
        };
    }

    // === Auxiliares ===

    // Filhos diretos com a tag esperada, dando erro quando há falha
    private List<org.w3c.dom.Element> children(org.w3c.dom.Element parent, String tag) {
        List<org.w3c.dom.Element> found = new ArrayList<>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            org.w3c.dom.Element child = (org.w3c.dom.Element) node;
            if (!tag.equals(child.getTagName())) {
                throw new SaveReadException("Tag inesperada <" + child.getTagName() + "> dentro de <"
                        + parent.getTagName() + ">.");
            }
            found.add(child);
        }
        return found;
    }

    private String required(org.w3c.dom.Element xml, String name) {
        if (!xml.hasAttribute(name)) {
            throw new SaveReadException("Falta o atributo \"" + name + "\" em <" + xml.getTagName() + ">.");
        }
        return xml.getAttribute(name);
    }

    private String optional(org.w3c.dom.Element xml, String name) {
        return xml.hasAttribute(name) ? xml.getAttribute(name) : null;
    }

    /* Boolean.parseBoolean devolve false para qualquer texto que não seja "true",
    o que esconderia um arquivo corrompido */
    private boolean readBoolean(org.w3c.dom.Element xml, String name) {
        String value = required(xml, name);
        if (!value.equals("true") && !value.equals("false")) {
            throw new SaveReadException("Valor inválido para \"" + name + "\": " + value);
        }
        return value.equals("true");
    }

    // Float.parseFloat aceita "NaN" e "Infinity", que não são valores válidos
    private float readFloat(org.w3c.dom.Element xml, String name) {
        float value = Float.parseFloat(required(xml, name));
        if (!Float.isFinite(value)) {
            throw new SaveReadException("Valor inválido para \"" + name + "\": " + value);
        }
        return value;
    }
}
