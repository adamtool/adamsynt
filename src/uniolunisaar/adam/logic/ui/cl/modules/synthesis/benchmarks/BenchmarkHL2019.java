package uniolunisaar.adam.logic.ui.cl.modules.synthesis.benchmarks;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniol.apt.io.parser.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.logic.distrsynt.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.logic.ui.cl.modules.AbstractSimpleModule;
import uniolunisaar.adam.data.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.distrsynt.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolver;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class BenchmarkHL2019 extends AbstractSimpleModule {

    private static final String name = "benchHL2019";
    private static final String descr = "Just for benchmark purposes. Does not check any preconditions of the Petri game."
            + " Tests existence of strategy, calculates graph and Petri game strategy, saves Petri game strategy as dot without rendering it to a pdf file.";
    private static final String PARAMETER_INTERN_OUTPUT = "io";
    private static final String PARAMETER_LLGRAPHSIZE = "llgs";
    private static final String PARAMETER_HLGRAPHSIZE = "hlgs";

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
        OptionBuilder.withArgName("file");
        OptionBuilder.withDescription("Just calculates the size of the two-player game over a finite graph of the low-level approach and saves it to the given file.");
        OptionBuilder.withLongOpt("ll-graphSize");
        options.put(PARAMETER_LLGRAPHSIZE, OptionBuilder.create(PARAMETER_LLGRAPHSIZE));

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("file");
        OptionBuilder.withDescription("Just calculates the size of the two-player game over a finite graph of the high-level approach and saves it to the given file.");
        OptionBuilder.withLongOpt("hl-graphSize");
        options.put(PARAMETER_HLGRAPHSIZE, OptionBuilder.create(PARAMETER_HLGRAPHSIZE));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, NetNotSafeException, NoSuitableDistributionFoundException, NoStrategyExistentException, ParseException, CommandLineParseException, Exception {
        super.execute(line);

        String file = IOParameters.getInput(line);

        DistrSysBDDSolver<? extends Condition<?>> sol = null;

        if (line.hasOption(PARAMETER_LLGRAPHSIZE) || !line.hasOption(PARAMETER_HLGRAPHSIZE)) {
            BDDSolverOptions opt = new BDDSolverOptions(true);
            opt.setNoType2(true);
            sol = DistrSysBDDSolverFactory.getInstance().getSolver(file, opt);
            sol.initialize();
        }

        if (line.hasOption(PARAMETER_LLGRAPHSIZE)) {
            Logger.getInstance().setVerbose(false);
            Logger.getInstance().setVerboseMessageStream(null);

            String output = line.getOptionValue(PARAMETER_LLGRAPHSIZE);

            double sizeBDD = sol.getBufferedDCSs().satCount(sol.getFirstBDDVariables());
            System.out.println("Number of states of the two-player game over a finite graph by solving BDD: " + sizeBDD); // todo: fix the logger...

//            String name = "no concrete graph calculation";
//            int size = -1;
//            if (true) { // to expensive this approach
//                BDDGraph graph = sol.getGraphGame();
//                size = graph.getSize();
////            Logger.getInstance().addMessage(true, "Number of states of the two-player game over a finite graph: " + size);
//                System.out.println("Number of states of the two-player game over a finite graph by creating graph: " + size); // todo: fix the logger...
//                name = graph.getName();
//            }
//            // version to put everything in one file
//            try {
//                String content = file + ": " + sizeBDD + "\n";
//                Files.write(Paths.get(output), content.getBytes(), StandardOpenOption.APPEND);
//            } catch (IOException e) {
//            }
            String content = "" + sizeBDD;
            Tools.saveFile(output, content);
            return;
        }

        if (line.hasOption(PARAMETER_HLGRAPHSIZE)) {
            Logger.getInstance().setVerbose(false);
            Logger.getInstance().setVerboseMessageStream(null);

            String output = line.getOptionValue(PARAMETER_HLGRAPHSIZE);

//        SymbolicGameGraph<Place, Transition, ILLDecision, LLDecisionSet, SRGFlow<Transition>> graph = SGGBuilder.createByLLGame(hlgame);
//            System.out.println("Number of states of the two-player game over a finite graph by solving BDD: " + sizeBDD); // todo: fix the logger...
//            String name = "no concrete graph calculation";
//            int size = -1;
//            if (true) { // to expensive this approach
//                BDDGraph graph = sol.getGraphGame();
//                size = graph.getSize();
////            Logger.getInstance().addMessage(true, "Number of states of the two-player game over a finite graph: " + size);
//                System.out.println("Number of states of the two-player game over a finite graph by creating graph: " + size); // todo: fix the logger...
//                name = graph.getName();
//            }
//            // version to put everything in one file
//            try {
//                String content = file + ": " + sizeBDD + "\n";
//                Files.write(Paths.get(output), content.getBytes(), StandardOpenOption.APPEND);
//            } catch (IOException e) {
//            }
//            String content = "" + sizeBDD;
//            Tools.saveFile(output, content);
            return;
        }

        String output_bench = null;
        if (line.hasOption(PARAMETER_INTERN_OUTPUT)) {
            output_bench = line.getOptionValue(PARAMETER_INTERN_OUTPUT);
//            Logger.getInstance().setPath(output_bench);
//            Logger.getInstance().setOutput(Logger.OUTPUT.FILE);
            // Output standard sizes
            StringBuilder sb = new StringBuilder();
            sb.append("$\\#\\mathit{Tok}$ &  $\\#\\mathit{Var}$ & $\\#\\pl$ & $\\#\\tr$ & \\emph{time} & \\emph{memory} & $\\#\\pl_\\mathit{str}$ & $\\#\\tr_{\\mathit{str}}$\\\\\n");
            sb.append("sizes:").append(sol.getSolvingObject().getMaxTokenCount()).append("  &  ").append(sol.getVariableNumber());
            sb.append("  &  ").append(sol.getGame().getPlaces().size()).append("  &  ").append(sol.getGame().getTransitions().size());
            Tools.saveFile(output_bench, sb.toString());
            Logger.getInstance().addMessage(sb.toString(), false);
        }
        // Output strategy
        try {
            PetriGameWithTransits pn = sol.getStrategy();
            PNWTTools.savePnwt2Dot(IOParameters.getOutput(line), pn, true);
            if (output_bench != null) {
                try (BufferedWriter wr = new BufferedWriter(new FileWriter(output_bench, true))) {
                    wr.append("\nsizes_strat:").append(Integer.toString(pn.getPlaces().size())).append("  &  ").append(Integer.toString(pn.getTransitions().size()));
                };
            }
        } catch (NoStrategyExistentException nsee) {
            if (output_bench != null) {
                try (BufferedWriter wr = new BufferedWriter(new FileWriter(output_bench, true))) {
                    wr.append("\nsizes_strat:-  &  -");
                };
            }
            throw nsee;
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
