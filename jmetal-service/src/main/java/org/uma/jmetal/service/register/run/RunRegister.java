package org.uma.jmetal.service.register.run;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.Run.Params;
import org.uma.jmetal.service.model.runnable.Run.Request;
import org.uma.jmetal.service.register.RegisterTemplate;

@Repository
public class RunRegister extends RegisterTemplate<Long, Run> {

	public synchronized Long store(Request request, Function<Params, Object> function) {
		Long id = newId(getIds());
		store(id, new Run(request, function));
		return id;
	}

	private Long newId(Collection<Long> IdsInUse) {
		Optional<Long> maxUsed = IdsInUse.stream().max(Long::compare);
		if (maxUsed.isPresent()) {
			return maxUsed.get() + 1;
		} else {
			return 0L;
		}
	}

}
