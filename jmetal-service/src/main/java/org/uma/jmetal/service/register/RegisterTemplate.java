package org.uma.jmetal.service.register;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public abstract class RegisterTemplate<ID, T> {

	private final Collection<ID> ids = new LinkedHashSet<>();
	private final Map<ID, T> values = new HashMap<>();

	public Collection<ID> getIds() {
		synchronized (ids) {
			return ids;
		}
	}

	public void store(ID id, T value) {
		synchronized (ids) {
			if (ids.contains(id)) {
				throw new IllegalArgumentException("Already used ID " + id);
			} else {
				ids.add(id);
			}
		}
		values.put(id, value);
	}

	public T retrieve(ID id) {
		return values.get(id);
	}

}
