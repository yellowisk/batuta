package br.unicamp.ic.mc322.strategy.recurring;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IntervalRecurringStrategy implements RecurringStrategy {
    private int intervalDays;

    public IntervalRecurringStrategy(int intervalDays) {
        if (intervalDays <= 0) {
            throw new IllegalArgumentException("O intervalo deve ser de pelo menos 1 dia.");
        }
        this.intervalDays = intervalDays;
    }

    @Override
    public LocalDate getNextOccurrence(LocalDate fromDate) {
        if (fromDate == null) {
            throw new IllegalArgumentException("A data de referência não pode ser nula.");
        }
        return fromDate.plusDays(intervalDays);
    }

    @Override
    public List<LocalDate> getNextOccurrences(LocalDate fromDate, int count) {
        if (fromDate == null) {
            throw new IllegalArgumentException("A data de referência não pode ser nula.");
        }
        if (count < 0) {
            throw new IllegalArgumentException("A quantidade de ocorrências não pode ser negativa.");
        }

        List<LocalDate> occurrences = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            occurrences.add(fromDate.plusDays((long) intervalDays * i));
        }
        return occurrences;
    }

    public int getIntervalDays() {
        return intervalDays;
    }
}
