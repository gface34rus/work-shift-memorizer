package com.example.memorizer.controller;

import com.example.memorizer.model.Shift;
import com.example.memorizer.service.ShiftService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для {@link ShiftController}.
 * 
 * Тестируют REST API эндпоинты для управления сменами.
 */
@WebMvcTest(ShiftController.class)
@DisplayName("ShiftController Integration Tests")
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShiftService shiftService;

    private Shift testShift;

    @BeforeEach
    void setUp() {
        testShift = new Shift();
        testShift.setId(1L);
        testShift.setWorkerName("Тестовый работник");
        testShift.setDate(LocalDate.of(2025, 1, 20));
        testShift.setStartTime(LocalTime.of(9, 0));
        testShift.setEndTime(LocalTime.of(18, 0));
        testShift.setCost(3000);
        testShift.setPaid(false);
    }

    @Test
    @DisplayName("GET /api/shifts должен вернуть список всех смен")
    void getAllShifts_ShouldReturnListOfShifts() throws Exception {
        // Arrange
        Shift shift2 = new Shift();
        shift2.setId(2L);
        shift2.setWorkerName("Другой работник");
        shift2.setDate(LocalDate.of(2025, 1, 24));
        shift2.setCost(4000);

        List<Shift> shifts = Arrays.asList(testShift, shift2);
        when(shiftService.getAllShifts()).thenReturn(shifts);

        // Act & Assert
        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].workerName", is("Тестовый работник")))
                .andExpect(jsonPath("$[0].cost", is(3000)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].cost", is(4000)));

        verify(shiftService, times(1)).getAllShifts();
    }

    @Test
    @DisplayName("POST /api/shifts должен создать новую смену")
    void createShift_ShouldReturnCreatedShift() throws Exception {
        // Arrange
        when(shiftService.createShift(any(Shift.class))).thenReturn(testShift);

        // Act & Assert
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testShift)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.workerName", is("Тестовый работник")))
                .andExpect(jsonPath("$.cost", is(3000)))
                .andExpect(jsonPath("$.paid", is(false)));

        verify(shiftService, times(1)).createShift(any(Shift.class));
    }

    @Test
    @DisplayName("DELETE /api/shifts/{id} должен удалить смену")
    void deleteShift_ShouldCallServiceDelete() throws Exception {
        // Arrange
        Long shiftId = 1L;
        doNothing().when(shiftService).deleteShift(shiftId);

        // Act & Assert
        mockMvc.perform(delete("/api/shifts/{id}", shiftId))
                .andExpect(status().isOk());

        verify(shiftService, times(1)).deleteShift(shiftId);
    }

    @Test
    @DisplayName("POST /api/shifts с валидными данными должен вернуть 200")
    void createShift_WithValidData_ShouldReturn200() throws Exception {
        // Arrange
        Shift newShift = new Shift();
        newShift.setWorkerName("Я");
        newShift.setDate(LocalDate.of(2025, 1, 25));
        newShift.setStartTime(LocalTime.of(0, 0));
        newShift.setEndTime(LocalTime.of(23, 59));

        testShift.setCost(4000); // Суббота
        when(shiftService.createShift(any(Shift.class))).thenReturn(testShift);

        // Act & Assert
        mockMvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newShift)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost", is(4000)));

        verify(shiftService, times(1)).createShift(any(Shift.class));
    }
}
