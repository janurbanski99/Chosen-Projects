package SRI02cw2.s32042.dto;

import SRI02cw2.s32042.model.Zoo;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AnimalDto {
    private Long id;

    @NotBlank(message = "Animal name is required (1-100 characters)")
    @Size(min = 1, max = 100)
    private String name;

    private String species;
    private String type;
}
