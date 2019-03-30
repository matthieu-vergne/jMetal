package org.uma.jmetal.service;

public class Link {
	public static final String REL_SELF = org.springframework.hateoas.Link.REL_SELF;
	
	public static final String REL_ALGORITHM = "algorithm";
	public static final String REL_OPERATOR = "operator";
	
	public static final String REL_PARAMS_DEFINITION = "params definition";
	public static final String REL_PARAMS_EXAMPLE = "params example";
	
	public static final String REL_RESULT_DEFINITION = "result definition";
	public static final String REL_RESULT_EXAMPLE = "result example";
	
	public static final String REL_RUNS = "runs";
	public static final String REL_RUN = "run";
	public static final String REL_RUN_PARAMS = "run params";
	public static final String REL_RUN_RESULT = "run result";
	public static final String REL_RUN_STATUS = "run status";
}
