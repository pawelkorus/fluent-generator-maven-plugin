package fluentgenerator.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.util.List;

/*import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;*/

@Mojo(
	name = "generate",
	defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
public class GeneratorMojo extends AbstractMojo {

	@Parameter(readonly = true, defaultValue = "${project}")
	private MavenProject project;

	@Parameter(required = true)
	private File outputDirectory;

	@Parameter
	private List<String> includes;

	@Parameter
	private List<String> excludes;

	public void execute() throws MojoExecutionException, MojoFailureException {
		File outputDirectory = getOutputDirectory();
		MavenProject project = getProject();

		if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
			throw new MojoFailureException("Output directory " + outputDirectory.getAbsolutePath() + " doesn't exists and can't be created");
		}

		project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

		List<String> sourceRoots = project.getCompileSourceRoots();

		Launcher launcher = new Launcher();
		sourceRoots.forEach(launcher::addInputResource);
		launcher.getEnvironment().setAutoImports(false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		PrettyPrinter prettyPrinter = launcher.createPrettyPrinter();

		JavaOutputProcessor javaOutputProcessor = new JavaOutputProcessor(outputDirectory, prettyPrinter);
		javaOutputProcessor.setFactory(launcher.getFactory());

		GeneratorInterfaceBuilderProcessor generatorInterfaceBuilderProcessor = new GeneratorInterfaceBuilderProcessor(launcher);
		generatorInterfaceBuilderProcessor.onInterfaceGenerated(cttype -> {
			getLog().info("Saving generator interface for " + cttype.getQualifiedName());
			javaOutputProcessor.createJavaFile(cttype);
		});

		FilteringProcessor processor = new FilteringProcessor(generatorInterfaceBuilderProcessor);
		processor.setIncludePatterns(getIncludes());
		processor.setExcludePatterns(getExcludes());

		CtModel model = launcher.getModel();
		model.processWith(processor);
	}

	private File getOutputDirectory() {
		return this.outputDirectory;
	}

	private MavenProject getProject() {
		return project;
	}

	private List<String> getIncludes() {
		return includes;
	}

	private List<String> getExcludes() {
		return excludes;
	}
}
