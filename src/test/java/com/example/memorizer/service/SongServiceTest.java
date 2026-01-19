package com.example.memorizer.service;

import com.example.memorizer.model.Song;
import com.example.memorizer.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для {@link SongService}.
 * 
 * Проверяют корректность логики добавления песен
 * и автоматического назначения фиксированной стоимости.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SongService Tests")
class SongServiceTest {

    @Mock

    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song testSong;

    @BeforeEach
    void setUp() {
        testSong = new Song();
        testSong.setTitle("Тестовая песня");
        testSong.setArtist("Тестовый исполнитель");
        testSong.setAddedBy("Тестовый гость");
    }

    @Test
    @DisplayName("Стоимость песни должна быть 1000₽")
    void addSong_ShouldCost1000() {
        // Arrange
        when(songRepository.save(any(Song.class))).thenReturn(testSong);

        // Act
        Song result = songService.addSong(testSong);

        // Assert
        assertEquals(1000, result.getCost(), "Стоимость песни должна быть 1000₽");
        verify(songRepository, times(1)).save(testSong);
    }

    @Test
    @DisplayName("Стоимость песни должна быть установлена автоматически")
    void addSong_ShouldSetCostAutomatically() {
        // Arrange
        assertNull(testSong.getCost(), "Изначально cost должен быть null");
        when(songRepository.save(any(Song.class))).thenReturn(testSong);

        // Act
        Song result = songService.addSong(testSong);

        // Assert
        assertNotNull(result.getCost(), "Cost должен быть установлен");
        assertEquals(1000, result.getCost());
        verify(songRepository, times(1)).save(testSong);
    }

    @Test
    @DisplayName("Получение всех песен должно вернуть список")
    void getAllSongs_ShouldReturnList() {
        // Arrange
        Song song1 = new Song();
        song1.setId(1L);
        song1.setTitle("Песня 1");

        Song song2 = new Song();
        song2.setId(2L);
        song2.setTitle("Песня 2");

        List<Song> expectedSongs = Arrays.asList(song1, song2);
        when(songRepository.findAll()).thenReturn(expectedSongs);

        // Act
        List<Song> result = songService.getAllSongs();

        // Assert
        assertEquals(2, result.size(), "Должно быть возвращено 2 песни");
        assertEquals("Песня 1", result.get(0).getTitle());
        assertEquals("Песня 2", result.get(1).getTitle());
        verify(songRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Удаление песни должно вызвать deleteById")
    void deleteSong_ShouldCallRepositoryDeleteById() {
        // Arrange
        Long songId = 1L;

        // Act
        songService.deleteSong(songId);

        // Assert
        verify(songRepository, times(1)).deleteById(songId);
    }

    @Test
    @DisplayName("Новая песня должна иметь флаг paid = false")
    void addSong_ShouldHavePaidFalseByDefault() {
        // Arrange
        when(songRepository.save(any(Song.class))).thenReturn(testSong);

        // Act
        Song result = songService.addSong(testSong);

        // Assert
        assertFalse(result.isPaid(), "Новая песня должна быть неоплаченной");
        verify(songRepository, times(1)).save(testSong);
    }

    @Test
    @DisplayName("Добавление песни с уже установленным cost не должно его менять")
    void addSong_WithExistingCost_ShouldOverrideTo1000() {
        // Arrange
        testSong.setCost(5000); // Пытаемся установить неверную цену
        when(songRepository.save(any(Song.class))).thenReturn(testSong);

        // Act
        Song result = songService.addSong(testSong);

        // Assert
        assertEquals(1000, result.getCost(), "Cost должен быть переопределен на 1000");
        verify(songRepository, times(1)).save(testSong);
    }
}
