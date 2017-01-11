package net.peter.batch.common.reader;

import java.lang.reflect.Field;
import java.util.List;

import net.peter.batch.annotation.Length;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import com.cncbinternational.spring.util.OrderUtil;

/**
 * 
 * Read File to Bean<br>
 * Order determined by <code>@Order</code> value on the Bean Field<br>
 * Length determined by <code>@Length</code> value on the Bean Field <br>
 * 
 * 
 * @see com.cncbinternational.spring.annotation.Order
 * @see Length
 */
public class DelimitedFileReader<T> extends AbstractFileReader<T> {

	private final String delimiter;

	public DelimitedFileReader(String fileName, String delimiter, Class<T> writerClass) {
		super(fileName, writerClass);
		this.delimiter = delimiter;
	}

	@Override
	protected LineMapper<T> getLineMapper(Class<T> clazz) {
		return new DefaultLineMapper<T>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setDelimiter(delimiter);
						List<Field> fields = OrderUtil.getFieldsSortedByOrder(clazz);
						setNames(fields.stream().map(Field::getName).toArray(String[]::new));
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<T>() {
					{
						setTargetType(clazz);
					}
				});
			}
		};
	}

}
