package net.peter.batch.service;

import static net.peter.batch.constant.ConfigConstant.CONFIG_GEN_FILES;
import static net.peter.batch.constant.ConfigConstant.GEN_FILES_REPORT_WORKING_PATH;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.annotation.Loggable;
import com.cncbinternational.spring.template.ConfigurationConfigTemplate.Configs;

@Service
public class LocalFilesService {

	private static final String BAK_FILE_EXTENSION = ".BAK";

	@Autowired
	private Configs configs;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private FilesService filesService;

	@Loggable
	public Resource loadLocalResource(String fileName) {
		return applicationContext.getResource("file:" + buildLocalFilePath(fileName));
	}

	@Loggable
	public String buildLocalFilePath(String fileName) {
		return getFileFolder() + File.separator + fileName;
	}

	@Loggable
	public void backupFile(String fileName) {

		String origFile = buildLocalFilePath(fileName);
		String bakFile = buildLocalFilePath(fileName) + BAK_FILE_EXTENSION;

		filesService.copyFile(origFile, bakFile);
	}

	@Loggable
	public String getFileFolder() {
		return configs.getString(CONFIG_GEN_FILES, GEN_FILES_REPORT_WORKING_PATH);
	}

	@Loggable
	public void clean(String fileName) {
		File file = new File(buildLocalFilePath(fileName));
		if (file.exists()) {
			file.delete();
		}
	}

	@Loggable
	public boolean checkExists(String fileName) {
		File file = new File(buildLocalFilePath(fileName));
		return file.exists();
	}

}
