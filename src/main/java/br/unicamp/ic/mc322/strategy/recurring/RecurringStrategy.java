package br.unicamp.ic.mc322.strategy.recurring;

import java.time.LocalDate;
import java.util.List;

public interface RecurringStrategy {
    public LocalDate getNextOccurrence(LocalDate fromDate);
    public List<LocalDate> getNextOccurrences(LocalDate fromDate, int count);
}
