package net.peter.batch.common.writer;

import net.peter.batch.common.extractor.BeanMappingFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;

import com.google.common.base.Preconditions;

public class FixLengthFileWriter<T> extends AbstractFileWriter<T> {

	private final String format;

	public FixLengthFileWriter(String fileName, Class<T> writerClass, String format) {
		super(fileName, writerClass);
		Preconditions.checkNotNull(format);
		this.format = format;
	}

	@Override
	protected FormatterLineAggregator<T> getLineAggregator(Class<T> clazz) {
		return new FormatterLineAggregator<T>() {
			{
				setFieldExtractor(new BeanMappingFieldExtractor<>());
				setFormat(format);
			}
		};
	}

}
