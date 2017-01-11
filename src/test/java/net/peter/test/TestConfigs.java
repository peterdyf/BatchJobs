package net.peter.test;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.config.configuration.ConfigurationDevConfig;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@Profile(ProfileNames.TEST)
@Import({ DbConfig4Test.class, ConfigurationDevConfig.class, FilesService.class })
public class TestConfigs {

}
