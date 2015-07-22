package org.uma.jmetal.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;
import org.uma.jmetal.problem.Problem;

public class JMetalLoaderTest {

	@BeforeClass
	public static void setup() {
		JMetalLoader.logger.setLevel(Level.ALL);
		for (Handler handler : JMetalLoader.logger.getParent().getHandlers()) {
			handler.setLevel(Level.ALL);
		}
	}

	/*
	 * Does not work because the reference class is Problem, which is declared
	 * in the core module, which uses a dedicated folder for class files.
	 */
	@Test
	public void testDefaultProblemsAreRetrieved() {
		JMetalLoader loader = new JMetalLoader();

		Collection<Problem<?>> problems = loader.getAvailableProblems();
		assertNotNull(problems);
		assertFalse(problems.isEmpty());
	}

}
