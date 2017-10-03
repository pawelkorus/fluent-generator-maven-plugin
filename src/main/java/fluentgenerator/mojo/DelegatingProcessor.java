package fluentgenerator.mojo;

import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;

public class DelegatingProcessor<E extends CtElement> extends AbstractProcessor<E> {

	private final Processor<E> targetProcessor;

	public DelegatingProcessor() {
		this.targetProcessor = null;
	}

	public DelegatingProcessor(Processor<E> targetProcessor) {
		this.targetProcessor = targetProcessor;
	}

	@Override
	public void process(E element) {
		if(this.targetProcessor != null) {
			this.targetProcessor.process(element);
		}
	}

}
