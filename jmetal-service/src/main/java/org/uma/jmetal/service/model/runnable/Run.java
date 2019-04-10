package org.uma.jmetal.service.model.runnable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

import org.springframework.hateoas.ResourceSupport;
import org.uma.jmetal.service.Rel;
import org.uma.jmetal.service.controller.runnable.RunnableController;
import org.uma.jmetal.service.executor.RunTask;

public class Run implements Runnable {

	private final Request request;
	private final RunTask task;
	private Result result;

	public Run(Request request, Function<Params, Object> function) {
		this.request = request;
		this.task = new RunTask(request.params, function, result -> this.result = result);
	}

	public Run(Request request, Result result) {
		// TODO check it fits the definitions
		this.request = request;
		this.task = new RunTask(result);
		this.result = result;
	}

	public Run.Params getParams() {
		return request.params;
	}

	public Result getResult() {
		return result;
	}

	public Run.Status getStatus() {
		return task.getStatus();
	}

	@Override
	public void run() {
		task.run();
	}

	@SuppressWarnings("serial")
	public static class Params extends HashMap<String, Object> {

		public static class Response extends ResourceSupport {

			public final Run.Params params;

			public Response(Run.Params params, Run.Response response) {
				this.params = params;
				add(response.getLink(Rel.RUN_PARAMS).withRel(Rel.SELF));
				add(response.getLink(Rel.SELF).withRel(Rel.RUN));
			}

		}

	}

	public static enum Status {

		PENDING, RUNNING, DONE;

		public static class Response extends ResourceSupport {

			public final Run.Status status;

			public Response(Run.Status status, Run.Response response) {
				this.status = status;
				add(response.getLink(Rel.RUN_STATUS).withRel(Rel.SELF));
				add(response.getLink(Rel.SELF).withRel(Rel.RUN));
			}

		}
	}

	public static class Result {

		private final Object value;
		private final Exception error;

		private Result(Object value, Exception error) {
			this.value = value;
			this.error = error;
		}

		public static Result withValue(Object result) {
			return new Result(result, null);
		}

		public static Result withError(Exception error) {
			Exception refException = new Exception();
			hideFrameworkStack(error, refException);
			return new Result(null, error);
		}

		private static void hideFrameworkStack(Exception error, Exception refException) {
			LinkedList<StackTraceElement> actual = new LinkedList<>(Arrays.asList(error.getStackTrace()));
			LinkedList<StackTraceElement> ref = new LinkedList<>(Arrays.asList(refException.getStackTrace()));
			while (!ref.isEmpty() && actual.size() > 1 && ref.getLast().equals(actual.getLast())) {
				ref.removeLast();
				actual.removeLast();
			}
			error.setStackTrace(actual.toArray(new StackTraceElement[0]));
		}

		public Object getValue() {
			return value;
		}

		public Exception getError() {
			return error;
		}

		public static class Response extends ResourceSupport {

			public final Run.Result result;

			public Response(Run.Result result, Run.Response response) {
				this.result = result;
				add(response.getLink(Rel.RUN_RESULT).withRel(Rel.SELF));
				add(response.getLink(Rel.SELF).withRel(Rel.RUN));
			}

		}
	}

	public static class Request {
		public Params params;
	}

	public static class Response extends ResourceSupport {

		public final Run.Request request;
		public final Run.Status status;
		public final Run.Result result;

		public Response(Run run, ResourceSupport parent, String parentId, String parentRel,
				Class<? extends RunnableController> parentController, long runId) {
			this.request = run.request;
			this.status = run.getStatus();
			this.result = run.result;
			add(linkTo(methodOn(parentController).getRun(parentId, runId)).withSelfRel());
			add(linkTo(methodOn(parentController).getRunParams(parentId, runId)).withRel(Rel.RUN_PARAMS));
			add(linkTo(methodOn(parentController).getRunResult(parentId, runId)).withRel(Rel.RUN_RESULT));
			add(linkTo(methodOn(parentController).getRunStatus(parentId, runId)).withRel(Rel.RUN_STATUS));
			add(parent.getLink(Rel.SELF).withRel(parentRel));
		}

	}

}
