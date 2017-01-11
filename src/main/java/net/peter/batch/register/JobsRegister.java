package net.peter.batch.register;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import net.peter.batch.constant.JobConvention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

//@formatter:off
/**
 * <br>Intention:</br><br>
 * To prevent bean name duplication in example below, each job has its own child ApplicationContext. <br>
 * It only a simulation of '(modular=true) from the logic of <code>@EnableBatchProcessing<code>.<br>
 * Since Spring Batch Admin <b>DOES NOT</b> support <code>@EnableBatchProcessing<code>.<br>
 * <p>
 * <b>Bean Name Duplication without child ApplicationContext Example</b><br>
 * In Job1<br>
 * 
 *  <pre>
 * <code>  
 * {@literal @}StepBean
 * ItemReader<SomeData1> reader(){
 * }
 * </code>
 * </pre>
 * will register a bean named 'reader' in ApplicationContext.<br>
 * 
 * In Job2<br>
 * 
 *  <pre>
 * <code>  
 * {@literal @}StepBean
 * ItemReader<SomeData2> reader(){
 * }
 * </code>
 * </pre>
 * will also register a bean named 'reader' in ApplicationContext.<br>
 * So, in Job1 
 *  <pre>
 *  <code>
 *	{@literal @}Bean
 *	Step step() {
 *	return stepBuilder(STEP_NAME)
 *		.{@literal <}SomeData1, SomeData1{@literal >} chunk(CHUNK_SIZE)	
 *		.reader(reader())
 *		...
 *	}
 *	</code>
 *	</pre>
 * and Job2
 *  <pre>
 *  <code>
 *	{@literal @}Bean
 *	Step step() {
 *	return stepBuilder(STEP_NAME)
 *		.{@literal <}SomeData2, SomeData2{@literal >} chunk(CHUNK_SIZE)	
 *		.reader(reader())
 *		...
 *	}
 *	</code>
 *	</pre>
 * Both 'reader()' would refer to the same bean,<br>
 * depend on which 'reader' registered later.
 * 
 * 
 *@author Peter.DI
 *@see org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
 */
//@formatter:on
@Component
public class JobsRegister {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final InstanceManager jobRegistry;

	private final ChildApplicationBuilder childApplicationBuilder;

	@Autowired
	public JobsRegister(InstanceManager jobRegistry, ChildApplicationBuilder childApplicationBuilder) {
		this.jobRegistry = jobRegistry;
		this.childApplicationBuilder = childApplicationBuilder;
	}

	@PostConstruct
	void init() {
		List<Class<?>> loadJobConfigClasses = loadJobConfigClasses();
		loadJobConfigClasses.forEach(this::loadOneJob);
	}

	private void loadOneJob(Class<?> jobConfig) {
		ApplicationContext child = childApplicationBuilder.build(jobConfig);
		JobFactory job = child.getBean(JobFactory.class);
		log.debug("Registering job: {} ", job.getJobName());
		jobRegistry.register(job);
	}

	private List<Class<?>> loadJobConfigClasses() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		new ClassPathBeanDefinitionScanner(context).scan(JobConvention.JOBS_PACKAGE);
		return Stream.of(context.getBeanDefinitionNames()).map(context::getBeanDefinition).map(BeanDefinition::getBeanClassName).map(this::loadClass)
				.filter(c -> c.isAnnotationPresent(JobConvention.JOBS_ANNOTATION)).collect(Collectors.toList());
	}

	private Class<?> loadClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw Throwables.propagate(e);
		}
	}

}
