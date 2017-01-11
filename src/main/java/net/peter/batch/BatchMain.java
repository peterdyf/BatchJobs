package net.peter.batch;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

public class BatchMain extends AbstractAnnotationConfigDispatcherServletInitializer {

	/**
	 * For Server runtime only<br>
	 * No need for Spring Boot
	 * 
	 * @author Peter.DI
	 *
	 */
	public static class WebContext {
		/**
		 * for Login page return redirect:URL<br>
		 * Need to set Order to prevent overriding SBA's BeanNameViewResolver
		 * <br>
		 * No need set in Spring Boot for Spring Boot provided it by default<br>
		 * 
		 * 
		 * @return ContentNegotiatingViewResolver
		 * @see /META-INF/spring/batch/override/resources-context.xml
		 */
		@Bean
		public InternalResourceViewResolver internalResourceViewResolver() {
			return new InternalResourceViewResolver() {
				{
					setOrder(LOWEST_PRECEDENCE);
				}
			};
		}

	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		System.setProperty(DocumentBuilderFactory.class.getName(), DocumentBuilderFactoryImpl.class.getName());
		return new Class[] { WebContext.class, ScanConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] {};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		// for MDC
		servletContext.addFilter("mdcInsertingServletFilter", new MDCInsertingServletFilter()).addMappingForUrlPatterns(null, false, "/*");

		// Some Server like Tomcat getPathInfo() return null
		servletContext.addFilter("pathInfoFilter", new PathInfoFilter()).addMappingForUrlPatterns(null, false, "/*");

		// for Etag
		servletContext.addFilter("shallowEtagHeaderFilter", new ShallowEtagHeaderFilter()).addMappingForUrlPatterns(null, false, "/*");
		// for RESTful
		servletContext.addFilter("hiddenHttpMethodFilter", new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, false, "/*");

		super.onStartup(servletContext);
	}

	/**
	 * 
	 * Spring Batch Admin JobController uses getPathInfo().<br>
	 * However, getPathInfo() will return null in some servers like Tomcat 7.
	 * <br>
	 * Use getServletPath() to replace it.
	 * 
	 * @author Peter.DI
	 * @see org.springframework.batch.admin.web.JobController
	 */
	public static class PathInfoFilter implements Filter {
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			// empty
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			if (request instanceof HttpServletRequest) {
				chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {
					public String getPathInfo() {
						String pathInfo = super.getPathInfo();
						if (pathInfo == null) {
							return getServletPath();
						}
						return pathInfo;
					}
				}, response);
			} else {
				chain.doFilter(request, response);
			}

		}

		@Override
		public void destroy() {
			// empty
		}
	}
}
