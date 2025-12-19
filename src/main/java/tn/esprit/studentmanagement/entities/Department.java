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
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDepartment;
    private String name;
    private String location;
    private String phone;
    private String head; // chef de département

    @OneToMany(mappedBy = "department")
    @JsonIgnore                       // 🔴 IMPORTANT
    private List<Student> students;
}
