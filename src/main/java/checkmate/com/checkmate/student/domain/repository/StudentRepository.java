package checkmate.com.checkmate.student.domain.repository;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.studentNumber = :studentNumber")
    Optional<Student> findByStudentNumber(@Param("studentNumber") int studentNumber);

    @Query("SELECT s FROM Student s  WHERE s.member.memberId = :memberId ORDER BY s.attendanceTime DESC")
    List<Student> findAllStudentsByMemberIdOrderByAttendanceTimeDesc(@Param("memberId") Long memberId);

}
