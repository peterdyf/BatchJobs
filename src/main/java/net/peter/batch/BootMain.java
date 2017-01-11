package net.peter.batch;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

/**
 * Set Env spring.profiles.active before run if you need use Profile<br>
 * Sample in Eclipse->Run Configuration->Environment->add
 * 'spring.profiles.active' as 'ConfigDev'<br>
 * Sample in windows cmd, 'set spring.profiles.active=ConfigDev'<br>
 * Sample in Unix shell, 'export spring.profiles.active=ConfigDev'<br>
 * 
 * @author Peter.DI
 */
@EnableAutoConfiguration
@Import(ScanConfig.class)
public class BootMain {

	@SuppressWarnings("PMD.UseVarargs") // Main
	public static void main(String[] args) {
		System.setProperty(DocumentBuilderFactory.class.getName(), DocumentBuilderFactoryImpl.class.getName());
		SpringApplication application = new SpringApplication(BootMain.class);
		application.addListeners(new ApplicationPidFileWriter("app.pid"));
		application.run(args);
	}

	// for MDC
	@Bean
	public FilterRegistrationBean mdcInsertingServletFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new MDCInsertingServletFilter());
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		registration.addUrlPatterns("/*");
		return registration;
	}

	// for Etag
	@Bean
	public FilterRegistrationBean shallowEtagHeaderFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new ShallowEtagHeaderFilter());
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		registration.addUrlPatterns("/*");
		return registration;
	}

	// for RESTful
	@Bean
	public FilterRegistrationBean hiddenHttpMethodFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new HiddenHttpMethodFilter());
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		registration.addUrlPatterns("/*");
		return registration;
	}

	// Some Server getPathInfo() return null
	@Bean
	public FilterRegistrationBean pathInfoFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new BatchMain.PathInfoFilter());
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
		registration.addUrlPatterns("/*");
		return registration;
	}

}
