package org.uma.jmetal.service.register.operator;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.operator.Operator;
import org.uma.jmetal.service.register.MapRegister;

@Repository
public class OperatorRegister extends MapRegister<String, Operator<?>> {

}
