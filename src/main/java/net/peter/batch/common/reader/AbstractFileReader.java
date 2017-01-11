package net.peter.batch.common.reader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;

import net.peter.batch.service.LocalFilesService;
import com.google.common.base.Preconditions;

/**
 * 
 * Read Bean from File<br>
 * Order determined by <code>@Order</code> on the Bean Field<br>
 * 
 * @see com.cncbinternational.spring.annotation.Order
 * @see LocalFilesService
 */
public abstract class AbstractFileReader<T> extends SimulatedFlatFileItemReader<T> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private LocalFilesService localFilesService;

	private final Class<T> beanClass;
	private final String fileName;

	public AbstractFileReader(String fileName, Class<T> beanClass) {
		super();
		Preconditions.checkNotNull(fileName);
		Preconditions.checkNotNull(beanClass);
		this.fileName = fileName;
		this.beanClass = beanClass;
	}

	@PostConstruct
	void init() {
		log.debug("Build File[{}]", fileName);
		setResource(localFilesService.loadLocalResource(fileName));
		setLineMapper(getLineMapper(beanClass));
	}

	protected abstract LineMapper<T> getLineMapper(Class<T> clazz);
}
