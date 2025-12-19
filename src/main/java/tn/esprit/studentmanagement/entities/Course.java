package tn.esprit.studentmanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCourse;
    private String name;
    private String code;
    private int credit;
    private String description;

    @OneToMany(mappedBy = "course")
    @JsonIgnore                      // 🔴 IMPORTANT
    private List<Enrollment> enrollments;
}
