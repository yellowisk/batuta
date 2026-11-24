package br.unicamp.ic.mc322.persistence;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import br.unicamp.ic.mc322.exception.SaveWriteException;
import br.unicamp.ic.mc322.model.BuyingTask;
import br.unicamp.ic.mc322.model.Element;
import br.unicamp.ic.mc322.model.RecurringTask;
import br.unicamp.ic.mc322.model.Task;
import br.unicamp.ic.mc322.model.lists.ElementList;
import br.unicamp.ic.mc322.model.lists.TodoList;
import br.unicamp.ic.mc322.service.House;
import br.unicamp.ic.mc322.strategy.recurring.IntervalRecurringStrategy;
import br.unicamp.ic.mc322.strategy.recurring.RecurringStrategy;

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
        root.setAttribute("name", h.getName());
        document.appendChild(root);

        for (ElementList<?> list : h.getLists()) {
            root.appendChild(writeList(list));
        }

        return document;
    }

    private org.w3c.dom.Element writeList(ElementList<?> l) {
        String type = l.getClass().getSimpleName();
        if (!HouseXml.LIST_TYPES.contains(type)) {
            throw new SaveWriteException("Tipo de lista não suportado no save: " + type);
        }

        org.w3c.dom.Element listXml = document.createElement("list");
        listXml.setAttribute("type", type);
        listXml.setAttribute("title", l.getTitle() != null ? l.getTitle() : "");
        setIfPresent(listXml, "desc", l.getDesc());

        if (l instanceof TodoList<?> todoList) {
            listXml.setAttribute("isCompleted", String.valueOf(todoList.isCompleted()));
        }

        for (Element el : l.getElements()) {
            listXml.appendChild(writeElement(el));
        }

        return listXml;
    }

    private org.w3c.dom.Element writeElement(Element e) {
        String type = e.getClass().getSimpleName();
        if (!HouseXml.ELEMENT_TYPES.contains(type)) {
            throw new SaveWriteException("Tipo de elemento não suportado no save: " + type);
        }

        org.w3c.dom.Element elXml = document.createElement("element");
        elXml.setAttribute("type", type);
        elXml.setAttribute("title", e.getTitle());
        setIfPresent(elXml, "desc", e.getDesc());
        elXml.setAttribute("creationDate", e.getCreationDate().toString());

        if (e instanceof Task task) {
            elXml.setAttribute("deadline", task.getDeadline().toString());
            elXml.setAttribute("isStarred", String.valueOf(task.isStarred()));
            elXml.setAttribute("isCompleted", String.valueOf(task.isCompleted()));

            if (task.getNote() != null) {
                elXml.appendChild(writeElement(task.getNote()));
            }
        }

        if (e instanceof BuyingTask buyingTask) {
            elXml.setAttribute("cost", String.valueOf(buyingTask.getCost()));
            elXml.setAttribute("quantity", String.valueOf(buyingTask.getQuantity()));
            elXml.setAttribute("unit", buyingTask.getUnit());
        }

        if (e instanceof RecurringTask recurringTask) {
            RecurringStrategy strategy = recurringTask.getCurrRecurringStrategy();
            String strategyType = strategy.getClass().getSimpleName();
            if (!HouseXml.STRATEGY_TYPES.contains(strategyType)) {
                throw new SaveWriteException("Estratégia de recorrência não suportada no save: " + strategyType);
            }
            elXml.setAttribute("strategyType", strategyType);

            if (strategy instanceof IntervalRecurringStrategy interval) {
                elXml.setAttribute("strategyIntervalDays", String.valueOf(interval.getIntervalDays()));
            }
        }

        return elXml;
    }

    private void setIfPresent(org.w3c.dom.Element xml, String name, String value) {
        if (value != null) {
            xml.setAttribute(name, value);
        }
    }
}
