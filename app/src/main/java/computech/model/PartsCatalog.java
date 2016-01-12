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
 * Catalog which contains every existing part for all-in-one computer.
 *
 */

public interface PartsCatalog extends Catalog <Part> {
	Iterable<Part> findByType(Part.PartType type);

}
