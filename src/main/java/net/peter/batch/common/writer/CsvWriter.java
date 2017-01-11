package net.peter.batch.common.writer;

import net.peter.batch.common.extractor.BeanMappingFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;

public class CsvWriter<T> extends AbstractFileWriter<T> {

	public CsvWriter(String fileName, Class<T> writerClass) {
		super(fileName, writerClass);
	}

	@Override
	protected DelimitedLineAggregator<T> getLineAggregator(Class<T> clazz) {
		return new DelimitedLineAggregator<T>() {
			{
				setFieldExtractor(new BeanMappingFieldExtractor<>());
			}
		};
	}

}
