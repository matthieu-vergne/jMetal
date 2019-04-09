package org.uma.jmetal.service.register.algorithm;

import org.springframework.stereotype.Repository;
import org.uma.jmetal.service.model.algorithm.Algorithm;
import org.uma.jmetal.service.register.MapRegister;

@Repository
public class AlgorithmRegister extends MapRegister<String, Algorithm<?>> {

}
