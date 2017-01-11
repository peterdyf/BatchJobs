package net.peter.batch.common.writer;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import net.peter.batch.annotation.ReportHeader;
import net.peter.batch.service.LocalFilesService;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;

import com.cncbinternational.spring.util.OrderUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * 
 * Write Bean to File<br>
 * Order determined by <code>@Order</code> on the Bean Field<br>
 * Header is from the <code>@ReportHeader</code> value on the Bean Field <br>
 * 
 * <p>
 * 
 * File would generated in localfile path
 * 
 * <p>
 * 
 * 
 * @see com.cncbinternational.spring.annotation.Order
 * @see ReportHeader
 * @see LocalFilesService
 */
public abstract class AbstractFileWriter<T> extends FlatFileItemWriter<T> {

	@Autowired
	private LocalFilesService localFilesService;

	private final Class<T> beanClass;
	private final String fileName;

	public AbstractFileWriter(String fileName, Class<T> beanClass) {
		super();
		Preconditions.checkNotNull(fileName);
		Preconditions.checkNotNull(beanClass);
		this.fileName = fileName;
		this.beanClass = beanClass;
		setHeaderCallback(this::defaultWriteHeader);
		setFooterCallback(this::defaultWriteFooter);
	}

	@PostConstruct
	void init() {
		setResource(localFilesService.loadLocalResource(fileName));
		setLineAggregator(getLineAggregator(beanClass));
	}

	private void defaultWriteHeader(Writer writer) {
		try {
			writer.write(headerString());
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}

	private String headerString() {
		List<Field> fields = OrderUtil.getFieldsSortedByOrder(beanClass);
		return fields.stream().map(f -> {
			if (f.isAnnotationPresent(ReportHeader.class)) {
				return f.getAnnotation(ReportHeader.class).value();
			}
			return f.getName();
		}).collect(Collectors.joining(","));
	}

	@SuppressWarnings({ "PMD.UnusedFormalParameter", "PMD.EmptyMethodInAbstractClassShouldBeAbstract" })
	private void defaultWriteFooter(Writer writer) {
		// do nothing
	}

	protected abstract LineAggregator<T> getLineAggregator(Class<T> clazz);
}
