//package SRI02cw2.s32042;
//
//import SRI02cw2.s32042.model.Animal;
//import SRI02cw2.s32042.model.Zoo;
//import SRI02cw2.s32042.repo.AnimalRepository;
//import SRI02cw2.s32042.repo.ZooRepository;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.HashSet;
//
//@Component
//public class DataInitializer implements ApplicationRunner {
//
//    private AnimalRepository animalRepository;
//    private ZooRepository zooRepository;
//
//    public DataInitializer(AnimalRepository animalRepository, ZooRepository zooRepository) {
//        this.animalRepository = animalRepository;
//        this.zooRepository = zooRepository;
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        Animal a1 = Animal.builder()
//                .name("Animal1")
//                .species("dog")
//                .type("omnivorous")
//                .build();
//        Animal a2 = Animal.builder()
//                .name("Animal2")
//                .species("cat")
//                .type("omnivorous")
//                .build();
//
//        Zoo z1 = Zoo.builder().name("Zoo1").animals(new HashSet<>()).build();
//        Zoo z2 = Zoo.builder().name("Zoo2").animals(new HashSet<>()).build();
//
//        a1.setZoo(z1);
//        z1.getAnimals().add(a1);
//
//        a2.setZoo(z2);
//        z2.getAnimals().add(a2);
//
//        zooRepository.saveAll(Arrays.asList(z1, z2));
//        animalRepository.saveAll(Arrays.asList(a1, a2));
//    }
//
//}
