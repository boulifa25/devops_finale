package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    EnrollmentRepository enrollmentRepository;

    @InjectMocks
    EnrollmentService enrollmentService;

    @Test
    void getAllEnrollments_shouldReturnList() {
        when(enrollmentRepository.findAll())
                .thenReturn(List.of(new Enrollment(), new Enrollment()));

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertEquals(2, result.size());
        verify(enrollmentRepository).findAll();
    }

    @Test
    void getEnrollmentById_shouldReturnEnrollment() {
        Enrollment e = new Enrollment();
        e.setIdEnrollment(10L);

        when(enrollmentRepository.findById(10L))
                .thenReturn(Optional.of(e));

        Enrollment result = enrollmentService.getEnrollmentById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getIdEnrollment());
        verify(enrollmentRepository).findById(10L);
    }

    @Test
    void saveEnrollment_shouldSaveAndReturn() {
        Enrollment e = new Enrollment();

        when(enrollmentRepository.save(e)).thenReturn(e);

        Enrollment result = enrollmentService.saveEnrollment(e);

        assertNotNull(result);
        verify(enrollmentRepository).save(e);
    }

    @Test
    void deleteEnrollment_shouldCallRepository() {
        enrollmentService.deleteEnrollment(7L);
        verify(enrollmentRepository).deleteById(7L);
    }
}
