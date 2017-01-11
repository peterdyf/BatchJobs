package net.peter.test.batch;

import java.io.File;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.cncbinternational.spring.constant.ProfileNames;

@Service
@Profile(ProfileNames.TEST)
public class TestFileUtilLocal extends AbstractTestFileUtil {

	public String buildFileName(String fileName) {
		return "C:/BatchReports" + File.separator + fileName;
	}
}
