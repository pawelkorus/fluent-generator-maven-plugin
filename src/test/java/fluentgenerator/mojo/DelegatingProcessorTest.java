package fluentgenerator.mojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DelegatingProcessorTest {

	@Mock
	private CtElement sampleCtElement;

	@Mock
	private Processor<CtElement> mockProcessor;

	private DelegatingProcessor<CtElement> delegatingProcessor;

	@Test
	public void shouldDelegateCallToProcessMethod() {
		delegatingProcessor = new DelegatingProcessor<>(mockProcessor);

		delegatingProcessor.process(sampleCtElement);

		verify(mockProcessor, times(1)).process(eq(sampleCtElement));
	}

	@Test(expected = Test.None.class)
	public void shouldNotThrowExceptionWhenTargetProcessorNotSet() {
		delegatingProcessor = new DelegatingProcessor<>();

		delegatingProcessor.process(sampleCtElement);
	}

}
