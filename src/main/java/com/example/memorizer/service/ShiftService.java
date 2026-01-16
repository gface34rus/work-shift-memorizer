package com.example.memorizer.service;

import com.example.memorizer.model.Shift;
import com.example.memorizer.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления рабочими сменами.
 * 
 * Содержит бизнес-логику для работы со сменами, включая
 * автоматический расчет стоимости в зависимости от дня недели.
 * 
 * @see Shift
 * @see ShiftRepository
 */
@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    /**
     * Получает список всех рабочих смен.
     * 
     * @return список всех смен из базы данных
     */
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    /**
     * Создает новую рабочую смену с автоматическим расчетом стоимости.
     * 
     * <p>
     * Логика расчета стоимости:
     * <ul>
     * <li>1-12 января (праздники): 3000 ₽</li>
     * <li>Пятница/Суббота: 4000 ₽</li>
     * <li>Остальные дни: 3000 ₽</li>
     * </ul>
     * 
     * @param shift объект смены для сохранения (без заполненного поля cost)
     * @return сохраненная смена с рассчитанной стоимостью
     */
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

    /**
     * Удаляет рабочую смену по идентификатору.
     * 
     * @param id уникальный идентификатор смены
     */
    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }
}
