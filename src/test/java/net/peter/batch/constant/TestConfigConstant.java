package net.peter.batch.constant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

public class TestConfigConstant {

	@Test
	public void test() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<ConfigConstant> constructor = ConfigConstant.class.getDeclaredConstructor();
		assertThat("private constructor", Modifier.isPrivate(constructor.getModifiers()), equalTo(true));
		constructor.setAccessible(true);
		constructor.newInstance();
	}
}
