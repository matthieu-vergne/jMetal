package org.uma.jmetal.service.register.run;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.MapRegister;

@Repository
public class RunRegister extends MapRegister<Long, Run> {

	public synchronized long store(Run run) {
		long id = newId(getIds());
		store(id, run);
		return id;
	}

	private long newId(Collection<Long> IdsInUse) {
		Optional<Long> maxUsed = IdsInUse.stream().max(Long::compare);
		if (maxUsed.isPresent()) {
			return maxUsed.get() + 1;
		} else {
			return 0L;
		}
	}

}
