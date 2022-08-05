package ar.com.intrale.persistence;

import java.util.Collection;

public interface PersitenceProvider<ENTITY> {
	
	/**
	 * Almacena una instancia de la entidad
	 * @param toPersist
	 */
	void save(ENTITY toPersist);
	
	/**
	 * Lista las entidades que cumplan con los filtros que se aplicaron
	 * @return
	 */
	Collection<ENTITY> list(PersistenceFilters filters);
	
	/**
	 * Obtiene la primera ocurrencia de la entidad que cumple con los filtros
	 * @return
	 */
	ENTITY get(PersistenceFilters filters);
	
	/**
	 * Elimina la entidad que cumpla con los filtros que se aplicaron
	 */
	void delete(PersistenceFilters filters) throws Exception;
	
}
