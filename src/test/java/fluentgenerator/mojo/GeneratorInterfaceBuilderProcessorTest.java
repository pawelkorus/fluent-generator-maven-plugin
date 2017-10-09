package fluentgenerator.mojo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.nio.file.Paths;
import java.util.Date;
import java.util.function.Consumer;
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
		launcher.addInputResource(Paths.get("src/test/resources").toString());
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
		performTestOnClassWithAssertions("TestClass1", ctInterface -> {
			assertThat(ctInterface)
				.hasMethod("property1", createCtTypeReference(Supplier.class, String.class))
				.hasMethod("property2", createCtTypeReference(Supplier.class, Integer.class))
				.hasMethod("build");
		});
	}

	@Test
	public void shouldCreateGeneratorInterfaceFromLombokDataPOJO() {
		performTestOnClassWithAssertions("TestClass2", ctInterface -> {
			assertThat(ctInterface)
				.hasMethod("property1", createCtTypeReference(Supplier.class, String.class))
				.hasMethod("property2", createCtTypeReference(Supplier.class, Integer.class))
				.hasMethod("build");
		});
	}

	@Test
	public void shouldCreateGeneratorInterfaceFromLombokSetters() {
		performTestOnClassWithAssertions("TestClass3", ctInterface -> {
			assertThat(ctInterface)
				.hasMethod("property1", createCtTypeReference(Supplier.class, String.class))
				.hasMethod("property2", createCtTypeReference(Supplier.class, Integer.class))
				.hasMethod("property4", createCtTypeReference(Supplier.class, Date.class))
				.hasMethod("build")
				.hasNotMethod("property3", createCtTypeReference(Supplier.class, String.class));
		});
	}

	private CtTypeReference<?> createCtTypeReference(Class<?> type, Class<?>... generics) {
		CtTypeReference supplierType = launcher.getFactory().createCtTypeReference(type);
		Stream.of(generics).forEach(g -> supplierType.addActualTypeArgument(launcher.getFactory().createCtTypeReference(g)));
		return supplierType;
	}

	private CtClass<?> findClass(String name) {
		CtType<?> type = launcher.getModel().getAllTypes().stream()
			.filter(ctType -> ctType.getSimpleName().compareTo(name) == 0).findFirst().orElse(null);

		if(type == null) {
			Assert.fail("Can't find type " + name);
		}

		if(!(type instanceof CtClass<?>)) {
			Assert.fail("Requested type is not a CtClass<?> type");
		}

		return (CtClass<?>) type;

	}

	private void performTestOnClassWithAssertions(String name, Consumer<CtInterface<?>> assertions) {
		CtClass<?> testClass = findClass(name);
		generatorInterfaceBuilderProcessor.onInterfaceGenerated(assertions);
		generatorInterfaceBuilderProcessor.process(testClass);
	}
}
