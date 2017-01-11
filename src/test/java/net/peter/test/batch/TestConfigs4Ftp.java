package net.peter.test.batch;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import net.peter.batch.service.FtpService;
import net.peter.batch.service.LocalFilesService;
import com.cncbinternational.common.service.SysParamService;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@Profile(ProfileNames.TEST)
@Import({ FtpService.class, SysParamService.class, LocalFilesService.class })
public class TestConfigs4Ftp {

}
