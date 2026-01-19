package com.example.memorizer.controller;

import com.example.memorizer.model.Song;
import com.example.memorizer.service.SongService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для {@link SongController}.
 * 
 * Тестируют REST API эндпоинты для управления песнями.
 */
@WebMvcTest(SongController.class)
@DisplayName("SongController Integration Tests")
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SongService songService;

    private Song testSong;

    @BeforeEach
    void setUp() {
        testSong = new Song();
        testSong.setId(1L);
        testSong.setTitle("Тестовая песня");
        testSong.setArtist("Вне очереди");
        testSong.setAddedBy("Гость");
        testSong.setCost(1000);
        testSong.setPaid(false);
    }

    @Test
    @DisplayName("GET /api/songs должен вернуть список всех песен")
    void getAllSongs_ShouldReturnListOfSongs() throws Exception {
        // Arrange
        Song song2 = new Song();
        song2.setId(2L);
        song2.setTitle("Другая песня");
        song2.setArtist("Вне очереди");
        song2.setCost(1000);

        List<Song> songs = Arrays.asList(testSong, song2);
        when(songService.getAllSongs()).thenReturn(songs);

        // Act & Assert
        mockMvc.perform(get("/api/songs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Тестовая песня")))
                .andExpect(jsonPath("$[0].cost", is(1000)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(songService, times(1)).getAllSongs();
    }

    @Test
    @DisplayName("POST /api/songs должен создать новую песню")
    void addSong_ShouldReturnCreatedSong() throws Exception {
        // Arrange
        when(songService.addSong(any(Song.class))).thenReturn(testSong);

        // Act & Assert
        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSong)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Тестовая песня")))
                .andExpect(jsonPath("$.cost", is(1000)))
                .andExpect(jsonPath("$.paid", is(false)));

        verify(songService, times(1)).addSong(any(Song.class));
    }

    @Test
    @DisplayName("DELETE /api/songs/{id} должен удалить песню")
    void deleteSong_ShouldCallServiceDelete() throws Exception {
        // Arrange
        Long songId = 1L;
        doNothing().when(songService).deleteSong(songId);

        // Act & Assert
        mockMvc.perform(delete("/api/songs/{id}", songId))
                .andExpect(status().isOk());

        verify(songService, times(1)).deleteSong(songId);
    }

    @Test
    @DisplayName("POST /api/songs должен установить стоимость 1000")
    void addSong_ShouldSetCostTo1000() throws Exception {
        // Arrange
        Song newSong = new Song();
        newSong.setTitle("Новая песня");
        newSong.setArtist("Исполнитель");
        newSong.setAddedBy("Пользователь");

        when(songService.addSong(any(Song.class))).thenReturn(testSong);

        // Act & Assert
        mockMvc.perform(post("/api/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSong)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost", is(1000)));

        verify(songService, times(1)).addSong(any(Song.class));
    }
}
