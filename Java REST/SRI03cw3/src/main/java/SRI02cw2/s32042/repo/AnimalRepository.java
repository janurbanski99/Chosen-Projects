package SRI02cw2.s32042.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import SRI02cw2.s32042.model.Animal;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


public interface AnimalRepository extends CrudRepository<Animal, Long> {
    List<Animal> findAll();

    @Query("select z.animals from Zoo as z where z.id =:zooId")
    List<Animal> findAnimalsByZooId(@Param("zooId") Long zooId);
}
