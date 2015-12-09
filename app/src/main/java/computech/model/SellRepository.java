package computech.model;

import org.springframework.data.repository.CrudRepository;

public interface SellRepository extends CrudRepository<SellOrder, Long> {

    void delete(Long id);

}
