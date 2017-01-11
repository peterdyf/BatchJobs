package net.peter.batch.jobs.tokenMailingAddress;

import java.util.Optional;
import java.util.function.Function;

class Matcher<P, T> {
	final Optional<T> result;
	final P param;

	private Matcher(P param, Optional<T> result) {
		this.param = param;
		this.result = result;
	}

	public Matcher(P param) {
		this.param = param;
		result = Optional.empty();
	}

	public Matcher<P, T> match(Function<P, Optional<T>> fun) {
		if (result.isPresent()) {
			return this;
		} else {
			return new Matcher<>(param, fun.apply(param));
		}
	}

	public T get(Function<P, T> elseFun) {
		if (result.isPresent()) {
			return result.get();
		} else {
			return elseFun.apply(param);
		}
	}
}