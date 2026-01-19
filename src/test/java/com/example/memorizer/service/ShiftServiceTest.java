package com.example.memorizer.service;

import com.example.memorizer.model.Shift;
import com.example.memorizer.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для {@link ShiftService}.
 * 
 * Проверяют корректность бизнес-логики расчета стоимости смен
 * в зависимости от дня недели и праздничных дат.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ShiftService Tests")
class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private ShiftService shiftService;

    private Shift testShift;

    @BeforeEach
    void setUp() {
        testShift = new Shift();
        testShift.setWorkerName("Тестовый работник");
        testShift.setStartTime(LocalTime.of(9, 0));
        testShift.setEndTime(LocalTime.of(18, 0));
    }

    @Test
    @DisplayName("Стоимость смены в пятницу должна быть 4000₽")
    void createShift_Friday_ShouldCost4000() {
        // Arrange - Пятница 17 января 2025
        LocalDate friday = LocalDate.of(2025, 1, 17);
        testShift.setDate(friday);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertEquals(4000, result.getCost(), "Смена в пятницу должна стоить 4000₽");
        verify(shiftRepository, times(1)).save(testShift);
    }

    @Test
    @DisplayName("Стоимость смены в субботу должна быть 4000₽")
    void createShift_Saturday_ShouldCost4000() {
        // Arrange - Суббота 18 января 2025
        LocalDate saturday = LocalDate.of(2025, 1, 18);
        testShift.setDate(saturday);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertEquals(4000, result.getCost(), "Смена в субботу должна стоить 4000₽");
        verify(shiftRepository, times(1)).save(testShift);
    }

    @Test
    @DisplayName("Стоимость смены в понедельник должна быть 3000₽")
    void createShift_Monday_ShouldCost3000() {
        // Arrange - Понедельник 20 января 2025
        LocalDate monday = LocalDate.of(2025, 1, 20);
        testShift.setDate(monday);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertEquals(3000, result.getCost(), "Смена в понедельник должна стоить 3000₽");
        verify(shiftRepository, times(1)).save(testShift);
    }

    @Test
    @DisplayName("Стоимость смены 1 января должна быть 3000₽ (праздник)")
    void createShift_January1_ShouldCost3000() {
        // Arrange - 1 января (праздник, несмотря на день недели)
        LocalDate jan1 = LocalDate.of(2025, 1, 1);
        testShift.setDate(jan1);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertEquals(3000, result.getCost(), "Смена в праздничный день (1 янв) должна стоить 3000₽");
        verify(shiftRepository, times(1)).save(testShift);
    }

    @Test
    @DisplayName("Стоимость смены 10 января (пятница) должна быть 3000₽ (праздник)")
    void createShift_January10Friday_ShouldCost3000Holiday() {
        // Arrange - 10 января 2025 (пятница, но праздник имеет приоритет)
        LocalDate jan10 = LocalDate.of(2025, 1, 10);
        testShift.setDate(jan10);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertEquals(3000, result.getCost(),
                "Смена в праздник (1-12 янв) должна стоить 3000₽, даже если это пятница");
        verify(shiftRepository, times(1)).save(testShift);
    }

    @Test
    @DisplayName("Стоимость смены 13 января (понедельник) должна быть 3000₽")
    void createShift_January13_ShouldCost3000RegularWeekday() {
        // Arrange - 13 января (не праздник, обычный будний день)
        LocalDate jan13 = LocalDate.of(2025, 1, 13);
        testShift.setDate(jan13);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertEquals(3000, result.getCost(), "Смена 13 января должна стоить 3000₽ (обычный будний день)");
        verify(shiftRepository, times(1)).save(testShift);
    }

    @Test
    @DisplayName("Получение всех смен должно вернуть список")
    void getAllShifts_ShouldReturnList() {
        // Arrange
        Shift shift1 = new Shift();
        shift1.setId(1L);
        Shift shift2 = new Shift();
        shift2.setId(2L);
        List<Shift> expectedShifts = Arrays.asList(shift1, shift2);
        when(shiftRepository.findAll()).thenReturn(expectedShifts);

        // Act
        List<Shift> result = shiftService.getAllShifts();

        // Assert
        assertEquals(2, result.size(), "Должно быть возвращено 2 смены");
        verify(shiftRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Удаление смены должно вызвать deleteById")
    void deleteShift_ShouldCallRepositoryDeleteById() {
        // Arrange
        Long shiftId = 1L;

        // Act
        shiftService.deleteShift(shiftId);

        // Assert
        verify(shiftRepository, times(1)).deleteById(shiftId);
    }

    @Test
    @DisplayName("Новая смена должна иметь флаг paid = false")
    void createShift_ShouldHavePaidFalseByDefault() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 1, 20);
        testShift.setDate(date);
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);

        // Act
        Shift result = shiftService.createShift(testShift);

        // Assert
        assertFalse(result.isPaid(), "Новая смена должна быть неоплаченной");
        verify(shiftRepository, times(1)).save(testShift);
    }
}
