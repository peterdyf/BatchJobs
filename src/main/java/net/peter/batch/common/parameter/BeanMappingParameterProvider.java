package net.peter.batch.common.parameter;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cncbinternational.spring.util.BeanToMapUtil;

public class BeanMappingParameterProvider<T> extends MapSqlParameterSource {
	public BeanMappingParameterProvider(T object) {
		super(BeanToMapUtil.process(object));
	}
}
