package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {

        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {

        return mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new EntityNotFoundException("Рейтинг с id=" + mpaId + " не найден"));
    }
}
