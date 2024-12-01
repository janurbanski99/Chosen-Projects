package SRI02cw2.s32042.rest;

import SRI02cw2.s32042.dto.AnimalDto;
import SRI02cw2.s32042.dto.ZooDetailsDto;
import SRI02cw2.s32042.dto.ZooDto;
import SRI02cw2.s32042.dto.mapper.ZooDtoMapper;
import SRI02cw2.s32042.model.Animal;
import SRI02cw2.s32042.model.Zoo;
import SRI02cw2.s32042.repo.AnimalRepository;
import SRI02cw2.s32042.repo.ZooRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/zoos")
@RequiredArgsConstructor

public class ZooControler {
    private final ZooRepository zooRepository;
    private final AnimalRepository animalRepository;
    private final ModelMapper modelMapper;
    private final ZooDtoMapper zooDtoMapper;

    private Link createZooSelfLink(Long zooId) {
        Link linkSelf = linkTo(methodOn(ZooControler.class).getZooById(zooId)).withSelfRel();
        return linkSelf;
    }

    private Link createZooAnimalsLink(Long animalId) {
        Link linkSelf = linkTo(methodOn(ZooControler.class).getAnimalsByZooId(animalId)).withSelfRel();
        return linkSelf;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ZooDto>> getZoos() {
        List<Zoo> allZooEntities = zooRepository.findAll();
        List<ZooDto> result = allZooEntities.stream()
                .map(zooDtoMapper::convertToDto)
                .collect(Collectors.toList());
        for (ZooDto dto : result) {
            dto.add(createZooSelfLink(dto.getId()));
            dto.add(createZooAnimalsLink(dto.getId()));
        }
        Link linkSelf = linkTo(methodOn(ZooControler.class).getZoos()).withSelfRel();
        CollectionModel<ZooDto> res = CollectionModel.of(result, linkSelf);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{zooId}")
    public ResponseEntity<ZooDetailsDto> getZooById(@PathVariable Long zooId) {
        Optional<Zoo> zoo = zooRepository.findById(zooId);
        if (zoo.isPresent()) {
            ZooDetailsDto zooDetailsDto = zooDtoMapper.convertToDtoDetails(zoo.get());
            Link linkSelf = createZooSelfLink(zooId);
            zooDetailsDto.add(linkSelf);
            return new ResponseEntity<>(zooDetailsDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private AnimalDto convertToDto(Animal a) {
        return modelMapper.map(a, AnimalDto.class);
    }

    private Animal convertToEntity(AnimalDto dto) {
        return modelMapper.map(dto, Animal.class);
    }

    @GetMapping("/{zooId}/animals")
    public ResponseEntity<?> getAnimalsByZooId(@PathVariable Long zooId){   //<?> - wildcarta - metoda może zwrócić ResponseEntity z dowolnym typem body (niekoniecznie kolekcja - dla obsługi nieistniejącego zooid)
        boolean found = zooRepository.existsById(zooId);
        if (found){
            List<Animal> allAnimals = animalRepository.findAnimalsByZooId(zooId);
            List<AnimalDto> result = allAnimals.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(result, HttpStatus.OK); //tylko ok? co jak da złe zooId? -- stestować
        } else {
            return new ResponseEntity<>("Zoo with ID: " + zooId + " not found.",HttpStatus.NOT_FOUND);
        }
    }

    private ZooDto convertToDto(Zoo z) {
        return modelMapper.map(z, ZooDto.class);
    }

    private Zoo convertToEntity(ZooDto dto) {
        return modelMapper.map(dto, Zoo.class);
    }

    @PostMapping
    public ResponseEntity saveNewZoo(@Valid @RequestBody ZooDto zoo) {
        Zoo entity = convertToEntity(zoo);
        zooRepository.save(entity);
        HttpHeaders headers = new HttpHeaders();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.getId())
                .toUri(); headers.add("Location", location.toString());
        return new ResponseEntity<>("New Zoo Created!", headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{zooId}")
    public ResponseEntity deleteZoo(@PathVariable Long zooId) {
        boolean found = zooRepository.existsById(zooId);
        if (found) {
            Zoo zoo = zooRepository.findById(zooId).get();
            for (Animal animal : zoo.getAnimals()) {
                animal.setZoo(null);    //usuwamy połączenie - animal nie należy do żadnego zoo, dla zoo nie usuwamy bo i tak zaraz usuwamy zoo
                animalRepository.save(animal);
            }
            zooRepository.deleteById(zooId);
            return new ResponseEntity("Zoo with ID: " + zooId + " Deleted!",HttpStatus.OK);
        } else {
            return new ResponseEntity("Not Found...", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{zooId}")
    public ResponseEntity updateZoo(@Valid @PathVariable Long zooId, @Valid @RequestBody ZooDto zooDto) {
        Optional<Zoo> zoo = zooRepository.findById(zooId);
        if (zoo.isPresent()) {
            zooDto.setId(zooId);
            Zoo entity = convertToEntity(zooDto);
            zooRepository.save(entity);
            return new ResponseEntity( "Zoo Updated!",HttpStatus.OK);
        } else {
            return new ResponseEntity("Not Found...", HttpStatus.NOT_FOUND);
        }
    }
}














//DONEzrobić posta, niech name zoo będzie required
//DONE do dodania @valid też w animalcontroler
//DONE sprawdzić czy mogę przypisać 1 zwierzę do 2 zoo - nie moge
//DONE co jak usunę zoo, do którego są przypisane zwierzęta - czy też się usuną?
//DONE co jak zupdatuję zoo (co ze zwierzętami)
//DONE stestować te 2 nowe metody
//DONE dodać new Zoo created