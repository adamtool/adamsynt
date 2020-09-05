package uniolunisaar.adam.logic.ui.cl.modules.synthesis.benchmarks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.logic.synthesis.pgwt.calculators.CalculatorIDs;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.logic.ui.cl.modules.AbstractSimpleModule;
import uniolunisaar.adam.ds.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolver;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.util.benchmarks.synthesis.Benchmarks;

/**
 *
 * @author Manuel Gieseking
 */
public class Benchmark extends AbstractSimpleModule {

    private static final String name = "bench";
    private static final String descr = "Just for benchmark purposes. Does not check any preconditions of the Petri game."
            + " Tests existence of strategy, calculates graph and Petri game strategy, saves Petri game strategy as dot without rendering it to a pdf file.";
    private static final String PARAMETER_INTERN_OUTPUT = "io";
    private static final String PARAMETER_SHORT_OUTPUT = "so";

    @Override
    protected Map<String, Option> createOptions() {
        Map<String, Option> options = new HashMap<>();

        // Add IO
        options.putAll(IOParameters.createOptions());

        // Add Benchmark specific ones
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("file");
        OptionBuilder.withDescription("The path to the output file for the internal benchmark data.");
        OptionBuilder.withLongOpt("out_bench");
        options.put(PARAMETER_INTERN_OUTPUT, OptionBuilder.create(PARAMETER_INTERN_OUTPUT));

        OptionBuilder.withDescription("Using the short output version.");
        OptionBuilder.withLongOpt("short");
        options.put(PARAMETER_SHORT_OUTPUT, OptionBuilder.create(PARAMETER_SHORT_OUTPUT));

        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, NetNotSafeException, NoSuitableDistributionFoundException, NoStrategyExistentException, ParseException, CommandLineParseException, Exception {
        super.execute(line);
        if (line.hasOption(PARAMETER_INTERN_OUTPUT)) {
            String output_bench = line.getOptionValue(PARAMETER_INTERN_OUTPUT);
            Logger.getInstance().setPath(output_bench);
            Logger.getInstance().setOutput(Logger.OUTPUT.FILE);
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().start(Benchmarks.Parts.OVERALL);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        BDDSolverOptions opts = new BDDSolverOptions(true, true);
        DistrSysBDDSolver<? extends Condition<?>> sol = DistrSysBDDSolverFactory.getInstance().getSolver(IOParameters.getInput(line), opts);
        PetriNet pn = sol.getStrategy();
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.OVERALL);

        Benchmarks.getInstance().start(Benchmarks.Parts.DOT_SAVING);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        PNWTTools.savePnwt2Dot(IOParameters.getOutput(line), sol.getGame(), true);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.DOT_SAVING);
        Benchmarks.getInstance().addData(sol, pn);
        if (line.hasOption("short")) {
            StringBuilder sb = new StringBuilder();
            sb.append("#Tok, #Var, #P, #T, #P_s, #T_s\n");
            sb.append(sol.getGame().getValue(CalculatorIDs.MAX_TOKEN_COUNT.name()).toString()).append(", ").append(sol.getVariableNumber());
            sb.append(", ").append(sol.getGame().getPlaces().size()).append(", ").append(sol.getGame().getTransitions().size());
            sb.append(", ").append(pn.getPlaces().size()).append(", ").append(pn.getTransitions().size());
            Logger.getInstance().addMessage(sb.toString(), false);
        } else {
            Logger.getInstance().addMessage(Benchmarks.getInstance().toCSVString(), false);
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
    }

    @Override
    public String getDescr() {
        return descr;
    }

    @Override
    public String getName() {
        return name;
    }
}
