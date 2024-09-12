package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findAllByAge(int studentAge);
    Collection<Student> findByAgeBetween(int min, int max);

    @Query(value = "SELECT COUNT(*) AS count FROM student", nativeQuery = true)
    long count();

    @Query(value = "SELECT AVG(student.age) AS avg_age FROM student", nativeQuery = true)
    int avgAge();

    @Query(value = "select * from student s order by id desc limit 5", nativeQuery = true)
    Collection<Student> findFileLastStudents();
}
