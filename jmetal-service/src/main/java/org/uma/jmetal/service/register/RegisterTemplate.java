package org.uma.jmetal.service.register;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.stereotype.Repository;

@Repository
public abstract class RegisterTemplate {

	private final Collection<String> ids = new LinkedList<>();

	public Collection<String> getIds() {
		return ids;
	}

	public void add(String id) {
		ids.add(id);
	}

}
