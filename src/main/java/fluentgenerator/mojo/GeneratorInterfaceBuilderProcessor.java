package fluentgenerator.mojo;

import fluentgenerator.core.Generator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GeneratorInterfaceBuilderProcessor extends AbstractProcessor<CtClass<?>> {
	private static Set<ModifierKind> PUBLIC = Collections.singleton(ModifierKind.PUBLIC);

	private Consumer<CtInterface> interfaceConsumer = (consumer) -> {};

	GeneratorInterfaceBuilderProcessor(Launcher launcher) {
		setFactory(launcher.getFactory());
	}

	public void onInterfaceGenerated(Consumer<CtInterface> consumer) {
		this.interfaceConsumer = consumer;
	}

	@Override
	public void process(CtClass<?> ctClass) {
		String generatorPackage = ctClass.getPackage().getQualifiedName() + ".generator";
		String generatorName = ctClass.getSimpleName() + "Generator";

		Set<CtMethod<?>> setters = extractorSetters(ctClass);
		Set<CtField<?>> fields = extractFields(ctClass);

		CtInterface generatorInterface = getFactory().Interface().create(generatorPackage + '.' + generatorName);
		generatorInterface.setModifiers(PUBLIC);

		CtTypeReference fluentGeneratorInterface = getFactory().createCtTypeReference(Generator.class);
		fluentGeneratorInterface.addActualTypeArgument(ctClass.getReference());
		generatorInterface.addSuperInterface(fluentGeneratorInterface);

		getFactory().Method().create(generatorInterface, PUBLIC, ctClass.getReference(), "build", null, null);

		setters.forEach(method -> {
			String methodName = method.getSimpleName().substring(3);
			methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

			CtTypeReference parameterType = method.getParameters().get(0).getType();

			CtTypeReference supplierType = getFactory().Code().createCtTypeReference(Supplier.class);
			supplierType.addActualTypeArgument(parameterType);

			CtMethod interfaceMethod = getFactory().Method().create(generatorInterface, PUBLIC, generatorInterface.getReference(), methodName, null, null);
			getFactory().Method().createParameter(interfaceMethod, supplierType, methodName);
		});

		fields.forEach(field -> {
			String methodName = field.getSimpleName();

			CtTypeReference<?> parameterType = field.getType();

			CtTypeReference supplierType = getFactory().Code().createCtTypeReference(Supplier.class);
			supplierType.addActualTypeArgument(parameterType);

			CtMethod interfaceMethod = getFactory().Method().create(generatorInterface, PUBLIC, generatorInterface.getReference(), methodName, null, null);
			getFactory().Method().createParameter(interfaceMethod, supplierType, methodName);
		});

		interfaceConsumer.accept(generatorInterface);
	}

	private Set<CtMethod<?>> extractorSetters(CtClass<?> ctClass) {
		return ctClass.getAllMethods().stream()
			.filter(method -> method.getSimpleName().startsWith("set"))
			.filter(method -> method.getParameters().size() == 1)
			.collect(Collectors.toSet());
	}

	private Set<CtField<?>> extractFields(CtClass<?> ctClass) {
		CtTypeReference<Data> lombokDataRef = getFactory().createCtTypeReference(Data.class);
		CtTypeReference<Setter> lombokSetterRef = getFactory().createCtTypeReference(Setter.class);
		boolean lombokDataPresent = ctClass.getAnnotation(lombokDataRef) != null;

		return ctClass.getFields().stream()
			.filter(ctField -> {
				return lombokDataPresent || ctField.getAnnotation(lombokSetterRef) != null;
			})
			.filter(ctField -> {
				CtAnnotation<Setter> setterCtAnnotation = ctField.getAnnotation(lombokSetterRef);
				if(setterCtAnnotation == null) {
					return true;
				} else {
					return setterCtAnnotation.getActualAnnotation().value() == null || setterCtAnnotation.getActualAnnotation().value() == AccessLevel.PUBLIC;
				}
			})
			.collect(Collectors.toSet());
	}
}
