package uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniol.apt.io.parser.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.highlevel.HLPetriGame;
import uniolunisaar.adam.ds.synthesis.highlevel.symmetries.Symmetries;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.ds.objectives.local.Safety;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.generators.highlevel.AlarmSystemHL;
import uniolunisaar.adam.generators.highlevel.ConcurrentMachinesHL;
import uniolunisaar.adam.generators.highlevel.DocumentWorkflowHL;
import uniolunisaar.adam.generators.highlevel.PackageDeliveryHL;
import uniolunisaar.adam.generators.pgwt.Clerks;
import uniolunisaar.adam.generators.pgwt.SecuritySystem;
import uniolunisaar.adam.generators.pgwt.Workflow;
import uniolunisaar.adam.logic.synthesis.transformers.highlevel.HL2PGConverter;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.bddapproach.membership.BDDASafetyWithoutType2HLSolver;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolvingObject;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolver;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.explicit.ExplicitASafetyWithoutType2Solver;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.explicit.ExplicitSolverFactory;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.explicit.ExplicitSolverOptions;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.HLSolverOptions;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.bddapproach.canonicalreps.HLASafetyWithoutType2CanonRepSolverBDDApproach;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.bddapproach.canonicalreps.HLSolverFactoryBDDApproachCanonReps;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.hlapproach.HLASafetyWithoutType2SolverHLApproach;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.hlapproach.HLSolverFactoryHLApproach;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.llapproach.HLASafetyWithoutType2SolverLLApproach;
import uniolunisaar.adam.logic.synthesis.solver.twoplayergame.hl.llapproach.HLSolverFactoryLLApproach;
import uniolunisaar.adam.logic.ui.cl.modules.AbstractSimpleModule;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 */
public class BenchmarkCanonical2021 extends AbstractSimpleModule {

    private static final String name = "benchCanon2021";
    private static final String descr = "Just for benchmark purposes. Does not check any preconditions of the Petri game."
            + " Only prints whether there exists a winning strategy.";
    private static final String PARAMETER_OUTPUT = "o";
    private static final String PARAMETER_APPROACH = "a";
    private static final String PARAMETER_BENCHMARK = "b";
    private static final String PARAMETER_BDD_LIB = "lib";
    private static final String PARAMETER_INTER_TIMING = "time";

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
        OptionBuilder.withArgName("ll | llExpl | hl | hlByLL | hlBDD | canonBDD");
        OptionBuilder.withDescription("Chooses the approach "
                + "low-level symbolic (ll), "
                + "low-level explicit (llExpl), "
                + "high-level direct explicit (hl), "
                + "high-level first converting explicit (hlByLL), "
                + "high-level implicit (hlBDD), or "
                + "high-level by canonical representatives (canonBDD) for the creation of the graph game.");
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

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("<filename>");
        OptionBuilder.withDescription("Activate the timing of intermediate steps and storing the results into the given file.");
        OptionBuilder.withLongOpt("inter_timing");
        options.put(PARAMETER_INTER_TIMING, OptionBuilder.create(PARAMETER_INTER_TIMING));
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
        if (line.hasOption(PARAMETER_INTER_TIMING)) {
            String file = line.getOptionValue(PARAMETER_INTER_TIMING);
            Logger.getInstance().addMessageStream("INTERMEDIATE_TIMING", new PrintStream(file));
        }
        Logger.getInstance().setVerbose(false);
        Logger.getInstance().setVerboseMessageStream(null);

        if (approach.equals("ll")) {
            // This is different to RvG2019 since I think it's a fairer comparison
            // to let the algorithm also calculate the low-level game.
//            PetriGameWithTransits game = getLLGame(elem[elem.length - 1], para);
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);
            PetriGameWithTransits game = HL2PGConverter.convert(hlgame, true, true);
            BDDSolverOptions opt = new BDDSolverOptions(true);
            if (line.hasOption(PARAMETER_BDD_LIB)) {
                String lib = line.getOptionValue(PARAMETER_BDD_LIB);
                opt.setLibraryName(lib); // todo: check of correct input
            }
            opt.setNoType2(true);
            DistrSysBDDSolver<? extends Condition<?>> sol = DistrSysBDDSolverFactory.getInstance().getSolver(PGTools.getPetriGameFromParsedPetriNet(game, true, false), opt);
            sol.initialize();

            boolean exWinStrat = sol.existsWinningStrategy();

            System.out.println("Low-Level approach with BDDs. Exists winning strategy: " + exWinStrat); // todo: fix the logger...
            String content = "" + exWinStrat;

            Tools.saveFile(output, content);
        } else if (approach.equals("llExpl")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);
            PetriGameWithTransits game = HL2PGConverter.convert(hlgame, true, true);

            ExplicitASafetyWithoutType2Solver solverExp = (ExplicitASafetyWithoutType2Solver) ExplicitSolverFactory.getInstance().getSolver(game, new ExplicitSolverOptions());

            boolean exWinStrat = solverExp.existsWinningStrategy();

            System.out.println("Low-Level approach explicit. Exists winning strategy: " + exWinStrat); // todo: fix the logger...
            String content = "" + exWinStrat;
            Tools.saveFile(output, content);
        } else if (approach.equals("hl")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);

            HLASafetyWithoutType2SolverHLApproach solverHL = (HLASafetyWithoutType2SolverHLApproach) HLSolverFactoryHLApproach.getInstance().getSolver(hlgame, new HLSolverOptions(true));

            boolean exWinStrat = solverHL.existsWinningStrategy();

            System.out.println("High-level approach explicit directly as HL game. Exists winning strategy: " + exWinStrat); // todo: fix the logger...
            String content = "" + exWinStrat;
            Tools.saveFile(output, content);
//                    HLTools.saveGraph2DotAndPDF(output + "CM21_gg", graph);
        } else if (approach.equals("hlByLL")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);

            HLASafetyWithoutType2SolverLLApproach solverLL = (HLASafetyWithoutType2SolverLLApproach) HLSolverFactoryLLApproach.getInstance().getSolver(hlgame, new HLSolverOptions(true));

            boolean exWinStrat = solverLL.existsWinningStrategy();

            System.out.println("High-level approach explicit by first reducing to LL game. Exists winning strategy: " + exWinStrat); // todo: fix the logger...
            String content = "" + exWinStrat;
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

            boolean exWinStrat = sol.existsWinningStrategy();

            System.out.println("High-level approach with solving BDD inbetween. Exists winning strategy: " + exWinStrat); // todo: fix the logger...
            String content = "" + exWinStrat;
            Tools.saveFile(output, content);
        } else if (approach.equals("canonBDD")) {
            HLPetriGame hlgame = getHLGame(elem[elem.length - 1], para);

            BDDSolverOptions opt = new BDDSolverOptions(true);
            if (line.hasOption(PARAMETER_BDD_LIB)) {
                String lib = line.getOptionValue(PARAMETER_BDD_LIB);
                opt.setLibraryName(lib); // todo: check of correct input
            }
            opt.setNoType2(true);

            HLASafetyWithoutType2CanonRepSolverBDDApproach solver = (HLASafetyWithoutType2CanonRepSolverBDDApproach) HLSolverFactoryBDDApproachCanonReps.getInstance().getSolver(hlgame, opt);
            solver.getSolver().initialize();
            boolean exWinStrat = solver.existsWinningStrategy();

            System.out.println("Canonical representatives with BDDs. Exists winning strategy: " + exWinStrat); // todo: fix the logger...
            String content = "" + exWinStrat;
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
