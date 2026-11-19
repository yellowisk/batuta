package br.unicamp.ic.mc322.strategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MonthlyRecurringStrategy implements RecurringStrategy {

    public MonthlyRecurringStrategy() {}

    @Override
    public LocalDate getNextOccurrence(LocalDate fromDate) {
        if (fromDate == null)
            throw new IllegalArgumentException("A data de referência não pode ser nula.");

        return fromDate.plusMonths(1);
    }

    @Override
    public List<LocalDate> getNextOccurrences(LocalDate fromDate, int count) {
        if (fromDate == null)
            throw new IllegalArgumentException("A data de referência não pode ser nula.");

        if (count < 0)
            throw new IllegalArgumentException("A quantidade de ocorrências não pode ser negativa.");

        List<LocalDate> occurrences = new ArrayList<>();
        for (int i = 1; i <= count; i++)
            occurrences.add(fromDate.plusMonths(i));

        return occurrences;
    }
}
