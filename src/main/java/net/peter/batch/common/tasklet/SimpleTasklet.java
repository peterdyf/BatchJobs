package net.peter.batch.common.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@FunctionalInterface
public interface SimpleTasklet extends Tasklet {

	@Override
	default RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		execute();
		return RepeatStatus.FINISHED;
	}

	void execute();
}
