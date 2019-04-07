package org.uma.jmetal.service.register.operator;

import java.util.function.Function;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.model.runnable.ParamsDefinition;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.MapRegister;

@Repository
public class OperatorRegister extends MapRegister<String, Operator> {

	public void store(String id, Function<Run.Params, Object> operator) {
		store(id, new Operator(operator, ParamsDefinition.empty()));
	}

}
