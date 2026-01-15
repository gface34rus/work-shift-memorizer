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
        java.time.DayOfWeek day = shift.getDate().getDayOfWeek();
        if (day == java.time.DayOfWeek.FRIDAY || day == java.time.DayOfWeek.SATURDAY) {
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
