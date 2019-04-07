package org.uma.jmetal.service.register.operator;

import java.util.function.Function;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.MapRegister;

@Repository
public class OperatorRegister extends MapRegister<String, Function<Run.Params, Object>> {

}
