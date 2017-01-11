package net.peter.test.batch;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public abstract class AbstractTestFileUtil {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public abstract String buildFileName(String file);

	public int clean(String fileName) {
		try {
			File file = getFile(fileName);
			if (file != null) {
				log.debug("Delete File [{}]", file);
				file.delete();
				return 1;
			}
			return 0;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public File getFile(String fileName) throws IOException {
		File file = new File(buildFileName(fileName));
		if (file.exists()) {
			return file;
		}
		return null;
	}

	public List<String> readFile(String fileName) throws IOException {
		File file = getFile(fileName);
		return FileUtils.readLines(file);
	}

	public void writeFile(String text, String fileName) throws IOException {
		File file = new File(buildFileName(fileName));
		FileUtils.writeStringToFile(file, text);
	}

}