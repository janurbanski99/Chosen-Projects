package SRI02cw2.s32042.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ZooDetailsDto extends RepresentationModel<ZooDetailsDto> {
    private Long id;

    @NotBlank(message = "Zoo name is required (1-100 characters)")
    @Size(min = 1, max = 100)
    private String name;

    private Set<AnimalDto> animals;
}
