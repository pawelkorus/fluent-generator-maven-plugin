package fluentgenerator.mojo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static fluentgenerator.test.assertion.spoon.Assertions.assertThat;

@RunWith(JUnit4.class)
public class GeneratorInterfaceBuilderProcessorTest {

	private static Launcher launcher;

	private GeneratorInterfaceBuilderProcessor generatorInterfaceBuilderProcessor;

	@BeforeClass
	public static void beforeClass() {
		launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();
	}

	@Before
	public void before() {
		generatorInterfaceBuilderProcessor = new GeneratorInterfaceBuilderProcessor(launcher);
	}

	@Test
	public void shouldCreateGeneratorInterfaceFromPOJOSetters() {
		CtClass<TestClass1> testClass1CtClass = launcher.getFactory().Class().get(TestClass1.class);
		generatorInterfaceBuilderProcessor.onInterfaceGenerated(ctInterface -> {
			assertThat(ctInterface)
				.hasMethod("property1", createCtTypeReference(Supplier.class, String.class))
				.hasMethod("property2", createCtTypeReference(Supplier.class, Integer.class))
				.hasMethod("build");
		});

		generatorInterfaceBuilderProcessor.process(testClass1CtClass);
	}

	@Test
	public void shouldCreateGeneratorInterfaceFromLombokDataPOJO() {
		CtClass<TestClass2> testClass2CtClass = launcher.getFactory().Class().get(TestClass2.class);
		generatorInterfaceBuilderProcessor.onInterfaceGenerated(ctInterface -> {
			assertThat(ctInterface)
				.hasMethod("property1", createCtTypeReference(Supplier.class, String.class))
				.hasMethod("property2", createCtTypeReference(Supplier.class, Integer.class))
				.hasMethod("build");
		});

		generatorInterfaceBuilderProcessor.process(testClass2CtClass);
	}

	@Test
	public void shouldCreateGeneratorInterfaceFromLombokSetters() {
		CtClass<TestClass3> testClass = launcher.getFactory().Class().get(TestClass3.class);
		generatorInterfaceBuilderProcessor.onInterfaceGenerated(ctInterface -> {
			assertThat(ctInterface)
				.hasMethod("property1", createCtTypeReference(Supplier.class, String.class))
				.hasMethod("property2", createCtTypeReference(Supplier.class, Integer.class))
				.hasMethod("property4", createCtTypeReference(Supplier.class, Date.class))
				.hasMethod("build")
				.hasNotMethod("property3", createCtTypeReference(Supplier.class, String.class));
		});

		generatorInterfaceBuilderProcessor.process(testClass);
	}

	private static class TestClass1 {
		public void setProperty1(String v) {}
		public void setProperty2(int v) {}
	}

	@Data
	private static class TestClass2 {
		String property1;
		int property2;
	}

	private static class TestClass3 {
		@Setter String property1;
		@Setter int property2;
		@Setter(AccessLevel.PRIVATE) String property3;
		@Setter(AccessLevel.PUBLIC) Date property4;
	}

	private CtTypeReference<?> createCtTypeReference(Class<?> type, Class<?>... generics) {
		CtTypeReference supplierType = launcher.getFactory().createCtTypeReference(type);
		Stream.of(generics).forEach(g -> supplierType.addActualTypeArgument(launcher.getFactory().createCtTypeReference(g)));
		return supplierType;
	}
}
