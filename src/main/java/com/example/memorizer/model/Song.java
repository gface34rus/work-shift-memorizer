package com.example.memorizer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Сущность "Песня вне очереди".
 * 
 * Представляет собой запись о песне, исполненной вне очереди,
 * с фиксированной стоимостью 1000 ₽.
 * 
 * @see SongService#addSong(Song)
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    /** Уникальный идентификатор песни */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название песни */
    private String title;
    /** Исполнитель песни */
    private String artist;
    /** Имя человека, который предложил/заказал песню */
    private String addedBy; // Кто предложил/заказал

    /** Рассчитанная стоимость песни в рублях (всегда 1000) */
    private Integer cost;

    /** Флаг оплаты песни (true - оплачено, false - не оплачено) */
    @jakarta.persistence.Column(columnDefinition = "boolean default false")
    private boolean paid = false;
}
