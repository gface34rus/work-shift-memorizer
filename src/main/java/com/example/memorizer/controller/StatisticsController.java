package com.example.memorizer.controller;

import com.example.memorizer.model.Shift;
import com.example.memorizer.model.Song;
import com.example.memorizer.repository.ShiftRepository;
import com.example.memorizer.repository.SongRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private SongRepository songRepository;

    @GetMapping("/earnings")
    public EarningsDTO getEarnings() {
        List<Shift> shifts = shiftRepository.findAll();
        List<Song> songs = songRepository.findAll();

        long shiftEarnings = shifts.stream()
                .mapToInt(shift -> shift.getCost() != null ? shift.getCost() : 0)
                .sum();

        long songEarnings = songs.stream()
                .mapToInt(song -> song.getCost() != null ? song.getCost() : 0)
                .sum();

        return new EarningsDTO(shiftEarnings, songEarnings, shiftEarnings + songEarnings);
    }

    @Data
    public static class EarningsDTO {
        private final long shiftEarnings;
        private final long songEarnings;
        private final long totalEarnings;
    }
}
