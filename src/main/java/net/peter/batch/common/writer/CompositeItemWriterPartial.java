package net.peter.batch.common.writer;

import java.util.Arrays;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;

import com.google.common.base.Preconditions;

public interface CompositeItemWriterPartial {

	@SafeVarargs
	static <T> CompositeItemWriter<? super T> multi(ItemWriter<? super T>... writers) {
		Preconditions.checkNotNull(writers);
		return new CompositeItemWriter<T>() {
			{
				setDelegates(Arrays.asList(writers));
			}
		};
	}
}
