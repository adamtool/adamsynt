package uniolunisaar.adam.data.ui.cl.parameters.synthesis.symbolic.bddapproach;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.exceptions.distrsynt.symbolic.bddapproach.NoSuchBDDLibraryException;

/**
 *
 * @author Manuel Gieseking
 */
public class BDDLibraryParameters {

    private static final String PARAMETER_LIBRARY = "lib";
    private static final String PARAMETER_MAX_INCREASE = "mi";
    private static final String PARAMETER_INIT_NODE_NB = "nnb";
    private static final String PARAMETER_CACHE_SIZE = "cs";

    public static Map<String, Option> createOptions() {
//    public static Options createOptions() {
        Map<String, Option> options = new HashMap<>();
//        Options options = new Options();

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("lib");
        OptionBuilder.withDescription("The BDD library which you would like to use."
                + " Possible values: 'buddy', 'cudd', 'cal', 'java', 'jdd'. "
                + " If the chosen C library isn't available, ADAM automatically falls"
                + " back to the JavaBDD library ('java'). For more information see:"
                + " http://javabdd.sourceforge.net/.");
        OptionBuilder.withLongOpt("BDDlib");
        options.put(PARAMETER_LIBRARY, OptionBuilder.create(PARAMETER_LIBRARY));
//        options.addOption(OptionBuilder.create(PARAMETER_LIBRARY));

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("nb");
        OptionBuilder.withDescription("Sets the maximum number of nodes by which"
                + " to increase node table after a garbage collection for the BDD library.");
        OptionBuilder.withLongOpt("libMaxInc");
        OptionBuilder.withType(Number.class);
        options.put(PARAMETER_MAX_INCREASE, OptionBuilder.create(PARAMETER_MAX_INCREASE));
//        options.addOption(OptionBuilder.create(PARAMETER_MAX_INCREASE));

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("nb");
        OptionBuilder.withDescription("Sets the initial node table size for the BDD library.");
        OptionBuilder.withLongOpt("libNodeNb");
        OptionBuilder.withType(Number.class);
        options.put(PARAMETER_INIT_NODE_NB, OptionBuilder.create(PARAMETER_INIT_NODE_NB));
//        options.addOption(OptionBuilder.create(PARAMETER_INIT_NODE_NB));

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("nb");
        OptionBuilder.withDescription("Sets the operation cache size for the BDD library.");
        OptionBuilder.withLongOpt("libCacheSize");
        OptionBuilder.withType(Number.class);
        options.put(PARAMETER_CACHE_SIZE, OptionBuilder.create(PARAMETER_CACHE_SIZE));
//        options.addOption(OptionBuilder.create(PARAMETER_CACHE_SIZE));

        return options;
    }

    public static void setOptions(BDDSolverOptions options, CommandLine line) throws ParseException {
        // Set BDD library values
        if (line.hasOption(PARAMETER_LIBRARY)) {
            try {
                options.setLibraryName(line.getOptionValue(PARAMETER_LIBRARY));
            } catch (NoSuchBDDLibraryException e) {
                throw new ParseException(e.getMessage());
            }
        }
        if (line.hasOption(PARAMETER_MAX_INCREASE)) {
            options.setMaxIncrease(((Number) line.getParsedOptionValue(PARAMETER_MAX_INCREASE)).intValue());

        }
        if (line.hasOption(PARAMETER_INIT_NODE_NB)) {
            options.setInitNodeNb(((Number) line.getParsedOptionValue(PARAMETER_INIT_NODE_NB)).intValue());
        }
        if (line.hasOption(PARAMETER_CACHE_SIZE)) {
            options.setCacheSize(((Number) line.getParsedOptionValue(PARAMETER_CACHE_SIZE)).intValue());
        }

    }
}
