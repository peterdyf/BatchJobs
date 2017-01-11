
package net.peter.batch.service;

import static net.peter.batch.constant.ConfigConstant.CONFIG_GEN_FILES;
import static net.peter.batch.constant.ConfigConstant.GEN_FILES_FTP_GET_SHELL;
import static net.peter.batch.constant.ConfigConstant.GEN_FILES_FTP_PUT_SHELL;
import static net.peter.batch.constant.ConfigConstant.GEN_FILES_FTP_USER;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cncbinternational.common.service.SysParamService;
import com.cncbinternational.spring.annotation.Loggable;
import com.cncbinternational.spring.template.ConfigurationConfigTemplate.Configs;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

@Service
public class FtpService {

	private static final String SPACE = " ";

	@Autowired
	private SysParamService sysParamService;

	@Autowired
	private LocalFilesService localFilesService;

	@Autowired
	private Configs configs;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Loggable
	public void send(String filePath) {
		String ftpFolder = ".";
		send(filePath, ftpFolder);
	}

	@Loggable
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public void send(String fileName, String ftpFolder) {
		String filePath = localFilesService.buildLocalFilePath(fileName);
		File file = new File(filePath);
		Preconditions.checkState(file.exists(), "Not Found " + filePath);
		String destFileName = fileName;
		String folder = file.getParent();
		String sourceDir = folder + File.separator;
		String remoteDir = ftpFolder;
		String user = getFtpUser();
		String host = sysParamService.getString("RPT_SRV_IP");
		String script = buildSendScript(user, host, remoteDir, sourceDir, fileName, destFileName);

		try {
			log.debug("Run Script [{}]", script);
			Process process = Runtime.getRuntime().exec(script);
			BufferedReader stdInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String stdOut = IOUtils.toString(stdInputReader);
			String stdError = IOUtils.toString(stdErrorReader);

			if (!StringUtils.contains(stdOut, "Done uploading...")) {
				log.error("FTP [{}] from [{}] to [{}/{}] result with Error: [{}]\n[{}]", fileName, folder, host, remoteDir, stdOut, stdError);
				throw new IOException(stdOut);
			}

			log.debug("FTP [{}] from [{}] to [{}/{}] result: [{}]", fileName, folder, host, remoteDir, stdOut);

		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Loggable
	public void download(String fileName) {
		String ftpFolder = ".";
		download(fileName, ftpFolder);
	}

	@Loggable
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public void download(String fileName, String ftpFolder) {

		localFilesService.clean(fileName);

		String destPath = localFilesService.getFileFolder() + File.separator;
		String remoteDir = ftpFolder;
		String user = getFtpUser();
		String host = sysParamService.getString("RPT_SRV_IP");
		String script = buildDownloadScript(user, host, remoteDir, destPath, fileName);

		try {
			log.debug("Run Script [{}]", script);
			Process process = Runtime.getRuntime().exec(script);
			process.waitFor(1, TimeUnit.MINUTES);
			BufferedReader stdInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String stdOut = IOUtils.toString(stdInputReader);
			String stdError = IOUtils.toString(stdErrorReader);

			if (!StringUtils.contains(stdOut, "Done downloading...")) {
				log.error("FTP [{}] from [{}/{}] to [{}{}] result with Error: [{}]\n[{}]", fileName, host, remoteDir, destPath, fileName, stdOut, stdError);
				throw new IOException(stdOut);
			}

			log.debug("FTP [{}] from [{}/{}] to [{}{}] result: [{}]", fileName, host, remoteDir, destPath, fileName, stdOut);

			if (!localFilesService.checkExists(fileName)) {
				throw new FtpDownloadFailedException(fileName);
			}

		} catch (IOException | InterruptedException e) {
			throw Throwables.propagate(e);
		}
	}

	@SuppressWarnings("PMD.UseObjectForClearerAPI") // Existing SH
	private String buildSendScript(String user, String host, String remoteDir, String sourceDir, String filename, String destFileName) {
		//@formatter:off
		return new StringBuilder()
				.append(getPutShell()).append(SPACE)
				.append(user).append(SPACE)
				.append(host).append(SPACE)
				.append(remoteDir).append(SPACE)
				.append(sourceDir).append(SPACE)
				.append(filename).append(SPACE)
				.append(destFileName).toString();
		//@formatter:on
	}

	@SuppressWarnings("PMD.UseObjectForClearerAPI") // Existing SH
	private String buildDownloadScript(String user, String host, String sourceDir, String destDir, String filename) {
		//@formatter:off
		return new StringBuilder()
				.append(getGetShell()).append(SPACE)
				.append(user).append(SPACE)
				.append(host).append(SPACE)
				.append(sourceDir).append(SPACE)
				.append(destDir).append(SPACE)
				.append(filename).toString();
		//@formatter:on
	}

	private String getPutShell() {
		return configs.getString(CONFIG_GEN_FILES, GEN_FILES_FTP_PUT_SHELL);
	}

	private String getGetShell() {
		return configs.getString(CONFIG_GEN_FILES, GEN_FILES_FTP_GET_SHELL);
	}

	private String getFtpUser() {
		return configs.getString(CONFIG_GEN_FILES, GEN_FILES_FTP_USER);
	}

	public static class FtpDownloadFailedException extends RuntimeException {

		private static final long serialVersionUID = 3891987504721803693L;
		private final String fileName;

		public FtpDownloadFailedException(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		public String getMessage() {
			return String.format("Ftp download failed. File [%s] not Found.", fileName);
		}
	}

}
