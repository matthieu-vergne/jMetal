package org.uma.jmetal.service.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.uma.jmetal.service.model.runnable.Run;

@Component
public class RunExecutor {

	private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public void submit(Run run) {
		pool.submit(run);
	}

}
