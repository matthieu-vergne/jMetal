package org.uma.jmetal.util;

import static org.junit.Assert.*;

import java.util.logging.Handler;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.ErrorRatio;
import org.uma.jmetal.qualityindicator.impl.SetCoverage;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.naming.impl.DescribedEntitySet;

public class JMetalLoaderTest {

	@BeforeClass
	public static void setup() {
		JMetalLoader.logger.setLevel(Level.ALL);
		for (Handler handler : JMetalLoader.logger.getParent().getHandlers()) {
			handler.setLevel(Level.ALL);
		}
	}

	@Test
	public void testDefaultQualityIndicatorsAreRetrieved() {
		JMetalLoader loader = new JMetalLoader();
		Front referenceFront = new ArrayFront();

		DescribedEntitySet<QualityIndicator<?, ?>> indicators = loader
				.getAvailableQualityIndicators(referenceFront);
		assertNotNull(indicators);

		SetCoverage setCoverage = indicators.get("SC");
		assertNotNull(setCoverage);

		Epsilon<?> epsilon = indicators.get("EP");
		assertNotNull(epsilon);

		ErrorRatio<?> errorRatio = indicators.get("ER");
		assertNotNull(errorRatio);
	}

}
