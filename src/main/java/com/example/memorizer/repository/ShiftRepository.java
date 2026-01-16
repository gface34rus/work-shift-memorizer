package com.example.memorizer.repository;

import com.example.memorizer.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Репозиторий для работы с сущностями {@link Shift}.
 * 
 * Предоставляет стандартные операции CRUD для рабочих смен.
 * Наследуется от {@link JpaRepository}, что автоматически добавляет
 * методы для создания, чтения, обновления и удаления записей.
 * 
 * @see Shift
 */
public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
