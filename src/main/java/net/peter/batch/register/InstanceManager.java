package net.peter.batch.register;

import java.util.Collection;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;

/**
 * 
 * A Proxy to JobRegistry
 * 
 * @author Peter.DI
 * @see JobsRegister
 *
 */
@Service
public class InstanceManager {

	private final JobRegistry jobRegistry;

	@Autowired
	public InstanceManager(JobRegistry jobRegistry) {
		this.jobRegistry = jobRegistry;
	}

	public void register(JobFactory jobFactory) {
		try {
			jobRegistry.register(jobFactory);
		} catch (DuplicateJobException e) {
			throw Throwables.propagate(e);
		}
	}

	public Job instance(String name) {
		try {
			return jobRegistry.getJob(name);
		} catch (NoSuchJobException e) {
			throw Throwables.propagate(e);
		}
	}

	public boolean hasInstance(String name) {
		return jobRegistry.getJobNames().contains(name);
	}

	public Collection<String> allInstanceNames() {
		return jobRegistry.getJobNames();
	}
}
