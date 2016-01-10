package computech.model;

import org.springframework.data.repository.CrudRepository;

public interface SellanwserRepository extends CrudRepository<Sellanwser, Long> {
	
	void delete(Long id);

}
