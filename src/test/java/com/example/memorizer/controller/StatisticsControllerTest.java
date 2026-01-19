package com.example.memorizer.controller;

import com.example.memorizer.model.Shift;
import com.example.memorizer.model.Song;
import com.example.memorizer.repository.ShiftRepository;
import com.example.memorizer.repository.SongRepository;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для {@link StatisticsController}.
 * 
 * Тестируют REST API для получения статистики заработка
 * и выполнения выплат (обнуления баланса).
 */
@WebMvcTest(StatisticsController.class)
@DisplayName("StatisticsController Integration Tests")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShiftRepository shiftRepository;

    @MockBean
    private SongRepository songRepository;

    private Shift testShift;
    private Song testSong;

    @BeforeEach
    void setUp() {
        testShift = new Shift();
        testShift.setId(1L);
        testShift.setWorkerName("Я");
        testShift.setDate(LocalDate.of(2025, 1, 20));
        testShift.setStartTime(LocalTime.of(0, 0));
        testShift.setEndTime(LocalTime.of(23, 59));
        testShift.setCost(3000);
        testShift.setPaid(false);

        testSong = new Song();
        testSong.setId(1L);
        testSong.setTitle("Песня");
        testSong.setArtist("Вне очереди");
        testSong.setAddedBy("Гость");
        testSong.setCost(1000);
        testSong.setPaid(false);
    }

    @Test
    @DisplayName("GET /api/stats/earnings должен вернуть корректную статистику")
    void getEarnings_ShouldReturnCorrectStatistics() throws Exception {
        // Arrange
        when(shiftRepository.findAll()).thenReturn(Collections.singletonList(testShift));
        when(songRepository.findAll()).thenReturn(Collections.singletonList(testSong));

        // Act & Assert
        mockMvc.perform(get("/api/stats/earnings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lifetimeEarnings", is(4000))) // 3000 + 1000
                .andExpect(jsonPath("$.currentBalance", is(4000))); // все неоплачено

        verify(shiftRepository, times(1)).findAll();
        verify(songRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/stats/earnings с оплаченными записями должен вернуть только текущий баланс")
    void getEarnings_WithPaidItems_ShouldReturnOnlyCurrentBalance() throws Exception {
        // Arrange
        Shift paidShift = new Shift();
        paidShift.setId(2L);
        paidShift.setCost(3000);
        paidShift.setPaid(true);

        when(shiftRepository.findAll()).thenReturn(Arrays.asList(testShift, paidShift));
        when(songRepository.findAll()).thenReturn(Collections.singletonList(testSong));

        // Act & Assert
        mockMvc.perform(get("/api/stats/earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifetimeEarnings", is(7000))) // 3000 + 3000 + 1000
                .andExpect(jsonPath("$.currentBalance", is(4000))); // только неоплаченные

        verify(shiftRepository, times(1)).findAll();
        verify(songRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/stats/earnings без данных должен вернуть нули")
    void getEarnings_WithNoData_ShouldReturnZeros() throws Exception {
        // Arrange
        when(shiftRepository.findAll()).thenReturn(Collections.emptyList());
        when(songRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/stats/earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifetimeEarnings", is(0)))
                .andExpect(jsonPath("$.currentBalance", is(0)));
    }

    @Test
    @DisplayName("POST /api/stats/payout должен пометить все записи как оплаченные")
    void payout_ShouldMarkAllItemsAsPaid() throws Exception {
        // Arrange
        when(shiftRepository.findAll()).thenReturn(Collections.singletonList(testShift));
        when(songRepository.findAll()).thenReturn(Collections.singletonList(testSong));
        when(shiftRepository.save(any(Shift.class))).thenReturn(testShift);
        when(songRepository.save(any(Song.class))).thenReturn(testSong);

        // Act & Assert
        mockMvc.perform(post("/api/stats/payout"))
                .andExpect(status().isOk());

        verify(shiftRepository, times(1)).findAll();
        verify(songRepository, times(1)).findAll();
        verify(shiftRepository, times(1)).save(any(Shift.class));
        verify(songRepository, times(1)).save(any(Song.class));
    }

    @Test
    @DisplayName("POST /api/stats/payout с уже оплаченными записями не должен их пересохранять")
    void payout_WithAlreadyPaidItems_ShouldNotResaveThem() throws Exception {
        // Arrange
        testShift.setPaid(true);
        testSong.setPaid(true);

        when(shiftRepository.findAll()).thenReturn(Collections.singletonList(testShift));
        when(songRepository.findAll()).thenReturn(Collections.singletonList(testSong));

        // Act & Assert
        mockMvc.perform(post("/api/stats/payout"))
                .andExpect(status().isOk());

        verify(shiftRepository, times(1)).findAll();
        verify(songRepository, times(1)).findAll();
        verify(shiftRepository, never()).save(any(Shift.class)); // не должен сохранять
        verify(songRepository, never()).save(any(Song.class)); // не должен сохранять
    }

    @Test
    @DisplayName("GET /api/stats/earnings должен игнорировать записи с null cost")
    void getEarnings_ShouldIgnoreNullCosts() throws Exception {
        // Arrange
        testShift.setCost(null);
        when(shiftRepository.findAll()).thenReturn(Collections.singletonList(testShift));
        when(songRepository.findAll()).thenReturn(Collections.singletonList(testSong));

        // Act & Assert
        mockMvc.perform(get("/api/stats/earnings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lifetimeEarnings", is(1000))) // только песня
                .andExpect(jsonPath("$.currentBalance", is(1000)));
    }
}
