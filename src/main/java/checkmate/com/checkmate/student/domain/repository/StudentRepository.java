package checkmate.com.checkmate.student.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.student_number = :studentNumber")
    Optional<Student> findByStudentNumber(@Param("studentNumber") int studentNumber);

}
