package org.uma.jmetal.util.naming.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleDescribedEntityTest {

	@Test
	public void testSetGetName() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setName("test");
		assertEquals("test", entity.getName());

		entity.setName("abc");
		assertEquals("abc", entity.getName());
	}

	@Test
	public void testSetGetDescription() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setDescription("test");
		assertEquals("test", entity.getDescription());

		entity.setDescription("abc");
		assertEquals("abc", entity.getDescription());
	}

	@Test
	public void testCorrectNameWhenProvided() {
		String name = "named measure";
		assertEquals(name, new SimpleDescribedEntity(name).getName());
		assertEquals(name,
				new SimpleDescribedEntity(name, "description").getName());
	}

	@Test
	public void testCorrectDescriptionWhenProvided() {
		String description = "My measure description is awesome!";
		assertEquals(description, new SimpleDescribedEntity("measure",
				description).getDescription());
	}

	@Test
	public void testNullNameForCompleteConstructorWithNullName() {
		assertNull(new SimpleDescribedEntity(null, "Test").getName());
	}

	@Test
	public void testNullDescriptionForCompleteConstructorWithNullDescription() {
		assertNull(new SimpleDescribedEntity("Test", null).getDescription());
	}

	@Test
	public void testNullNameForNameOnlyConstructorWithNullName() {
		assertNull(new SimpleDescribedEntity(null).getName());
	}

	@Test
	public void testNullDescriptionForNamleOnlyConstructor() {
		assertNull(new SimpleDescribedEntity("Test").getDescription());
	}

	@Test
	public void testNullNameForEmptyConstructor() {
		assertNull(new SimpleDescribedEntity().getName());
	}

	@Test
	public void testNullDescriptionForEmptyConstructor() {
		assertNull(new SimpleDescribedEntity().getDescription());
	}
}
