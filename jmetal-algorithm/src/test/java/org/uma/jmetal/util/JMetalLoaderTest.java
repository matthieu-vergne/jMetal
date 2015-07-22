package org.uma.jmetal.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;

public class JMetalLoaderTest {

	@BeforeClass
	public static void setup() {
		JMetalLoader.logger.setLevel(Level.ALL);
		for (Handler handler : JMetalLoader.logger.getParent().getHandlers()) {
			handler.setLevel(Level.ALL);
		}
	}

	/*
	 * Does not work because the reference class is Algorithm, which is declared
	 * in the core module, which uses a dedicated folder for class files.
	 */
	@Test
	public void testDefaultAlgorithmsAreRetrieved() {
		JMetalLoader loader = new JMetalLoader();

		Collection<Algorithm<?>> algorithms = loader.getAvailableAlgorithms();
		assertNotNull(algorithms);
		assertFalse(algorithms.isEmpty());
	}

}
