package net.peter.batch.common.tasklet;

import net.peter.batch.service.FtpService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

public class FtpDownloadTasklet implements SimpleTasklet {

	@Autowired
	private FtpService ftpService;
	private final String fileName;

	public FtpDownloadTasklet(String fileName) {
		Preconditions.checkNotNull(fileName);
		this.fileName = fileName;
	}

	public void execute() {
		ftpService.download(fileName);
	}
}
