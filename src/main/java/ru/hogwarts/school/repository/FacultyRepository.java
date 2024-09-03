package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findAllByColorIgnoreCase(String facultyColor);
    Collection<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);
    Optional<Faculty> findByNameIgnoreCase(String facultyName);
    @Query(value = "SELECT * FROM faculty ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Faculty> findRandom();
}
