package SRI02cw2.s32042.rest;

import SRI02cw2.s32042.dto.AnimalDto;
import SRI02cw2.s32042.model.Animal;
import SRI02cw2.s32042.model.Zoo;
import SRI02cw2.s32042.repo.AnimalRepository;
import SRI02cw2.s32042.repo.ZooRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animals")

public class AnimalController {
    private final ZooRepository zooRepository;
    private AnimalRepository animalRepository;
    private ModelMapper modelMapper;

    public AnimalController(AnimalRepository animalRepository, ModelMapper modelMapper, ZooRepository zooRepository) {
        this.animalRepository = animalRepository;
        this.modelMapper = modelMapper;
        this.zooRepository = zooRepository;
    }
    private AnimalDto convertToDto(Animal a) {
        return modelMapper.map(a, AnimalDto.class);
    }

    private Animal convertToEntity(AnimalDto dto) {
        return modelMapper.map(dto, Animal.class);
    }


    @GetMapping
    public ResponseEntity<Collection<AnimalDto>> getAnimals(){
        List<Animal> allAnimals = animalRepository.findAll();
        List<AnimalDto> result = allAnimals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/{animId}")
    public ResponseEntity<AnimalDto> getAnimalById(@PathVariable Long animId){
        Optional<Animal> anim = animalRepository.findById(animId);
        if (anim.isPresent()) {
            AnimalDto animalDto = convertToDto(anim.get());     //.get() - metoda klasy Optional, jeśli Optional przechowuje jakąś wartość to ją zwraca, inaczej NoSuchElementException
            return new ResponseEntity<>(animalDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity newAnimal(@Valid @RequestBody AnimalDto anim) {
    Animal entity = convertToEntity(anim);
    animalRepository.save(entity);
        HttpHeaders headers = new HttpHeaders();
    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{id}")
            .buildAndExpand(entity.getId())
            .toUri();
    headers.add("Location", location.toString());
    return new ResponseEntity("New Animal Created!",headers, HttpStatus.CREATED);
    }

    @PutMapping("/{animId}")
    public ResponseEntity updateAnimal(@PathVariable Long animId, @Valid @RequestBody AnimalDto animDto) {
        Optional<Animal> currentAnim = animalRepository.findById(animId);
        if (currentAnim.isPresent()) {
            animDto.setId(animId);
            Animal entity = convertToEntity(animDto);
//            String entityName = animDto.getName();
            animalRepository.save(entity);
            return new ResponseEntity( "Animal Updated!",HttpStatus.OK);
        } else {
            return new ResponseEntity("Not Found...", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{animId}")
    public ResponseEntity deleteAnimal(@PathVariable Long animId) {
        boolean found = animalRepository.existsById(animId);
        if (found) {
            animalRepository.deleteById(animId);
            return new ResponseEntity("Animal with ID: " + animId + " Deleted!",HttpStatus.OK);
        } else {
            return new ResponseEntity("Not Found...", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{animalId}/zoo/{zooId}")
    public ResponseEntity assignAnimalToZoo(@PathVariable Long animalId, @PathVariable Long zooId) {
        Optional<Animal> anim= animalRepository.findById(animalId);
        Optional<Zoo> z = zooRepository.findById(zooId);

        if (anim.isPresent() && z.isPresent()) {
            Animal animal = anim.get();
            Zoo zoo = z.get();
            animal.setZoo(zoo);
            zoo.getAnimals().add(animal);

            animalRepository.saveAll(Arrays.asList(animal));
            zooRepository.saveAll(Arrays.asList(zoo));

            return new ResponseEntity<>("Animal assigned to zoo!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Animal / Zoo does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{animalId}/removeFromZoo")
    public ResponseEntity removeAnimalFromZoo(@PathVariable Long animalId) {
        Optional<Animal> animalOptional = animalRepository.findById(animalId);
        if (animalOptional.isPresent()) {
            Animal animal = animalOptional.get();
            Zoo zoo = animal.getZoo();
            if (zoo != null) {
                zoo.getAnimals().remove(animal);
                animal.setZoo(null);
                animalRepository.save(animal);
                zooRepository.save(zoo);
                return new ResponseEntity<>("Animal removed from zoo!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Animal is not assigned to any zoo", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("Animal does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
