package org.uma.jmetal.service.executor;

import java.util.function.Consumer;
import java.util.function.Function;

import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.model.runnable.Run.Result;

public class RunTask implements Runnable {

	private final Run.Params params;
	private final Consumer<Run.Result> resultSetter;
	private Run.Status status;
	private final Function<Run.Params, ?> function;

	public RunTask(Run.Params params, Function<Run.Params, ?> function, Consumer<Result> resultSetter) {
		this.params = params;
		this.status = Run.Status.PENDING;
		this.function = function;
		this.resultSetter = resultSetter;
	}

	public RunTask(Result result) {
		this.params = null;
		this.status = Run.Status.DONE;
		this.function = null;
		this.resultSetter = null;
	}

	@Override
	public void run() {
		status = Run.Status.RUNNING;
		Result result;
		try {
			result = Result.withValue(function.apply(params));
		} catch (Exception cause) {
			result = Result.withError(cause);
		}
		resultSetter.accept(result);
		status = Run.Status.DONE;
	}

	public Run.Status getStatus() {
		return status;
	}

}
