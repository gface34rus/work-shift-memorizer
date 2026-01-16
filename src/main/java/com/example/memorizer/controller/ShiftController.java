package com.example.memorizer.controller;

import com.example.memorizer.model.Shift;
import com.example.memorizer.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления рабочими сменами.
 * 
 * Предоставляет HTTP API для создания, получения и удаления смен.
 * Все эндпоинты доступны по базовому пути {@code /api/shifts}.
 * 
 * @see ShiftService
 * @see Shift
 */
@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    /**
     * Получает список всех рабочих смен.
     * 
     * @return список всех смен в формате JSON
     */
    @GetMapping
    public List<Shift> getAllShifts() {
        return shiftService.getAllShifts();
    }

    /**
     * Создает новую рабочую смену.
     * 
     * Стоимость смены рассчитывается автоматически на основе даты.
     * 
     * @param shift данные новой смены в формате JSON
     * @return созданная смена с рассчитанной стоимостью
     */
    @PostMapping
    public Shift createShift(@RequestBody Shift shift) {
        return shiftService.createShift(shift);
    }

    /**
     * Удаляет рабочую смену по идентификатору.
     * 
     * @param id уникальный идентификатор смены
     */
    @DeleteMapping("/{id}")
    public void deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
    }
}
