package uniolunisaar.adam.data.ui.cl.parameters.synthesis;

import java.io.IOException;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.symbolic.bddapproach.BDDParameters;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.logic.synthesis.solver.Solver;
import uniolunisaar.adam.ds.synthesis.solver.SolverOptions;
import uniolunisaar.adam.ds.synthesis.solver.SolvingObject;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.ui.cl.synthesis.NoSuitableSolverException;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.bounded.qbfapproach.QBFParameters;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.bounded.qbfconcurrent.QBFConParameters;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.symbolic.mtbddapproach.MTBDDParameters;
import uniolunisaar.adam.ds.synthesis.pgwt.IPetriGame;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.SolverHandle;

/**
 *
 * @author Manuel Gieseking
 */
public class SolverParameters {

    private static final String PARAMETER_SKIP = "s";
    private static final String PARAMETER_EXPERIMENTAL = "exp";
    private static final String PARAMETER_TIKZ = "tikz";
    private static final String PARAMETER_DOT = "dot";
    private static final String PARAMETER_NOPDF = "nopdf";
    private static final String PARAMETER_SOLV = "sol";

    private static final Map<String, SpecificSolverParameters> solver = new HashMap<>();

    static {
        solver.put("bdd", new BDDParameters());
        solver.put("mtbdd", new MTBDDParameters());
        solver.put("qbf", new QBFParameters());
        solver.put("con", new QBFConParameters());
    }
//    private static final String PARAMETER_SOLV_BDD = "bdd";
//    private static final String PARAMETER_SOLV_QBF = "qbf";

//    public static Map<String, OptionGroup> createOptionGroups() {
//        Map<String, OptionGroup> options = new HashMap<>();
//        OptionBuilder.hasArg();
//        OptionBuilder.withArgName("options");
//        OptionBuilder.withDescription("Choose the BDD solver to do the job. Set the options with {options} as argument. Possible options: " + BDDOptions.printHelp());
//        Option bdd = OptionBuilder.create(PARAMETER_SOLV_BDD);
//
//        OptionBuilder.hasArg();
//        OptionBuilder.withArgName("options");
//        OptionBuilder.withDescription("Choose the QBF solver to do the job. Set the options with {options} as argument. Possible options: ");
//        Option qbf = OptionBuilder.create(PARAMETER_SOLV_QBF);
//        OptionGroup group = new OptionGroup();
//        group.addOption(bdd);
//        group.addOption(qbf);
//        group.setRequired(true);
//        try {
//            group.setSelected(bdd);
//        } catch (AlreadySelectedException ex) {
//            System.out.println("error");
//        }
//        options.put("solver", group);
//        return options;
//    }
    public static Map<String, Option> createOptions() {
//    public static Options createOptions() {
        Map<String, Option> options = new HashMap<>();
//        Options options = new Options();

//        OptionBuilder.hasArg();
//        OptionBuilder.withArgName("num");
//        OptionBuilder.withDescription("The maximum number of partitions needed to "
//                + " portion the places disjunct, such that there is no state within the reachability graph"
//                + " of the underlying net, marked with two places from the same partition. This number normally"
//                + " corresponds to the maximum number of token ever visible within the reachability graph."
//                + " Often ADAM can calculate them on it's own. But for saving time, or using other partitions you "
//                + " can use this flag or annotate it within the input file. Only use this flag if you are aware"
//                + " of the consequences! Otherwise this can lead to undefined behavior!");
//        OptionBuilder.withLongOpt("maxNbPartitions");
//        OptionBuilder.withType(Number.class);
//        options.addOption(OptionBuilder.create("mp"));
//        OptionBuilder.withDescription("Use the fallback to the old and very slow version."
//                + " If ADAM was not able to partition the places on its own and "
//                + " you do not want to annotate the places within the input file, you can "
//                + " try this slow version of ADAM. All other optional parameters do not"
//                + " have any effect. ATTENTION: It's a experimental version."
//                + " We strongly recommend to annotate the places on your own and"
//                + " use the standard version!");
//        OptionBuilder.withLongOpt("fallback");
//        options.addOption(OptionBuilder.create("f"));
//        OptionBuilder.withDescription("Use the experimental version. Trys to find a strategy without "
//                + "a given distribution annotated to the places. The Petri net must be "
//                + "concurreny-preserving. Currently still very slow. No other optional "
//                + "parameters have any effect.");
//        OptionBuilder.withLongOpt("experimental");
//        options.put(PARAMETER_EXPERIMENTAL, OptionBuilder.create(PARAMETER_EXPERIMENTAL));
////        options.addOption(OptionBuilder.create(PARAMETER_EXPERIMENTAL));
        String descr = "The solver with which the game should be solved. Currently:\n";
        for (Map.Entry<String, SpecificSolverParameters> entry : solver.entrySet()) {
            String key = entry.getKey();
            SpecificSolverParameters value = entry.getValue();
            descr += value.getUsage(key) + "\n";
        }
        descr += "are available.";
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("sol='<options>'");
        OptionBuilder.withDescription(descr);
        OptionBuilder.isRequired();
        OptionBuilder.withLongOpt("solver");
        options.put(PARAMETER_SOLV, OptionBuilder.create(PARAMETER_SOLV));

        OptionBuilder.withDescription("Skips the tests like bounded. Saves time,"
                + " but should only be used if you are asure that your net fullfills "
                + " all necessary preconditions!");
        OptionBuilder.withLongOpt("skip");
        options.put(PARAMETER_SKIP, OptionBuilder.create(PARAMETER_SKIP));
//        options.addOption(OptionBuilder.create(PARAMETER_SKIP));

        OptionBuilder.withDescription("Additionally exports all output files "
                + "as a tikz file for latex.");
        options.put(PARAMETER_TIKZ, OptionBuilder.create(PARAMETER_TIKZ));

        OptionBuilder.withDescription("Additionally saves the output files "
                + "as a dot.");
        options.put(PARAMETER_DOT, OptionBuilder.create(PARAMETER_DOT));

        OptionBuilder.withDescription("Do not generate the pdfs of the output files.");
        options.put(PARAMETER_NOPDF, OptionBuilder.create(PARAMETER_NOPDF));
//        options.addOption(OptionBuilder.create(PARAMETER_TIKZ));
        return options;
    }

