package com.example.memorizer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Сущность "Рабочая смена".
 * 
 * Представляет собой запись о рабочей смене с указанием работника,
 * даты, времени начала и окончания, а также рассчитанной стоимости.
 * 
 * <p>
 * Стоимость смены зависит от дня недели:
 * <ul>
 * <li>Пятница/Суббота: 4000 ₽</li>
 * <li>Остальные дни: 3000 ₽</li>
 * <li>1-12 января (праздники): 3000 ₽</li>
 * </ul>
 * 
 * @see ShiftService#createShift(Shift)
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    /** Уникальный идентификатор смены */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Имя работника */
    private String workerName;

    /** Дата смены */
    private LocalDate date;

    /** Время начала смены */
    private LocalTime startTime;

    /** Время окончания смены */
    private LocalTime endTime;

    /** Рассчитанная стоимость смены в рублях */
    private Integer cost;

    /** Флаг оплаты смены (true - оплачено, false - не оплачено) */
    @jakarta.persistence.Column(columnDefinition = "boolean default false")
    private boolean paid = false;
}
