package SRI02cw2.s32042.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Animal name is required (1-100 characters)")
    @Size(min = 1, max = 100)
    private String name;
    private String species;
    private String type; //carnivorous / herbivorous / omnivorous

    @ManyToOne
    @JoinColumn(name="zoo_id")
    private Zoo zoo;
}
