package uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniol.apt.io.parser.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniol.apt.util.Pair;
import uniolunisaar.adam.logic.synthesis.solver.bounded.qbfapproach.QbfSolver;
import uniolunisaar.adam.logic.synthesis.solver.bounded.qbfapproach.QbfSolverFactory;
import uniolunisaar.adam.ds.synthesis.solver.bounded.qbfapproach.QbfSolverOptions;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
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

/**
 *
 * @author Manuel Gieseking
 */
public class BenchmarkSynt2017 extends AbstractSimpleModule {

    private static final String name = "benchSynt";
    private static final String descr = "Just for benchmark purposes. Does not check any preconditions of the Petri game."
            + " Tests existence of strategy, calculates graph and Petri game strategy, saves Petri game strategy as dot without rendering it to a pdf file.";
    private static final String PARAMETER_INTERN_OUTPUT = "io";
    private static final String PARAMETER_SOLVER = "sol";
    private static final String PARAMETER_OPTIMAL = "opt";
    private static final String PARAMETER_SIZES = "sizes";
//    private static final String PARAMETER_SHORT_OUTPUT = "so";

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

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("solver (bdd/qbf)");
        OptionBuilder.withDescription("The solver which should be used.");
        OptionBuilder.withLongOpt("solver");
        OptionBuilder.isRequired();
        options.put(PARAMETER_SOLVER, OptionBuilder.create(PARAMETER_SOLVER));

        OptionBuilder.withDescription("Flag indicating that the optimal parameters for the bounded synthesis should be used and not searched.");
        OptionBuilder.withLongOpt("optimal");
        options.put(PARAMETER_OPTIMAL, OptionBuilder.create(PARAMETER_OPTIMAL));

        OptionBuilder.withDescription("Flag indicating that only the input sizes of the problem should be printed.");
        OptionBuilder.withLongOpt("inputSizes");
        options.put(PARAMETER_SIZES, OptionBuilder.create(PARAMETER_SIZES));

//        OptionBuilder.withDescription("Using the short output version.");
//        OptionBuilder.withLongOpt("short");
//        options.put(PARAMETER_SHORT_OUTPUT, OptionBuilder.create(PARAMETER_SHORT_OUTPUT));
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

        String file = IOParameters.getInput(line);

        if (line.hasOption(PARAMETER_SIZES)) {
            DistrSysBDDSolver<? extends Condition<?>> sol;
            BDDSolverOptions opts = new BDDSolverOptions(true, true);
            sol = DistrSysBDDSolverFactory.getInstance().getSolver(file, opts);
            sol.initialize();

            StringBuilder sb = new StringBuilder();
            sb.append("#Tok, #Var, #P, #T, #P_s, #T_s\n");
            sb.append("sizes:").append(sol.getSolvingObject().getMaxTokenCount()).append("  &  ").append(sol.getVariableNumber());
            sb.append("  &  ").append(sol.getGame().getPlaces().size()).append("  &  ").append(sol.getGame().getTransitions().size());
            sb.append("\nsizes_strat:").append("-  &  -");
            Logger.getInstance().addMessage(sb.toString(), false);
            return;
        }

