package net.peter.batch.register;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.peter.batch.register.TestChildApplicationBuilder.ParentClassContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ParentClassContext.class })
public class TestChildApplicationBuilder {

	@Autowired
	ApplicationContext parent;

	private ChildApplicationBuilder childApplicationBuilder;

	@Before
	public void setUp() {
		childApplicationBuilder = new ChildApplicationBuilder(parent);
	}

	@Test
	public void test() throws DuplicateJobException {

		ApplicationContext childContext = childApplicationBuilder.build(ChildClassContext.class);
		assertThat("parent bean", childContext.getBean(ParentClass.class), instanceOf(ParentClass.class));
		assertThat("child bean", childContext.getBean(ChildClass.class), instanceOf(ChildClass.class));
	}

	public static class ParentClass {

	}

	public static class ChildClass {

	}

	@Configuration
	public static class ParentClassContext {
		@Bean
		public ParentClass parentClass() {
			return new ParentClass();
		}
	}

	@Configuration
	public static class ChildClassContext {
		@Bean
		public ChildClass childClass() {
			return new ChildClass();
		}
	}
}
