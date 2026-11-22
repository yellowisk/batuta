package br.unicamp.ic.mc322.persistence;

import java.lang.reflect.Field;
import java.time.LocalDate;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import br.unicamp.ic.mc322.exception.SaveWriteException;
import br.unicamp.ic.mc322.model.Element;
import br.unicamp.ic.mc322.model.RecurringTask;
import br.unicamp.ic.mc322.model.Task;
import br.unicamp.ic.mc322.model.lists.ElementList;
import br.unicamp.ic.mc322.model.lists.TodoList;
import br.unicamp.ic.mc322.service.House;
import br.unicamp.ic.mc322.strategy.IntervalRecurringStrategy;
import br.unicamp.ic.mc322.strategy.RecurringStrategy;
public class HouseXmlWriter {
    private Document document;
    HouseXmlWriter(){

    }
    Document write(House h) {
        if (h == null) {
            throw new SaveWriteException("Não é possível salvar uma casa que não existe.");
        }

        try {
            this.document = HouseXml.newParser().newDocument();
        } catch (ParserConfigurationException e) {
            throw new SaveWriteException("Erro ao inicializar o documento XML.", e);
        }

        org.w3c.dom.Element root = document.createElement("house");
        root.setAttribute("version", HouseXml.VERSION);
        root.setAttribute("name", h.getName() != null ? h.getName() : "");
        document.appendChild(root);

        for (Object obj : h.getLists()) {
            if (obj instanceof ElementList<?> list) {
                root.appendChild(writeList(list));
            }
        }

        return document;
    }
    private org.w3c.dom.Element writeList(ElementList<?> l) {
        org.w3c.dom.Element listXml = document.createElement("list");
        listXml.setAttribute("type", l.getClass().getSimpleName());
        listXml.setAttribute("title", l.getTitle() != null ? l.getTitle() : "");
        listXml.setAttribute("desc", l.getDesc() != null ? l.getDesc() : "");

        if (l instanceof TodoList<?> todoList) {
            listXml.setAttribute("isCompleted", String.valueOf(todoList.isCompleted()));
        }

        for (Object obj : l.getElements()) {
            if (obj instanceof Element el) {
                listXml.appendChild(writeElement(el));
            }
        }

        return listXml;
    }
    private  org.w3c.dom.Element writeElement(Element e) {
        org.w3c.dom.Element elXml = document.createElement("element");
        elXml.setAttribute("type", e.getClass().getSimpleName());
        elXml.setAttribute("title", e.getTitle() != null ? e.getTitle() : "");
        elXml.setAttribute("desc", e.getDesc() != null ? e.getDesc() : "");
        elXml.setAttribute("creationDate", e.getCreationDate() != null ? e.getCreationDate().toString() : "");

        if (e instanceof Task task) {
            elXml.setAttribute("deadline", task.getDeadline() != null ? task.getDeadline().toString() : "");
            elXml.setAttribute("isStarred", String.valueOf(task.isStarred()));
            elXml.setAttribute("isCompleted", String.valueOf(task.isCompleted()));

            if (task.getNote() != null) {
                elXml.appendChild(writeElement(task.getNote()));
            }
        }

        if (e instanceof BuyingTask buyingTask) {
            elXml.setAttribute("cost", String.valueOf(buyingTask.getCost()));
            elXml.setAttribute("quantity", String.valueOf(buyingTask.getQuantity()));
            elXml.setAttribute("unit", buyingTask.getUnit() != null ? buyingTask.getUnit() : "");
        }

        if (e instanceof RecurringTask recurringTask) {
            RecurringStrategy strategy = recurringTask.getCurrRecurringStrategy();
            if (strategy != null) {
                elXml.setAttribute("strategyType", strategy.getClass().getSimpleName());
                try {
                    Field fromDateField = strategy.getClass().getDeclaredField("fromDate");
                    fromDateField.setAccessible(true);
                    LocalDate fromDate = (LocalDate) fromDateField.get(strategy);
                    elXml.setAttribute("strategyFromDate", fromDate != null ? fromDate.toString() : "");

                    if (strategy instanceof IntervalRecurringStrategy) {
                        Field intervalField = strategy.getClass().getDeclaredField("intervalDays");
                        intervalField.setAccessible(true);
                        int intervalDays = intervalField.getInt(strategy);
                        elXml.setAttribute("strategyIntervalDays", String.valueOf(intervalDays));
                    }
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    throw new SaveWriteException("Erro ao salvar a estratégia de recorrência.", ex);
                }
            }
        }

        return elXml;
}
}
