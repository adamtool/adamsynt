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
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.ds.synthesis.highlevel.HLPetriGame;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.generators.highlevel.ClientServerHL;
import uniolunisaar.adam.generators.highlevel.PackageDeliveryHL;
import uniolunisaar.adam.generators.pgwt.Clerks;
import uniolunisaar.adam.generators.pgwt.ManufactorySystem;
import uniolunisaar.adam.generators.pgwt.SecuritySystem;
import uniolunisaar.adam.generators.pgwt.SelfOrganizingRobots;
import uniolunisaar.adam.generators.pgwt.Workflow;
import uniolunisaar.adam.logic.synthesis.transformers.highlevel.HL2PGConverter;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.mcutscheduling.safe.DistrSysBDDSolver;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.mcutscheduling.safe.DistrSysBDDSolverFactory;
import uniolunisaar.adam.logic.ui.cl.modules.AbstractSimpleModule;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 */
public class BenchmarkSynthesis2021 extends AbstractSimpleModule {

    private static final String name = "benchSynthesis2021";
    private static final String descr = "Just for benchmark purposes. Does not check any preconditions of the Petri game.";
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
        OptionBuilder.withArgName("ll | llExpl | hl | hlByLL | hlByLLCanon | hlBDD | canonBDD");
        OptionBuilder.withDescription("Chooses the approach "
                //                + "low-level symbolic (ll), "
                //                + "low-level explicit (llExpl), "
                //                + "high-level direct explicit (hl), "
                //                + "high-level first converting explicit (hlByLL), "
                //                + "high-level first converting explicit with canon reps (hlByLLCanon), "
                //                + "high-level implicit (hlBDD), or "
                //                + "high-level by canonical representatives (canonBDD) for the creation of the graph game.");
                + "currently there is only one. The parameter is not used.");
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

//        OptionBuilder.hasArg();
//        OptionBuilder.withArgName("<filename>");
//        OptionBuilder.withDescription("Activate the timing of intermediate steps and storing the results into the given file.");
//        OptionBuilder.withLongOpt("inter_timing");
//        options.put(PARAMETER_INTER_TIMING, OptionBuilder.create(PARAMETER_INTER_TIMING));
        return options;
    }

    /**
     *
     * @param line
     * @throws IOException
     * @throws InterruptedException
     * @throws FileNotFoundException
     * @throws ModuleException
     * @throws NetNotSafeException
     * @throws NoSuitableDistributionFoundException
     * @throws NoStrategyExistentException
     * @throws ParseException
     * @throws CommandLineParseException
     * @throws Exception
     */
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
//        if (line.hasOption(PARAMETER_INTER_TIMING)) {
//            String file = line.getOptionValue(PARAMETER_INTER_TIMING);
//            Logger.getInstance().addMessageStream("INTERMEDIATE_TIMING", new PrintStream(file));
//        }
//        Logger.getInstance().setVerbose(true);
//        Logger.getInstance().setVerboseMessageStream(null);
        Logger.getInstance().setErrorsWithStackTrace(true);

        String bench = elem[elem.length - 1];
        PetriGameWithTransits game = getLLGame(bench, para);
        BDDSolverOptions opt = new BDDSolverOptions(true);
        if (line.hasOption(PARAMETER_BDD_LIB)) {
            String lib = line.getOptionValue(PARAMETER_BDD_LIB);
            opt.setLibraryName(lib); // todo: check of correct input
        }
        opt.setNoType2(false);
        StringBuilder sb = new StringBuilder();

        PetriGameWithTransits pg = PGTools.getPetriGameFromParsedPetriNet(game, true, false);
        DistrSysBDDSolver<? extends Condition<?>> sol = DistrSysBDDSolverFactory.getInstance().getSolver(pg, opt);
        sol.initialize();
//        sb.append("#Tok, #Var, #P, #T, #P_s, #T_s\n");
//        sb.append("sizes:")
        sb.append(sol.getSolvingObject().getMaxTokenCount()).append("  &  ").append(sol.getVariableNumber());
        sb.append("  &  ").append(sol.getGame().getPlaces().size()).append("  &  ").append(sol.getGame().getTransitions().size());
        Tools.saveFile(output, sb.toString());

        
        PetriGameWithTransits strategy = null;
        try {
            strategy = sol.getStrategy();
        } catch (NoStrategyExistentException nse) {

        }
        boolean exWinStrat = strategy != null;

        sb = new StringBuilder();
        if (exWinStrat) {
//            sb.append("\nsizes_strat:").
            sb.append("  &  ").append(strategy.getPlaces().size()).append("  &  ").append(strategy.getTransitions().size());
        } else {
//            sb.append("\nsizes_strat:").
            sb.append("  &  ").append("-").append("  &  ").append("-");
        }

        sb.append("&").append(exWinStrat);

        Tools.saveFile(output, sb.toString(), true);
    }

    private PetriGameWithTransits getLLGame(String id, int[] paras) throws ModuleException {
        switch (id) {
            case "AS":
                return SecuritySystem.createSafetyVersionForHLRep(paras[0], true);
            case "CM":
                return Workflow.generateBJVersion(paras[0], paras[1], true, false);
            case "SR":
                // tools = robots for the benchmark 
                return SelfOrganizingRobots.generateImproved(paras[0], paras[0], paras[1], true, true); // not implemented the two boolean flags (partition, max token)
            case "JP":
                return ManufactorySystem.generate(paras[0], true, false);
            case "DW":
                return Clerks.generateNonCP(paras[0], true, false);
            case "DWs":
                return Clerks.generateCP(paras[0], true, false);
            case "PD":
                HLPetriGame hlgame = PackageDeliveryHL.generateEwithPool(paras[0], paras[1], true);
                return HL2PGConverter.convert(hlgame, true, true);
            case "CS": // not used
                return HL2PGConverter.convert(ClientServerHL.create(paras[0], true));
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
