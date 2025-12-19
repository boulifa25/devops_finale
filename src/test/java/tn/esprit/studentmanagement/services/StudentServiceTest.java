package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    StudentRepository studentRepository;

    @InjectMocks
    StudentService studentService;

    @Test
    void getAllStudents_shouldReturnList() {
        when(studentRepository.findAll())
                .thenReturn(List.of(new Student(), new Student()));

        List<Student> result = studentService.getAllStudents();

        assertEquals(2, result.size());
        verify(studentRepository).findAll();
    }

    @Test
    void getStudentById_shouldReturnStudent() {
        Student s = new Student();
        s.setIdStudent(1L);

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(s));

        Student result = studentService.getStudentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdStudent());
        verify(studentRepository).findById(1L);
    }

    @Test
    void saveStudent_shouldSaveAndReturn() {
        Student s = new Student();
        s.setFirstName("Hamza");

        when(studentRepository.save(s)).thenReturn(s);

        Student result = studentService.saveStudent(s);

        assertEquals("Hamza", result.getFirstName());
        verify(studentRepository).save(s);
    }

    @Test
    void deleteStudent_shouldCallRepository() {
        studentService.deleteStudent(3L);
        verify(studentRepository).deleteById(3L);
    }
}
