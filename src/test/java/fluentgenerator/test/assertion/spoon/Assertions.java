package fluentgenerator.test.assertion.spoon;

import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;

public class Assertions {

	public static CtMethodAssert assertThat(CtMethod<?> method) {
		return new CtMethodAssert(method);
	}

	public static CtInterfaceAssert assertThat(CtInterface<?> ctInterface) {
		return new CtInterfaceAssert(ctInterface);
	}

}
