/*
 *
 *	SWT-Praktikum TU Dresden 2015
 *	Gruppe 32 - Computech
 *
 *	Stephan Fischer
 *  Anna Gromykina
 *  Kevin Horst
 *  Philipp Oehme
 *
 */

package computech.model;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * Contains every customer (private and business customer).
 *
 */

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    void delete(Long id);

    Customer findByUserAccount(UserAccount userAccount);
}