
package net.peter.batch.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Spring Batch Admin default 
 * @author Peter.DI
 *
 */
@Configuration
@ImportResource("classpath:/org/springframework/batch/admin/web/resources/servlet-config.xml")
public class ServletConfig {

}
