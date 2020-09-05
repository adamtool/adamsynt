package uniolunisaar.adam.data.ui.cl.parameters.synthesis.bounded.qbfconcurrent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import uniolunisaar.adam.ds.synthesis.solver.bounded.qbfconcurrent.QbfConSolverOptions;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.SpecificSolverParameters;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.bounded.qbfconcurrent.QBFConSolverHandle;

/**
 *
 * @author Manuel
 */
public class QBFConParameters extends SpecificSolverParameters {

	private static final String PARAMETER_N = "n";
	private static final String PARAMETER_B = "b";

	// TODO decide on size of the description
	@Override
	public Map<String, Option> createOptions() {
		Map<String, Option> options = new HashMap<>();
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("n");
		OptionBuilder.withDescription("length of the simulation");
//        OptionBuilder.isRequired();
		OptionBuilder.withLongOpt("bound1");
		OptionBuilder.withType(Number.class);
		options.put(PARAMETER_N, OptionBuilder.create(PARAMETER_N));

		OptionBuilder.hasArg();
		OptionBuilder.withArgName("b");
		OptionBuilder.withDescription("size of the bounded unfolding per place");
//        OptionBuilder.isRequired();
		OptionBuilder.withLongOpt("bound2");
		OptionBuilder.withType(Number.class);
		options.put(PARAMETER_B, OptionBuilder.create(PARAMETER_B));

		return options;
	}

	@Override
	protected QBFConSolverHandle createSolverHandle(String input, boolean skip, String name, String parameterLine)
			throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException,
			NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException,
			ParameterMissingException, SolvingException {
		return new QBFConSolverHandle(input, skip, name, this, parameterLine);
	}

	public void setParameters(QbfConSolverOptions options, CommandLine line) throws ParseException {
		int n = line.hasOption(PARAMETER_N) ? ((Number) line.getParsedOptionValue(PARAMETER_N)).intValue() : -1;
		int b = line.hasOption(PARAMETER_B) ? ((Number) line.getParsedOptionValue(PARAMETER_B)).intValue() : -1;
		options.setN(n);
		options.setB(b);
	}

}
