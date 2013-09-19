package com.nioos.leanspooler.bdd;



import java.util.ArrayList;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;



/**
 * Add behavior tests.
 * @author Hipolito Jimenez.
 */
@RunWith(JUnitReportingRunner.class)
public class AddBehaviorTest extends JUnitStories { // NOPMD
	
	
	/**
	 * Story paths.
	 */
	private final transient List<String> storyPathList =
		new ArrayList<String>();
	
	
	/**
	 * Steps for the tests.
	 */
	private final transient List<Object> stepsInstances =
		new ArrayList<Object>();
	
	
	/**
	 * Constructor.
	 */
	public AddBehaviorTest() {
		super();
		storyPathList.add("com/nioos/leanspooler/bdd/Add.story");
		stepsInstances.add(new AddBehaviorSteps());
		final Configuration configuration = getConf();
		final InjectableStepsFactory stepsFactory =
			new InstanceStepsFactory(configuration, stepsInstances);
		useStepsFactory(stepsFactory);
	}
	
	
	/**
	 * Gets the configuration for the tests.
	 * @return the configuration for the tests.
	 */
	private Configuration getConf() {
		final Configuration conf = configuration();
		final StoryReporterBuilder storyReporterBuilder =
			conf.storyReporterBuilder();
		storyReporterBuilder.withFormats(Format.CONSOLE, Format.HTML,
			Format.TXT);
		conf.useStoryReporterBuilder(storyReporterBuilder);
		conf.usePendingStepStrategy(new FailingUponPendingStep());
		return conf;
	}
	
	
	@Override
	protected final List<String> storyPaths() {
		return storyPathList;
	}
	
	
}
