package br.unicamp.ic.mc322.persistence;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Set;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

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
        DocumentBuilder parser = factory.newDocumentBuilder();
        parser.setErrorHandler(new SilentErrorHandler());
        return parser;
    }

    /* O handler padrão imprime "[Fatal Error]" no
     terminal, mas queremos ele como exceção
     */
    private static final class SilentErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException e) {
        }

        @Override
        public void error(SAXParseException e) throws SAXParseException {
            throw e;
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXParseException {
            throw e;
        }
    }
}
