package net.peter.batch.common.extractor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.file.transform.FieldExtractor;

import com.cncbinternational.spring.util.OrderUtil;
import com.cncbinternational.spring.util.ReflectionUtil;

/**
 * 
 * Use <code>@Order</code> to Extractor Field
 * 
 * @author Peter.DI
 * @param <T>
 * @param <G>
 * @see com.cncbinternational.batch.common.extractor. BeanMappingFieldExtractor
 * 
 */
public class BeanMappingFieldExtractor<T> implements FieldExtractor<T> {

	/**
	 * @see org.springframework.batch.item.file.transform.FieldExtractor#extract(java.lang.Object)
	 */
	@Override
	public final Object[] extract(T item) {
		List<Object> values = getValuesByOrder(item);
		return values.toArray();
	}

	protected final List<Object> getValuesByOrder(Object item) {
		List<Field> fields = OrderUtil.getFieldsSortedByOrder(item.getClass());
		return fields.stream().map(f -> ReflectionUtil.getValue(f, item)).collect(Collectors.toList());
	}

}