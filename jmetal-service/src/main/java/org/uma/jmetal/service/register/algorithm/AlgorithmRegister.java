package org.uma.jmetal.service.register.algorithm;

import java.util.function.Function;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.runnable.Run;
import org.uma.jmetal.service.register.RegisterTemplate;

@Repository
public class AlgorithmRegister extends RegisterTemplate<String, Function<Run.Params, Object>> {

}
