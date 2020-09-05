package uniolunisaar.adam.logic.ui.cl.modules.exporter.synthesis;

import uniolunisaar.adam.logic.ui.cl.modules.exporter.AbstractExporter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolverDontFitPetriGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CalculationInterruptedException;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.ds.graph.symbolic.bddapproach.BDDGraph;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.logic.distrsynt.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolver;
import uniolunisaar.adam.logic.distrsynt.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.util.symbolic.bddapproach.BDDTools;
import uniolunisaar.adam.logic.ui.cl.modules.Modules;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.ModulesSynthesizer;
import uniolunisaar.adam.tools.Logger;

/**
 *
 * @author Manuel Gieseking
 */
public class ExporterSynth extends AbstractExporter {

    private static final String PARAMETER_INPUT = "i";
    private static final String PARAMETER_FGS = "fgs";

    @Override
    protected Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("file");
        OptionBuilder.withDescription("The path to the input file in APT format, which should be exported to tikz when using option '-" + PARAMETER_FGS + "'.");
        OptionBuilder.withLongOpt("input");
        options.put(PARAMETER_INPUT, OptionBuilder.create(PARAMETER_INPUT));

        OptionBuilder.withDescription("Exports a finite graph strategy for the Petri game given by the input flag in APT format to tikz-code.");
        OptionBuilder.withLongOpt("exp_fg_strat");
        options.put(PARAMETER_FGS, OptionBuilder.create(PARAMETER_FGS));

        return options;
    }

    @Override
    protected Modules getModules() {
        return new ModulesSynthesizer();
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, NoSuitableDistributionFoundException, NoStrategyExistentException, CommandLineParseException, uniol.apt.io.parser.ParseException, NotSupportedGameException, CouldNotFindSuitableConditionException, SolverDontFitPetriGameException, ParameterMissingException, SolvingException, CalculationInterruptedException, Exception {
        super.execute(line);
        if (line.hasOption(PARAMETER_FGS)) {
            if (!line.hasOption(PARAMETER_INPUT)) {
                throw new CommandLineParseException("The option -" + PARAMETER_INPUT + " has to be set, if you use option -" + PARAMETER_FGS + "!");
            }
            String input = line.getOptionValue(PARAMETER_INPUT);
            BDDSolverOptions opts = new BDDSolverOptions(false, true);
            DistrSysBDDSolver<? extends Condition<?>> sol = DistrSysBDDSolverFactory.getInstance().getSolver(input, opts);
            BDDGraph g = sol.getGraphStrategy();
            String content = BDDTools.graph2Tikz(g, sol);
            try (PrintStream out = new PrintStream(getOutput(line) + ".tex")) {
                out.println(content);
            }
            Logger.getInstance().addMessage("Saved to: " + getOutput(line) + ".tex", false);
        }
    }

}
