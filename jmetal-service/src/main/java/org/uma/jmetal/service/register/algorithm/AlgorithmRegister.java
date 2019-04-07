package org.uma.jmetal.service.register.algorithm;

import java.util.function.Function;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.algorithm.Algorithm;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.MapRegister;

@Repository
public class AlgorithmRegister extends MapRegister<String, Algorithm> {

	public void store(String id, Function<Run.Params, Object> function) {
		store(id, new Algorithm(function, ParamsDefinition.empty()));
	}

}
