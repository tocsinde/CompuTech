package kickstart.model;

import org.salespointframework.catalog.Catalog;

import kickstart.model.Computer.ComputerType;


public interface ComputerCatalog extends Catalog <Computer> {
	Iterable<Computer> findByType(ComputerType type);

}
