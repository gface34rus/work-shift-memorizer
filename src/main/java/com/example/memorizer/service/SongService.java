package com.example.memorizer.service;

import com.example.memorizer.model.Song;
import com.example.memorizer.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления песнями вне очереди.
 * 
 * Содержит бизнес-логику для работы с песнями, включая
 * автоматическое назначение фиксированной стоимости.
 * 
 * @see Song
 * @see SongRepository
 */
@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    /**
     * Получает список всех песен.
     * 
     * @return список всех песен из базы данных
     */
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    /**
     * Добавляет новую песню с автоматическим расчетом стоимости.
     * 
     * Стоимость каждой песни автоматически устанавливается в 1000 ₽.
     * 
     * @param song объект песни для сохранения (без заполненного поля cost)
     * @return сохраненная песня с установленной стоимостью
     */
    public Song addSong(Song song) {
        song.setCost(1000);
        return songRepository.save(song);
    }

    /**
     * Удаляет песню по идентификатору.
     * 
     * @param id уникальный идентификатор песни
     */
    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }
}
