package fluentgenerator.test.assertion.spoon;

import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Objects;

public class CtMethodAssert extends AbstractAssert<CtMethodAssert, CtMethod<?>> {

	public CtMethodAssert(CtMethod<?> actual) {
		super(actual, CtMethodAssert.class);
	}

	public CtMethodAssert hasName(String name)  {
		isNotNull();

		if(!Objects.equals(actual.getSimpleName(), name)) {
			failWithMessage("Expected CtMethod name to be <%s> but was <%s>", name, actual.getSimpleName());
		}

		return this;
	}

	public CtMethodAssert hasParameter(int position, CtTypeReference<?> type) {
		isNotNull();

		List<CtParameter<?>> parameters = actual.getParameters();

		if(parameters.size() <= position) {
			failWithMessage("Expected parameter on position <%d> to be of type <%s> but there are only <%d> parameters",
				position, type.toString(), parameters.size());
		}

		CtParameter<?> parameter = parameters.get(position);
		CtTypeReference<?> parameterType = parameter.getType();

		if(!Objects.equals(type, parameterType)) {
			failWithMessage("Expected parameter on position <%d> to be of type <%s> but was <%s>",
				position, type, parameter);
		}

		return this;
	}

	public CtMethodAssert hasParameter(int position, String name, CtTypeReference<?> type) {
		isNotNull();

		List<CtParameter<?>> parameters = actual.getParameters();

		if(parameters.size() <= position) {
			failWithMessage("Expected parameter on position <%d> to be of type <%s> but there are only <%d> parameters",
				position, type.toString(), parameters.size());
		}

		CtParameter<?> parameter = parameters.get(position);

		if(!Objects.equals(actual.getSimpleName(), name)) {
			failWithMessage("Expected parameter on position <%d> to have name <%s> but was <%s>",
				position, name, actual.getSimpleName());
		}

		return hasParameter(position, type);
	}

	public void hasNoParameters() {
		isNotNull();

		if(actual.getParameters().size() != 0) {
			failWithMessage("Expected that there is no parameters but was <%s>", actual.getParameters().size());
		}
	}
}
