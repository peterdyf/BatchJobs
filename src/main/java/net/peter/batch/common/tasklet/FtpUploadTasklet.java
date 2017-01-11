package net.peter.batch.common.tasklet;

import net.peter.batch.service.FtpService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

public class FtpUploadTasklet implements SimpleTasklet {

	@Autowired
	private FtpService ftpService;
	private final String fileName;
	private final String ftpFolder;

	public FtpUploadTasklet(String fileName, String ftpFolder) {
		Preconditions.checkNotNull(fileName);
		Preconditions.checkNotNull(ftpFolder);
		this.fileName = fileName;
		this.ftpFolder = ftpFolder;
	}

	public FtpUploadTasklet(String fileName) {
		Preconditions.checkNotNull(fileName);
		this.fileName = fileName;
		this.ftpFolder = ".";
	}

	public void execute() {
		ftpService.send(fileName, ftpFolder);
	}
}
