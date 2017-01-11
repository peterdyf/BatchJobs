package net.peter.tools;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.cncbinternational.spring.util.ReflectionUtil;

@SuppressWarnings("PMD") // tools
public class StaticSqlFileName {

	Set<String> allStaticValue;
	Set<String> allSqlFileNames;

	@Before
	public void setUp() {
		allStaticValue = getAllClasses().stream().flatMap(c -> ReflectionUtil.getStaticFields(c).stream().map(f -> ReflectionUtil.getValue(f, null))).filter(o -> o != null).map(String::valueOf)
				.collect(Collectors.toSet());
		Set<String> fullSqlNames = new Reflections("com.cncbinternational.batch.jobs", new ResourcesScanner()).getResources(x -> StringUtils.endsWithIgnoreCase(x, ".sql"));
		allSqlFileNames = fullSqlNames.stream().map(this::getSimpleName).collect(Collectors.toSet());

	}

	private String getSimpleName(String f) {
		return f.substring(f.lastIndexOf("/") + 1);
	}

	@Test
	public void check() {
		Set<String> unused = new HashSet<>(allSqlFileNames);

		Set<String> errors = new HashSet<>();

		allSqlFileNames.forEach(f -> {
			allStaticValue.forEach(v -> {
				if (StringUtils.containsIgnoreCase(v, f)) {
					unused.remove(f);
					if (!StringUtils.contains(v, f)) {
						errors.add(f);
					}
				}
			});
		});

		if (!unused.isEmpty() || !errors.isEmpty()) {
			throw new RuntimeException("unused: " + unused + "\nerror: " + errors);
		}

	}

	private Set<Class<?>> getAllClasses() {
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(
						false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.cncbinternational"))));

		return reflections.getSubTypesOf(Object.class);
	}

}
