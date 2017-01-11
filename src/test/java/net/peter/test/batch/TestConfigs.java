package net.peter.test.batch;

import net.peter.test.DbConfig4Test;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.cncbinternational.common.service.FilesService;
import com.cncbinternational.spring.config.configuration.ConfigurationDevConfig;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@Profile(ProfileNames.TEST)
@Import({ JobConfig4Test.class, DbConfig4Test.class, ConfigurationDevConfig.class, FilesService.class, TestFileUtilFTP.class })
public class TestConfigs {

}
