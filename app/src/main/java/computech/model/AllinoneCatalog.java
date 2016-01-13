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

import org.salespointframework.catalog.Catalog;

/**
 *
 * Contains every all-in-one computer.
 *
 */


public interface AllinoneCatalog extends Catalog <Computer> {
	Iterable<Computer> findByType(Computer.Computertype type);

}
