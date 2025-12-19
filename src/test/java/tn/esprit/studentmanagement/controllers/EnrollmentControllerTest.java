package tn.esprit.studentmanagement.controllers;

import org.junit.jupiter.api.Test;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.services.IEnrollment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentControllerTest {

    @Test
    void getAllEnrollment_shouldReturnListFromService() {
        IEnrollment service = mock(IEnrollment.class);
        when(service.getAllEnrollments()).thenReturn(List.of(new Enrollment(), new Enrollment()));

        EnrollmentController controller = new EnrollmentController(service);

        List<Enrollment> result = controller.getAllEnrollment();

        assertEquals(2, result.size());
        verify(service).getAllEnrollments();
    }

    @Test
    void getEnrollment_shouldReturnOneFromService() {
        IEnrollment service = mock(IEnrollment.class);
        Enrollment e = new Enrollment();
        e.setIdEnrollment(3L);

        when(service.getEnrollmentById(3L)).thenReturn(e);

        EnrollmentController controller = new EnrollmentController(service);

        Enrollment result = controller.getEnrollment(3L);

        assertNotNull(result);
        assertEquals(3L, result.getIdEnrollment());
        verify(service).getEnrollmentById(3L);
    }

    @Test
    void createEnrollment_shouldCallServiceAndReturnResult() {
        IEnrollment service = mock(IEnrollment.class);
        Enrollment e = new Enrollment();

        when(service.saveEnrollment(e)).thenReturn(e);

        EnrollmentController controller = new EnrollmentController(service);

        Enrollment result = controller.createEnrollment(e);

        assertNotNull(result);
        verify(service).saveEnrollment(e);
    }

    @Test
    void updateEnrollment_shouldCallServiceAndReturnResult() {
        IEnrollment service = mock(IEnrollment.class);
        Enrollment e = new Enrollment();
        e.setIdEnrollment(4L);

        when(service.saveEnrollment(e)).thenReturn(e);

        EnrollmentController controller = new EnrollmentController(service);

        Enrollment result = controller.updateEnrollment(e);

        assertEquals(4L, result.getIdEnrollment());
        verify(service).saveEnrollment(e);
    }

    @Test
    void deleteEnrollment_shouldCallServiceDelete() {
        IEnrollment service = mock(IEnrollment.class);

        EnrollmentController controller = new EnrollmentController(service);

        controller.deleteEnrollment(8L);

        verify(service).deleteEnrollment(8L);
    }
}
