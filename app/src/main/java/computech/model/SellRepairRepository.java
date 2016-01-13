package computech.model;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by Anna on 12.01.2016.
 */
public interface SellRepairRepository  extends CrudRepository<Reparation, Long> {

        void delete(Long id);
}
