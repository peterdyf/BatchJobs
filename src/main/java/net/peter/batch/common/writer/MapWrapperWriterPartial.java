package net.peter.batch.common.writer;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;

import com.google.common.base.Preconditions;

public interface MapWrapperWriterPartial {

	static <T, G> Builder<T, G> map(Function<T, G> mapper) {
		return new Builder<>(mapper);
	}

	/**
	 * 
	 * Wrap an existing ItemWriter by providing a Lambda map operation
	 * <p>
	 * 
	 * Intention:<br>
	 * A Spring batch trunk only support one processor, but Sometimes<br>
	 * CompositeItemWriter need different in-type for each ItemWriter.<br>
	 * There use a mapper to simulate pre-processor for different ItemWriters.
	 * <p>
	 * 
	 * @author Peter.DI
	 * @param <T>
	 *            Returned Type
	 * @param <G>
	 *            Wrapped Type
	 * @see CompositeItemWriterPartial
	 * @see org.springframework.batch.item.support.CompositeItemWriter
	 */
	class Builder<T, G> {

		final Function<T, G> mapper;

		Builder(Function<T, G> mapper) {
			Preconditions.checkNotNull(mapper);
			this.mapper = mapper;
		}

		public ItemWriter<T> wrapper(ItemWriter<? super G> writer) {
			Preconditions.checkNotNull(writer);
			return items -> writer.write(map(items));
		}

		private List<G> map(List<? extends T> items) {
			return items.stream().map(mapper).collect(Collectors.toList());
		}

		/**
		 * It is probably a Spring Batch design defect<br>
		 * to deal with ItemStreamWriter and ItemWriter in different ways.<br>
		 * Use wrapper() to deal with ItemStreamWriter would return ItemWriter and cause exception.<br>
		 * So it is need to specify using wrapperStream() to deal with for ItemStreamWriter
		 * <br>
		 * 
		 * @param writer
		 * @return
		 */
		@SuppressWarnings("unchecked") // dynamic proxy
		public ItemStreamWriter<T> wrapperStream(ItemStreamWriter<? super G> writer) {
			Preconditions.checkNotNull(writer);
			return (ItemStreamWriter<T>) Proxy.newProxyInstance(ItemStreamWriter.class.getClassLoader(), new Class[] { ItemStreamWriter.class }, (proxy, method, args) -> {
				if ("write".equals(method.getName())) {
					args[0] = map((List<? extends T>) args[0]);
				}
				return method.invoke(writer, args);
			});

		}

	}

}
