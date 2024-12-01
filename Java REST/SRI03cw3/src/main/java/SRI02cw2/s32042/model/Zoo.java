package SRI02cw2.s32042.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Zoo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;

    @NotBlank(message = "Zoo name is required (1-100 characters)")
    @Size(min = 1, max = 100)
    private String name;

    @OneToMany(mappedBy = "zoo")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Animal> animals;
}
