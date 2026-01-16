package com.example.memorizer.controller;

import com.example.memorizer.model.Song;
import com.example.memorizer.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления песнями вне очереди.
 * 
 * Предоставляет HTTP API для создания, получения и удаления песен.
 * Все эндпоинты доступны по базовому пути {@code /api/songs}.
 * 
 * @see SongService
 * @see Song
 */
@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    /**
     * Получает список всех песен.
     * 
     * @return список всех песен в формате JSON
     */
    @GetMapping
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }

    /**
     * Добавляет новую песню.
     * 
     * Стоимость песни устанавливается автоматически (1000 ₽).
     * 
     * @param song данные новой песни в формате JSON
     * @return созданная песня с установленной стоимостью
     */
    @PostMapping
    public Song addSong(@RequestBody Song song) {
        return songService.addSong(song);
    }

    /**
     * Удаляет песню по идентификатору.
     * 
     * @param id уникальный идентификатор песни
     */
    @DeleteMapping("/{id}")
    public void deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
    }
}
