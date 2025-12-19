package tn.esprit.studentmanagement.controllers;

import org.junit.jupiter.api.Test;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    @Test
    void getAllStudents_shouldReturnListFromService() {
        IStudentService service = mock(IStudentService.class);
        List<Student> students = List.of(new Student(), new Student());
        when(service.getAllStudents()).thenReturn(students);

        StudentController controller = new StudentController(service);

        List<Student> result = controller.getAllStudents();

        assertEquals(2, result.size());
        assertSame(students, result);
        verify(service).getAllStudents();
    }

    @Test
    void getStudent_shouldReturnStudentFromService() {
        IStudentService service = mock(IStudentService.class);
        Student st = new Student();
        when(service.getStudentById(1L)).thenReturn(st);

        StudentController controller = new StudentController(service);

        Student result = controller.getStudent(1L);

        assertSame(st, result);
        verify(service).getStudentById(1L);
    }

    @Test
    void createStudent_shouldDelegateToService() {
        IStudentService service = mock(IStudentService.class);
        Student toSave = new Student();
        when(service.saveStudent(toSave)).thenReturn(toSave);

        StudentController controller = new StudentController(service);

        Student result = controller.createStudent(toSave);

        assertSame(toSave, result);
        verify(service).saveStudent(toSave);
    }

    @Test
    void updateStudent_shouldDelegateToService() {
        IStudentService service = mock(IStudentService.class);
        Student updated = new Student();
        when(service.saveStudent(updated)).thenReturn(updated);

        StudentController controller = new StudentController(service);

        Student result = controller.updateStudent(updated);

        assertSame(updated, result);
        verify(service).saveStudent(updated);
    }

    @Test
    void deleteStudent_shouldCallService() {
        IStudentService service = mock(IStudentService.class);
        StudentController controller = new StudentController(service);

        controller.deleteStudent(1L);

        verify(service).deleteStudent(1L);
    }

    @Test
    void ping_shouldReturnOk() {
        IStudentService service = mock(IStudentService.class);
        StudentController controller = new StudentController(service);

        String result = controller.ping();

        assertEquals("OK", result);
    }
}
