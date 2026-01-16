package com.example.memorizer.repository;

import com.example.memorizer.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями {@link Song}.
 * 
 * Предоставляет стандартные операции CRUD для песен вне очереди.
 * Наследуется от {@link JpaRepository}, что автоматически добавляет
 * методы для создания, чтения, обновления и удаления записей.
 * 
 * @see Song
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
}