        if (line.hasOption(PARAMETER_SOLVER)) {
            String solver = line.getOptionValue(PARAMETER_SOLVER);
            if (solver.equals("bdd")) {
                StringBuilder sb = new StringBuilder();
                DistrSysBDDSolver<? extends Condition<?>> sol = null;
                try {
                    BDDSolverOptions opts = new BDDSolverOptions(true, true);
                    sol = DistrSysBDDSolverFactory.getInstance().getSolver(file, opts);

                    PetriGameWithTransits pn = sol.getStrategy();
                    PNWTTools.savePnwt2Dot(IOParameters.getOutput(line), pn, true);

                    sb.append("#Tok, #Var, #P, #T, #P_s, #T_s\n");
                    sb.append("sizes:").append(sol.getSolvingObject().getMaxTokenCount()).append("  &  ").append(sol.getVariableNumber());
                    sb.append("  &  ").append(sol.getGame().getPlaces().size()).append("  &  ").append(sol.getGame().getTransitions().size());
                    sb.append("\nsizes_strat:").append(pn.getPlaces().size()).append("  &  ").append(pn.getTransitions().size());
                    Logger.getInstance().addMessage(sb.toString(), false);
                } catch (NoStrategyExistentException nsee) {
                    sb.append("#Tok, #Var, #P, #T, #P_s, #T_s\n");
                    sb.append("sizes:").append(sol.getSolvingObject().getMaxTokenCount()).append("  &  ").append(sol.getVariableNumber());
                    sb.append("  &  ").append(sol.getGame().getPlaces().size()).append("  &  ").append(sol.getGame().getTransitions().size());
                    sb.append("\nsizes_strat:").append("-  &  -");
                    Logger.getInstance().addMessage(sb.toString(), false);
                    throw nsee;
                }
            } else if (line.hasOption(PARAMETER_OPTIMAL)) {
                StringBuilder sb = new StringBuilder();
                try {
                    Pair<Integer, Integer> paras = BenchmarkSynt2017ParameterQBF.getInstance().getParameters(file.substring(file.lastIndexOf("/") + 1, file.length() - 4));
                    QbfSolver<? extends Condition<?>> sol = QbfSolverFactory.getInstance().getSolver(file, new QbfSolverOptions(paras.getFirst(), paras.getSecond(), true));

                    sb.append("#Tok, #P, #T, #P_s, #T_s\n");
                    sb.append("sizes:").append(sol.getGame().getValue(CalculatorIDs.MAX_TOKEN_COUNT.name()).toString());
                    sb.append("& ").append(sol.getGame().getPlaces().size()).append("& ").append(sol.getGame().getTransitions().size());

                    PetriGameWithTransits pn = sol.getStrategy();
                    PNWTTools.savePnwt2Dot(IOParameters.getOutput(line), pn, true);

                    sb.append("\nsizes_strat:").append(pn.getPlaces().size()).append("  &  ").append(pn.getTransitions().size());
                    Logger.getInstance().addMessage(sb.toString(), false);
                } catch (NoStrategyExistentException nsee) {
                    sb.append("\nsizes_strat:").append("-  &  -");
                    Logger.getInstance().addMessage(sb.toString(), false);
                    throw nsee;
                }
            } else {
                int start1 = 5;
                int end1 = 50;
                int start2 = 1;
                int end2 = 10;
                for (int i = start2; i < end2; i++) {
                    if (start1 < end1) {
                        for (int j = start1; j < start1 + 6; j += 2) {
                            try {
                                QbfSolver<? extends Condition<?>> sol = QbfSolverFactory.getInstance().getSolver(file, new QbfSolverOptions(j, i, true));
                                PetriGameWithTransits pn = sol.getStrategy();
                                PNWTTools.savePnwt2Dot(IOParameters.getOutput(line), pn, true);

                                StringBuilder sb = new StringBuilder();
                                sb.append("#Tok, #P, #T, #P_s, #T_s, n, b\n");
                                sb.append("sizes:").append(sol.getGame().getValue(CalculatorIDs.MAX_TOKEN_COUNT.name()).toString());
                                sb.append("& ").append(sol.getGame().getPlaces().size()).append("& ").append(sol.getGame().getTransitions().size());
                                sb.append("\nsizes_strat:").append(pn.getPlaces().size()).append("& ").append(pn.getTransitions().size());
                                sb.append(j).append("&  ").append(i);
                                Logger.getInstance().addMessage(sb.toString(), false);
                                return; // found a strategy
                            } catch (NoStrategyExistentException nsee) { // try next parameter
                            }
                        }
                        start1 += 6;
                    }
                }
            }
        }
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
