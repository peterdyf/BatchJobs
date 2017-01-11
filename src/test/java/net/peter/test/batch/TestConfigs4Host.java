package net.peter.test.batch;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.cncbinternational.common.service.TransNoService;
import com.cncbinternational.common.storeprocedure.SpTransRefXSeqSel;
import com.cncbinternational.spring.constant.ProfileNames;

@Configurable
@Profile(ProfileNames.TEST)
@Import({TransNoService.class, SpTransRefXSeqSel.class })
public class TestConfigs4Host {

}
