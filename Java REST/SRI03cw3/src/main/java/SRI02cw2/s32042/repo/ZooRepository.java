package SRI02cw2.s32042.repo;

import SRI02cw2.s32042.model.Zoo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZooRepository extends CrudRepository<Zoo, Long> {
    List<Zoo> findAll();

//    @Query("from Zoo as z left join fetch z.animals where z.id =:zooId")
//    Optional<Zoo> getZooDetailsById(@Param("zooId") Long zooId);
}
