package org.uma.jmetal.service.register;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.stereotype.Repository;

@Repository
public abstract class RegisterTemplate<ID> {

	private final Collection<ID> ids = new LinkedList<>();

	public Collection<ID> getIds() {
		return ids;
	}

	public void add(ID id) {
		ids.add(id);
	}

}
