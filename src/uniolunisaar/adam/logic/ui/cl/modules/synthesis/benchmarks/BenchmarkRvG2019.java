package uniolunisaar.adam.logic.ui.cl.modules.synthesis.benchmarks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.graph.GameGraph;
import uniolunisaar.adam.ds.graph.GameGraphFlow;
import uniolunisaar.adam.ds.graph.explicit.DecisionSet;
import uniolunisaar.adam.ds.graph.hl.hlapproach.HLDecisionSet;
import uniolunisaar.adam.ds.graph.hl.hlapproach.IHLDecision;
import uniolunisaar.adam.ds.graph.explicit.ILLDecision;
import uniolunisaar.adam.ds.highlevel.ColoredPlace;
import uniolunisaar.adam.ds.highlevel.ColoredTransition;
import uniolunisaar.adam.ds.highlevel.HLPetriGame;
import uniolunisaar.adam.ds.highlevel.oneenv.OneEnvHLPG;
import uniolunisaar.adam.ds.highlevel.symmetries.Symmetries;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.ds.objectives.Safety;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.generators.hl.AlarmSystemHL;
import uniolunisaar.adam.generators.hl.ConcurrentMachinesHL;
import uniolunisaar.adam.generators.hl.DocumentWorkflowHL;
import uniolunisaar.adam.generators.hl.PackageDeliveryHL;
import uniolunisaar.adam.generators.pgwt.Clerks;
import uniolunisaar.adam.generators.pgwt.SecuritySystem;
import uniolunisaar.adam.generators.pgwt.Workflow;
import uniolunisaar.adam.logic.pg.converter.hl.HL2PGConverter;
import uniolunisaar.adam.logic.pg.builder.graph.hl.SGGBuilderHL;
import uniolunisaar.adam.logic.pg.builder.graph.hl.SGGBuilderLL;
import uniolunisaar.adam.logic.pg.solver.hl.bddapproach.BDDASafetyWithoutType2HLSolver;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolvingObject;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolver;
import uniolunisaar.adam.logic.ui.cl.modules.AbstractSimpleModule;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 */
public class BenchmarkRvG2019 extends AbstractSimpleModule {

    private static final String name = "benchRvG2019";
    private static final String descr = "Just for benchmark purposes. Does not check any preconditions of the Petri game."
            + " Only prints the sizes of the low-level graph game and the corresponding symbolic graph game.";
    private static final String PARAMETER_OUTPUT = "o";
    private static final String PARAMETER_APPROACH = "a";
    private static final String PARAMETER_BENCHMARK = "b";
    private static final String PARAMETER_BDD_LIB = "lib";

