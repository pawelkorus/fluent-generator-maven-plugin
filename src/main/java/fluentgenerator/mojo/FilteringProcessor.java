package fluentgenerator.mojo;

import spoon.processing.Processor;
import spoon.reflect.declaration.CtClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FilteringProcessor extends DelegatingProcessor<CtClass<?>> {

	private List<Pattern> includePatterns = Collections.emptyList();
	private List<Pattern> excludePatterns = Collections.emptyList();

	FilteringProcessor(Processor<CtClass<?>> targetProcessor) {
		super(targetProcessor);
	}

	@Override
	public void process(CtClass<?> element) {
		String qualifiedName = element.getQualifiedName();

		if(includePatterns.stream().anyMatch(pattern -> pattern.matcher(qualifiedName).matches())) {
			if(excludePatterns.stream().noneMatch(pattern -> pattern.matcher(qualifiedName).matches())) {
				super.process(element);
			}
		}
	}

	public void setIncludePatterns(List<String> patterns) {
		includePatterns = new ArrayList<>();
		patterns.stream().map(Pattern::compile).forEach(includePatterns::add);
	}

	public void setExcludePatterns(List<String> patterns) {
		excludePatterns = new ArrayList<>();
		patterns.stream().map(Pattern::compile).forEach(excludePatterns::add);
	}

}
