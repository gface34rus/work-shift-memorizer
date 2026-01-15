package com.example.memorizer.service;

import com.example.memorizer.model.Shift;
import com.example.memorizer.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public Shift createShift(Shift shift) {
        java.time.LocalDate date = shift.getDate();
        java.time.DayOfWeek day = date.getDayOfWeek();

        // Holiday logic: Jan 1 to Jan 12 is always 3000
        if (date.getMonthValue() == 1 && date.getDayOfMonth() >= 1 && date.getDayOfMonth() <= 12) {
            shift.setCost(3000);
        } else if (day == java.time.DayOfWeek.FRIDAY || day == java.time.DayOfWeek.SATURDAY) {
            shift.setCost(4000);
        } else {
            shift.setCost(3000);
        }
        return shiftRepository.save(shift);
    }

    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }
}
