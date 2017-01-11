package net.peter.batch.common.tasklet;

import static com.cncbinternational.common.util.MyDate.date;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import net.peter.batch.constant.JobConvention;
import net.peter.batch.service.LocalFilesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class CheckPlatFileDateTask implements Tasklet {

	@Autowired
	private LocalFilesService localFilesService;

	private final String fileName;

	public CheckPlatFileDateTask(String fileName) {
		Preconditions.checkNotNull(fileName);
		this.fileName = fileName;
	}

	@Override
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

		Object startTimeObj = chunkContext.getStepContext().getJobParameters().get(JobConvention.JOB_PARAMETER_START_TIME);
		Preconditions.checkNotNull(startTimeObj, "Not Found Job Parameter:startTime");
		String startTimeStr = startTimeObj.toString();

		Resource file = localFilesService.loadLocalResource(fileName);
		try (BufferedReader brTest = new BufferedReader(new FileReader(file.getFile()))) {
			String firstLine = brTest.readLine();
			String objectStr = date(startTimeStr).fileString();
			if (!StringUtils.startsWith(trimUnwantedUnicodeCharacters(firstLine), objectStr)) {
				throw new FileDateNotTodayException(file.getFile().getAbsolutePath(), firstLine, objectStr);
			}
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}

		return RepeatStatus.FINISHED;
	}

	private static String trimUnwantedUnicodeCharacters(String firstLine) {
		return firstLine.replaceAll("[\uFEFF-\uFFFF]", "");
	}

	public static class FileDateNotTodayException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public FileDateNotTodayException(String file, String firstLine, String objectStr) {
			super(String.format("File [%s] excepted starting with [%s], but starting with [%s]", file, objectStr, firstLine));
		}
	}

}
