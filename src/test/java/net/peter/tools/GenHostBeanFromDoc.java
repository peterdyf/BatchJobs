package net.peter.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jooq.tools.csv.CSVReader;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;

@SuppressWarnings("PMD") // tools
public class GenHostBeanFromDoc {

	Pattern p1 = Pattern.compile("(.*)\\((.*)\\)(.*)");
	Pattern p2 = Pattern.compile("Occurs (.*) times");

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new GenHostBeanFromDoc().gen();
	}

	private void gen() throws FileNotFoundException, IOException {

		StringBuilder code = new StringBuilder();
		StringBuilder testCode = new StringBuilder();

		try (CSVReader reader = new CSVReader(new FileReader("c:/peter/work/host/gen.csv"))) {
			String[] nextLine;

			Optional<Integer> child = Optional.absent();
			Optional<AtomicInteger> childCount = Optional.absent();
			AtomicInteger count = new AtomicInteger();

			while ((nextLine = reader.readNext()) != null) {
				if (nextLine == null || (Stream.of(nextLine).collect(Collectors.joining()).trim().isEmpty())) {
					continue;
				}
				String name = nextLine[1].replaceAll("\\.", "").replaceAll("-", " ").replaceAll(" ", "_");
				String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
				String typeStr = nextLine[2];

				testCode.append(format("assertThat(\"%s\", obj.%s, equalTo(\"\"));\n", fieldName, fieldName));

				if (child.isPresent()) {
					code.append(format("\n@Order(%s)\n", childCount.get().incrementAndGet()));
				} else {
					code.append(format("\n@Order(%s)\n", count.incrementAndGet()));
				}

				Matcher m = p1.matcher(typeStr);
				if (m.find()) {
					String typeMark = m.group(1);
					int length = Integer.parseInt(m.group(2));

					if ("X".equals(typeMark)) {
						code.append(format("@StringType(length = %s)\nString %s;\n", length, fieldName));
					} else if ("9".equals(typeMark) && 8 == length && (StringUtils.containsIgnoreCase(fieldName, "date") || StringUtils.containsIgnoreCase(fieldName, "time"))) {
						code.append(format("@DateType\nDate %s;\n", fieldName));
					} else if ("9".equals(typeMark) || "+9".equals(typeMark)) {
						if (typeMark.startsWith("+")) {
							length++;
						}

						if ("V99".equalsIgnoreCase(m.group(3))) {
							length += 2;
							code.append(format("@NumberType(length = %s, scale = 2)\nBigDecimal %s;\n", length, fieldName));
						}
						else if ("V9999".equalsIgnoreCase(m.group(3))||"V9(4)".equalsIgnoreCase(m.group(3))) {
							code.append(format("@NumberType(length = %s, scale = 4)\nBigDecimal %s;\n", length, fieldName));
						}
						else if (m.group(3).isEmpty()) {
							code.append(format("@NumberType(length = %s)\nBigDecimal %s;\n", length, fieldName));
						}
						else {
							throw new RuntimeException(format("Unsupport Type:[%s] at %s", typeStr, fieldName));
						}
						
					} else {
						throw new RuntimeException(format("Unsupport Type:[%s] at %s", typeStr, fieldName));
					}

					if (childCount.isPresent() && childCount.get().get() == child.get()) {
						child = Optional.absent();
						childCount = Optional.absent();
						code.append("}\n");
					}

				} else {
					Matcher m2 = p2.matcher(typeStr);
					if (m2.find()) {
						int length = Integer.parseInt(m2.group(1));
						child = Optional.of(length);
						childCount = Optional.of(new AtomicInteger());
						String childClassName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
						code.append(format("@ChildType(%s)\n", length));
						code.append(format("List<%s> %ss;\n", childClassName, fieldName));
						code.append(format("public static class %s {\n", childClassName));
					}
				}

			}
		}

		System.out.println(code);

		System.out.println("\n\n\n/////////////////////// in Test /////////////////////////\n\n\n");

		System.out.println(testCode);

	}

	private String format(String format, Object... args) {
		return String.format(format, args);
	}

}
