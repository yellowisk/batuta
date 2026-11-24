package br.unicamp.ic.mc322.persistence;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Set;

public final class HouseXml {
    static final String VERSION= "1.0";

    static final Set<String> LIST_TYPES = Set.of("NoteList", "TodoList", "ShoppingList", "CleaningRoutineList");
    static final Set<String> ELEMENT_TYPES = Set.of("Note", "Task", "BuyingTask", "RecurringTask");
    static final Set<String> STRATEGY_TYPES =
            Set.of("WeeklyRecurringStrategy", "MonthlyRecurringStrategy", "IntervalRecurringStrategy");

    private HouseXml(){
    }

    static DocumentBuilder newParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return factory.newDocumentBuilder();
    }
}
