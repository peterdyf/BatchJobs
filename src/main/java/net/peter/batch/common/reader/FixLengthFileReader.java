package net.peter.batch.common.reader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

import net.peter.batch.annotation.Length;
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
public class FixLengthFileReader<T> extends AbstractFileReader<T> {

	public FixLengthFileReader(String fileName, Class<T> writerClass) {
		super(fileName, writerClass);
	}

	@Override
	protected LineMapper<T> getLineMapper(Class<T> clazz) {
		
		return new DefaultLineMapper<T>() {
			{
				setLineTokenizer(new FixedLengthTokenizer() {
					{
						List<Field> fields = OrderUtil.getFieldsSortedByOrder(clazz);
						AtomicInteger loc = new AtomicInteger(1);
						setColumns(fields.stream().map(f -> f.getAnnotation(Length.class).value()).map(length -> new Range(loc.getAndAdd(length - 1), loc.getAndAdd(1))).toArray(Range[]::new));
						setNames(fields.stream().map(Field::getName).toArray(String[]::new));
						setStrict(false);
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
