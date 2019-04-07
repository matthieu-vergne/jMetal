package org.uma.jmetal.service.register;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class MapRegister<ID, T> {

	private final Map<ID, T> map = new LinkedHashMap<>();

	public Collection<ID> getIds() {
		synchronized (map) {
			return map.keySet();
		}
	}

	public void store(ID id, T value) {
		synchronized (map) {
			if (map.containsKey(id)) {
				throw new IllegalArgumentException("Already used ID " + id);
			} else {
				map.put(id, value);
			}
		}
	}

	public T retrieve(ID id) {
		return map.get(id);
	}

}
