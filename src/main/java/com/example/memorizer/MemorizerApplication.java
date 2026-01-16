package com.example.memorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Work Shift & Song Memorizer.
 * 
 * Приложение предназначено для учета рабочих смен и песен вне очереди,
 * с автоматическим расчетом заработка.
 * 
 * @author Pesterev Ivan
 * @version 1.0
 * @since 2026-01-15
 */
@SpringBootApplication
public class MemorizerApplication {

	/**
	 * Точка входа в приложение.
	 * 
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(MemorizerApplication.class, args);
	}

}
