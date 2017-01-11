package net.peter.batch.common.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

@FunctionalInterface
public interface ForEachItemWriter<T> extends ItemWriter<T> {

	default void write(List<? extends T> items) {
		items.forEach(this::write);
	}

	void write(T item);
}
