package computech.model;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by Anna on 18.11.2015.
 */
public interface RepairRepository extends CrudRepository<Reparation, Long> {

        void delete(Long id);

}

