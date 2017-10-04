package fluentgenerator.mojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtClass;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FilteringProcessorTest {

	@Mock
	private Processor<CtClass<?>> mockProcessor;

	private FilteringProcessor<CtClass<?>> filteringProcessor;

	@Test
	public void shouldMatchMatchAnyIfNoIncludeExcludePatternsSet() {
		CtClass mockCtClass = mock(CtClass.class);
		filteringProcessor = new FilteringProcessor<>(mockProcessor);
		when(mockCtClass.getQualifiedName()).thenReturn("some.class.qualified.name");

		filteringProcessor.process(mockCtClass);

		verify(mockCtClass, times(1)).getQualifiedName();
		verify(mockProcessor, times(1)).process(eq(mockCtClass));
	}

	@Test
	public void shouldProcessClassesThatMatchIncludePatterns() {
		List<CtClass> listMockCtClass = setupListMockCtClass();

		filteringProcessor = new FilteringProcessor<>(mockProcessor);
		filteringProcessor.setIncludePatterns(Arrays.asList("pattern1.*", "pattern2.*"));

		listMockCtClass.forEach(filteringProcessor::process);

		verify(mockProcessor, times(1)).process(listMockCtClass.get(0));
		verify(mockProcessor, times(1)).process(listMockCtClass.get(1));
		verify(mockProcessor, never()).process(listMockCtClass.get(2));
	}

	@Test
	public void shouldNotProcessClassesThatAreExcludedByPattern() {
		List<CtClass> listMockCtClass = setupListMockCtClass();

		filteringProcessor = new FilteringProcessor<>(mockProcessor);
		filteringProcessor.setExcludePatterns(Arrays.asList("pattern1.*", "pattern2.*"));

		listMockCtClass.forEach(filteringProcessor::process);

		verify(mockProcessor, never()).process(listMockCtClass.get(0));
		verify(mockProcessor, never()).process(listMockCtClass.get(1));
		verify(mockProcessor, times(1)).process(listMockCtClass.get(2));
	}

	private List<CtClass> setupListMockCtClass() {
		List<CtClass> listMockCtClass = Arrays.asList(mock(CtClass.class), mock(CtClass.class), mock(CtClass.class));
		when(listMockCtClass.get(0).getQualifiedName()).thenReturn("pattern1.sample.class");
		when(listMockCtClass.get(1).getQualifiedName()).thenReturn("pattern2.sample.class");
		when(listMockCtClass.get(2).getQualifiedName()).thenReturn("pattern3.sample.class");
		return listMockCtClass;
	}
}
