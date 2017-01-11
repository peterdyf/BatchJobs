package net.peter.tools;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.cncbinternational.spring.annotation.Order;
import com.cncbinternational.spring.util.ReflectionUtil;

@SuppressWarnings("PMD") // tools
public class StaticCheckDuplOrder {

	@Test
	public void check() {

		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(
						false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.cncbinternational"))));

		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);

		allClasses.stream().filter(c -> ReflectionUtil.getFieldsWithAnnoation(c, Order.class).size() > 0).forEach(StaticCheckDuplOrder::check);

	}

	public static void check(Class<?> c) {
		Set<Field> fields = ReflectionUtil.getFieldsWithAnnoation(c, Order.class);
		if (fields.stream().map(f -> f.getAnnotation(Order.class).value()).collect(Collectors.toSet()).size() < fields.size()) {
			throw new RuntimeException("Find duplcated in class:" + c);
		}

	}

}
