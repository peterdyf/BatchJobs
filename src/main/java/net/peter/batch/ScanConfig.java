package net.peter.batch;

import net.peter.batch.register.JobsRegister;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import net.peter.batch.annotation.JobConfiguration;

/**
 * JobConfiguration will be handled by separated child ApplicationContext <br>
 * to avoid duplicate of bean name.<br>
 * So skip Scan here.
 * 
 * @see JobsRegister
 */
@Configuration
@ComponentScan(value = { "com.cncbinternational" }, excludeFilters = @ComponentScan.Filter(value = JobConfiguration.class, type = FilterType.ANNOTATION) )
public class ScanConfig {
}