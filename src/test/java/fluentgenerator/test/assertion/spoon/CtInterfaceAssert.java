package fluentgenerator.test.assertion.spoon;

import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class CtInterfaceAssert extends AbstractAssert<CtInterfaceAssert, CtInterface<?>> {

	public CtInterfaceAssert(CtInterface<?> actual) {
		super(actual, CtInterfaceAssert.class);
	}

	public CtInterfaceAssert hasMethod(String name, CtTypeReference<?>... parameters) {
		isNotNull();

		CtMethod<?> method = actual.getMethod(name, parameters);

		if(method == null) {
			failWithMessage("Expected method <%s> with parameters <%s>, but there was no such method",
				name, Stream.of(parameters).map(Object::toString).collect(Collectors.joining(", ")));
		}

		List<CtTypeReference<?>> parameterTypes = Optional.ofNullable(method.getParameters()).orElse(Collections.emptyList())
			.stream().map(CtParameter::getType).collect(Collectors.toList());

		boolean parametersSame = IntStream.range(0, parameters.length)
			.allMatch(index -> Objects.equals(parameters[index], parameterTypes.get(index)));

		if(!parametersSame) {
			failWithMessage("Expected method <%s> with parameters <%s>, but there was <%s> with parameters <%s>",
				name, Stream.of(parameters).map(Object::toString).collect(Collectors.joining(", ")),
				method.getSimpleName(), parameterTypes.stream().map(Object::toString).collect(Collectors.joining(", ")));
		}

		return this;
	}

	public CtInterfaceAssert hasNotMethod(String name, CtTypeReference<?>... parameterTypes) {
		isNotNull();

		CtMethod<?> method = actual.getMethod(name, parameterTypes);

		if(method == null) {
			return this;
		}

		List<CtTypeReference<?>> actualParameterTypes = Optional.ofNullable(method.getParameters()).orElse(Collections.emptyList())
			.stream().map(CtParameter::getType).collect(Collectors.toList());

		if(parameterTypes.length != actualParameterTypes.size()) {
			return this;
		}

		boolean parametersDiffer = IntStream.range(0, parameterTypes.length)
			.noneMatch(index -> Objects.equals(parameterTypes[index], actualParameterTypes.get(index)));

		if(!parametersDiffer) {
			failWithMessage("Expected no method <%s> with parameteres <%s>, but was present",
				name, Stream.of(parameterTypes).map(Object::toString).collect(Collectors.joining(", ")));
		}

		return this;
	}
}
