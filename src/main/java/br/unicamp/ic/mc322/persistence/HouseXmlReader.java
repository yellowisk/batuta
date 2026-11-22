package br.unicamp.ic.mc322.persistence;

import br.unicamp.ic.mc322.exception.SaveReadException;
import org.w3c.dom.Document;
import br.unicamp.ic.mc322.model.lists.*;
import br.unicamp.ic.mc322.model.*;
import br.unicamp.ic.mc322.service.*;
import br.unicamp.ic.mc322.strategy.*;
import br.unicamp.ic.mc322.strategy.sorting.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;
public class HouseXmlReader {
    HouseXmlReader()
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

        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                house.addList(readList((org.w3c.dom.Element) node));
            }
        }

        return house;
    }
    private ElementList<?> readList(org.w3c.dom.Element xml ){
        String type = xml.getAttribute("type");
        String title = xml.getAttribute("title");
        String desc = xml.getAttribute("desc");

        if ("NoteList".equals(type)) {
            NoteList<Note> list = new NoteList<>(title, desc);
            NodeList children = xml.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = readElement((org.w3c.dom.Element) node);
                    if (el instanceof Note note) {
                        list.addElement(note);
                    }
                }
            }
            return list;
        }

        if ("ShoppingList".equals(type)) {
            ShoppingList<BuyingTask> list = new ShoppingList<>(title, desc);
            list.setCompleted(Boolean.parseBoolean(xml.getAttribute("isCompleted")));
            NodeList children = xml.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = readElement((org.w3c.dom.Element) node);
                    if (el instanceof BuyingTask task) {
                        list.addElement(task);
                    }
                }
            }
            list.calcTotalCost();
            return list;
        }

        if ("CleaningRoutineList".equals(type)) {
            CleaningRoutineList list = new CleaningRoutineList(title, desc);
            list.setCompleted(Boolean.parseBoolean(xml.getAttribute("isCompleted")));
            NodeList children = xml.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = readElement((org.w3c.dom.Element) node);
                    if (el instanceof RecurringTask task) {
                        list.addElement(task);
                    }
                }
            }
            return list;
        }
        throw new SaveReadException("Tipo de lista desconhecido no XML: " + type);
     }
    private Element readElement(org.w3c.dom.Element xml){
        String type = xml.getAttribute("type");
        String title = xml.getAttribute("title");
        String desc = xml.getAttribute("desc");
        String creationDateStr = xml.getAttribute("creationDate");

        Element element;

        if ("Note".equals(type)) {
            element = new Note(title, desc);

        } else if ("Task".equals(type) || "BuyingTask".equals(type) || "RecurringTask".equals(type)) {
            String deadlineStr = xml.getAttribute("deadline");
            LocalDate deadline = deadlineStr.isEmpty() ? null : LocalDate.parse(deadlineStr);

            Task task;
            if ("BuyingTask".equals(type)) {
                float cost = Float.parseFloat(xml.getAttribute("cost"));
                float quantity = Float.parseFloat(xml.getAttribute("quantity"));
                String unit = xml.getAttribute("unit");

                BuyingTask buyingTask = new BuyingTask(title, desc, deadline, cost, quantity, unit);
                buyingTask.calcTotalCost();
                task = buyingTask;

            } else if ("RecurringTask".equals(type)) {
                String strategyType = xml.getAttribute("strategyType");
                RecurringStrategy strategy = null;

                if (!strategyType.isEmpty()) {
                    String fromDateStr = xml.getAttribute("strategyFromDate");
                    LocalDate fromDate = fromDateStr.isEmpty() ? null : LocalDate.parse(fromDateStr);

                    if ("WeeklyRecurringStrategy".equals(strategyType)) {
                        strategy = new WeeklyRecurringStrategy(fromDate);
                    } else if ("MonthlyRecurringStrategy".equals(strategyType)) {
                        strategy = new MonthlyRecurringStrategy(fromDate);
                    } else if ("IntervalRecurringStrategy".equals(strategyType)) {
                        int intervalDays = Integer.parseInt(xml.getAttribute("strategyIntervalDays"));
                        strategy = new IntervalRecurringStrategy(fromDate, intervalDays);
                    } else {
                        throw new SaveReadException("Estratégia de recorrência desconhecida: " + strategyType);
                    }
                }

                task = new RecurringTask(title, desc, deadline, strategy);

            } else {
                task = new Task(title, desc, deadline);
            }

            task.setStarred(Boolean.parseBoolean(xml.getAttribute("isStarred")));
            task.setCompleted(Boolean.parseBoolean(xml.getAttribute("isCompleted")));

            NodeList children = xml.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nested = readElement((org.w3c.dom.Element) node);
                    if (nested instanceof Note note) {
                        task.addNote(note);
                    }
                }
            }

            element = task;

        } else {
            throw new SaveReadException("Tipo de elemento desconhecido no XML: " + type);
        }
        return element;
    }
    }