    @Override
    protected Map<String, Option> createOptions() {
        Map<String, Option> options = new HashMap<>();

        // Add Benchmark specific ones
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("file");
        OptionBuilder.withDescription("The path to the output file for the data.");
        OptionBuilder.withLongOpt("out_bench");
        options.put(PARAMETER_OUTPUT, OptionBuilder.create(PARAMETER_OUTPUT));

        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("ll | hl | hlByLL | hlBDD");
        OptionBuilder.withDescription("Chooses the approach low-level (ll), high-level direct explicit (hl), high-level first converting explicit (hlByLL), or high-level implicit (hlBDD) for the creation of the graph game.");
        OptionBuilder.withLongOpt("approach");
        options.put(PARAMETER_APPROACH, OptionBuilder.create(PARAMETER_APPROACH));

        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("ID");
        OptionBuilder.withDescription("Chooses the benchmark by a proper identifier.");
        OptionBuilder.withLongOpt("benchmark");
        options.put(PARAMETER_BENCHMARK, OptionBuilder.create(PARAMETER_BENCHMARK));

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("buddy | cudd | cal | java");
        OptionBuilder.withDescription("Chooses the BDD library.");
        OptionBuilder.withLongOpt("library");
        options.put(PARAMETER_BDD_LIB, OptionBuilder.create(PARAMETER_BDD_LIB));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, NetNotSafeException, NoSuitableDistributionFoundException, NoStrategyExistentException, ParseException, CommandLineParseException, Exception {
        super.execute(line);

        String output = line.getOptionValue(PARAMETER_OUTPUT);
        String approach = line.getOptionValue(PARAMETER_APPROACH);
        String idInput = line.getOptionValue(PARAMETER_BENCHMARK);

        // Get the Parameter and bench mark ID
        String id = idInput.substring(idInput.lastIndexOf("/") + 1);
        String[] elem = id.split("_");
        int[] para = new int[elem.length - 1];
        for (int i = 0; i < elem.length - 1; i++) {
            para[i] = Integer.parseInt(elem[i]);
        }
        Logger.getInstance().setVerbose(false);
        Logger.getInstance().setVerboseMessageStream(null);

        if (approach.equals("ll")) {
            PetriGameWithTransits game = getLLGame(elem[elem.length - 1], para);
            BDDSolverOptions opt = new BDDSolverOptions(true);
            if (line.hasOption(PARAMETER_BDD_LIB)) {
                String lib = line.getOptionValue(PARAMETER_BDD_LIB);
                opt.setLibraryName(lib); // todo: check of correct input
            }
            opt.setNoType2(true);
            DistrSysBDDSolver<? extends Condition<?>> sol = DistrSysBDDSolverFactory.getInstance().getSolver(PGTools.getPetriGameFromParsedPetriNet(game, true, false), opt);
            sol.initialize();

            double sizeBDD = sol.getBufferedDCSs().satCount(sol.getFirstBDDVariables()) + 1; // for the additional init state
            System.out.println("Number of states of the LL two-player game over a finite graph by solving BDD: " + sizeBDD); // todo: fix the logger...

            String content = "" + sizeBDD;
            Tools.saveFile(output, content);
        } else if (approach.equals("hl")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);

//            SGGByHashCode<ColoredPlace, ColoredTransition, IHLDecision, HLDecisionSet, SGGFlow<ColoredTransition, IntegerID>> graph = SGGBuilderHL.getInstance().createByHashcode(new OneEnvHLPG(hlgame, true));
            GameGraph<ColoredPlace, ColoredTransition, IHLDecision, HLDecisionSet, GameGraphFlow<ColoredTransition, HLDecisionSet>> graph = SGGBuilderHL.getInstance().create(new OneEnvHLPG(hlgame, true));

            int size = graph.getStatesView().size();
            System.out.println("Number of states of the HL two-player game over a finite graph explizit directly by HL: " + size); // todo: fix the logger...
            String content = "" + size;
            Tools.saveFile(output, content);
//                    HLTools.saveGraph2DotAndPDF(output + "CM21_gg", graph);
        } else if (approach.equals("hlByLL")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);

//            SGGByHashCode<Place, Transition, ILLDecision, LLDecisionSet, SGGFlow<Transition, IntegerID>> graph = SGGBuilderLL.getInstance().createByHashcode(hlgame);
            GameGraph<Place, Transition, ILLDecision, DecisionSet, GameGraphFlow<Transition, DecisionSet>> graph = SGGBuilderLL.getInstance().create(hlgame);

            int size = graph.getStatesView().size();
            System.out.println("Number of states of the HL two-player game over a finite graph explizit by converting first to LL: " + size); // todo: fix the logger...
            String content = "" + size;
            Tools.saveFile(output, content);
//                    HLTools.saveGraph2DotAndPDF(output + "CM21_gg", graph);
        } else if (approach.equals("hlBDD")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);

            PetriGameWithTransits game = HL2PGConverter.convert(hlgame, true, true);
            Symmetries syms = hlgame.getSymmetries();

            BDDSolverOptions opt = new BDDSolverOptions(true);
            if (line.hasOption(PARAMETER_BDD_LIB)) {
                String lib = line.getOptionValue(PARAMETER_BDD_LIB);
                opt.setLibraryName(lib); // todo: check of correct input
            }
            BDDASafetyWithoutType2HLSolver sol = new BDDASafetyWithoutType2HLSolver(new DistrSysBDDSolvingObject<>(game, new Safety()), syms, opt);
            sol.initialize();

            double sizeBDD = sol.getBufferedDCSs().satCount(sol.getFirstBDDVariables()) + 1;

            System.out.println("Number of states of the HL two-player game over a finite graph BDD: " + sizeBDD); // todo: fix the logger...
            String content = "" + sizeBDD;
            Tools.saveFile(output, content);
        } else {
            throw new ModuleException("Approach " + approach + " not yet implemented.");
        }
    }

    private PetriGameWithTransits getLLGame(String id, int[] paras) throws ModuleException {
        switch (id) {
            case "AS":
                return SecuritySystem.createSafetyVersionForHLRep(paras[0], true);
            case "CM":
                return Workflow.generateBJVersion(paras[0], paras[1], true, false);
            case "DW":
                return Clerks.generateNonCP(paras[0], true, false);
            case "DWs":
                return Clerks.generateCP(paras[0], true, false);
            case "PD":
                HLPetriGame hlgame = PackageDeliveryHL.generateEwithPool(paras[0], paras[1], true);
                return HL2PGConverter.convert(hlgame, true, true);
            default:
                throw new ModuleException("Benchmark " + id + " not yet implemented.");
        }
    }

    private HLPetriGame getHLGame(String id, int[] paras) throws ModuleException {
        switch (id) {
            case "AS":
                return AlarmSystemHL.createSafetyVersionForHLRepWithSetMinus(paras[0], true);
            case "CM":
                return ConcurrentMachinesHL.generateImprovedVersionWithSetMinus(paras[0], paras[1], true);
            case "DW":
                return DocumentWorkflowHL.generateDW(paras[0], true);
            case "DWs":
                return DocumentWorkflowHL.generateDWs(paras[0], true);
            case "PD":
                return PackageDeliveryHL.generateEwithPool(paras[0], paras[1], true);
            default:
                throw new ModuleException("Benchmark " + id + " not yet implemented.");
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
