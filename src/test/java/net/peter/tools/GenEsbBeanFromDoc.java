package net.peter.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.tools.csv.CSVReader;

import com.google.common.base.CaseFormat;

@SuppressWarnings("PMD") // tools
public class GenEsbBeanFromDoc {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new GenEsbBeanFromDoc().gen();
	}

	private void gen() throws FileNotFoundException, IOException {

		StringBuilder code = new StringBuilder();
		StringBuilder testCode = new StringBuilder();

		try (CSVReader reader = new CSVReader(new FileReader("c:/peter/work/esb/gen.csv"))) {
			String[] nextLine;

			while ((nextLine = reader.readNext()) != null) {
				if (nextLine == null || (Stream.of(nextLine).collect(Collectors.joining()).trim().isEmpty())) {
					continue;
				}
				String name = nextLine[0].replaceAll("\\.", "").replaceAll("-", " ").replaceAll(" ", "_");
				String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
				String typeStr = nextLine[1];
				String tagName = nextLine[4];

				testCode.append(format("assertThat(\"%s\", obj.%s, equalTo(\"\"));\n", fieldName, fieldName));

				String type = null;
				if (typeStr.startsWith("X")) {
					type = "String";
					code.append(format("@XmlElement(name = \"%s\")\n%s %s;\n\n", tagName, type, fieldName));
				} else if (typeStr.startsWith("9")) {
					type = "Integer";
					code.append(format("@XmlElement(name = \"%s\")\n%s %s;\n\n", tagName, type, fieldName));
				} else if (typeStr.startsWith("Timestamp")) {
					type = "Date";
					code.append(format("@XmlElement(name = \"%s\")\n%s %s;\n\n", tagName, type, fieldName));
				} else if (typeStr.equals("-")) {
					if (fieldName.endsWith("Table")) {
						fieldName = fieldName.substring(0, fieldName.length() - 5);
					}
					type = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
					
					fieldName = fieldName + "s";
					
					code.append(format("@XmlElementWrapper(name = \"%s\")\n", tagName));
					code.append(format("@XmlElement(name = \"%s\")\n", tagName));
					code.append(format("List<%s> %s = new ArrayList<%s>();\n\n", type, fieldName, type));
					code.append(format("@XmlAccessorType(XmlAccessType.FIELD) public static class %s{}", type));
				} else {
					throw new RuntimeException("Not support type:" + typeStr);
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
