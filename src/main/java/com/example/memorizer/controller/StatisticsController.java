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

        // Lifetime earnings (all items, paid and unpaid)
        long lifetimeShiftEarnings = shifts.stream()
                .mapToInt(shift -> shift.getCost() != null ? shift.getCost() : 0)
                .sum();

        long lifetimeSongEarnings = songs.stream()
                .mapToInt(song -> song.getCost() != null ? song.getCost() : 0)
                .sum();

        // Current balance (unpaid items only)
        long currentShiftBalance = shifts.stream()
                .filter(shift -> !shift.isPaid())
                .mapToInt(shift -> shift.getCost() != null ? shift.getCost() : 0)
                .sum();

        long currentSongBalance = songs.stream()
                .filter(song -> !song.isPaid())
                .mapToInt(song -> song.getCost() != null ? song.getCost() : 0)
                .sum();

        long lifetimeTotal = lifetimeShiftEarnings + lifetimeSongEarnings;
        long currentTotal = currentShiftBalance + currentSongBalance;

        return new EarningsDTO(lifetimeTotal, currentTotal);
    }

    @org.springframework.web.bind.annotation.PostMapping("/payout")
    public void payout() {
        List<Shift> shifts = shiftRepository.findAll();
        for (Shift shift : shifts) {
            if (!shift.isPaid()) {
                shift.setPaid(true);
                shiftRepository.save(shift);
            }
        }

        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            if (!song.isPaid()) {
                song.setPaid(true);
                songRepository.save(song);
            }
        }
    }

    @Data
    public static class EarningsDTO {
        private final long lifetimeEarnings; // Всего за всё время
        private final long currentBalance; // Текущий баланс (с последней выплаты)
    }
}