    public static boolean skip(CommandLine line) {
        return line.hasOption(PARAMETER_SKIP);
    }

    public static boolean tikzExport(CommandLine line) {
        return line.hasOption(PARAMETER_TIKZ);
    }

    public static boolean saveDot(CommandLine line) {
        return line.hasOption(PARAMETER_DOT);
    }

    public static boolean noPdf(CommandLine line) {
        return line.hasOption(PARAMETER_NOPDF);
    }

    //        if (line.hasOption("mp")) {
//            int maxToken = ((Number) line.getParsedOptionValue("mp")).intValue();
//            net.putExtension("MAXTOKEN", maxToken);
//        }
    public static String getPARAMETER_TIKZ() {
        return PARAMETER_TIKZ;
    }

    public static String getPARAMETER_EXPERIMENTAL() {
        return PARAMETER_EXPERIMENTAL;
    }

    public static SolverHandle<? extends Solver<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ?>>, ? extends SolverOptions>, ? extends SpecificSolverParameters> getSolverHandle(String input, boolean skip, CommandLine line) throws ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, NoSuitableSolverException, uniol.apt.io.parser.ParseException, ParameterMissingException, SolvingException {
        String solverLine = line.getOptionValue(PARAMETER_SOLV);

        for (Map.Entry<String, SpecificSolverParameters> entry : solver.entrySet()) {
            String key = entry.getKey();
            SpecificSolverParameters value = entry.getValue();
            if (solverLine.equals(key) || solverLine.startsWith(key + "=")) { // specific solver choosen
                return value.createSolverHandle(input, skip, key, solverLine.substring(key.length()));
            }
        }

        throw new NoSuitableSolverException(solverLine);
    }

}
