package uniolunisaar.adam.ds.ui.cl.parameters.synthesis.symbolic;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 *
 * @author Manuel Gieseking
 */
public class SymbolicSolverParameters {

    private static final String PARAMETER_GG = "gg";
    private static final String PARAMETER_GG_STRAT = "ggs";
    private static final String PARAMETER_NPG_STRAT = "npgs";
    private static final String PARAMETER_NO_TYPE2 = "nt2";

    public static Map<String, Option> createOptions() {
        Map<String, Option> options = new HashMap<>();

        OptionBuilder.withDescription("Additionally saves the finite graph game to the given folder as dot, and if dot is executable, as pdf. (with suffix '_gg')\n"
                + "ATTENTION: The solution is often extremly large and needs much time to calculate and especially executing graphviz!");
        OptionBuilder.withLongOpt("graphgame");
        options.put(PARAMETER_GG, OptionBuilder.create(PARAMETER_GG));

        OptionBuilder.withDescription("Additionally saves the finite graph game strategy to the given folder as dot, and if dot is executable, as pdf. (with suffix '_gg_strat')");
        OptionBuilder.withLongOpt("graphgame_strat");
        options.put(PARAMETER_GG_STRAT, OptionBuilder.create(PARAMETER_GG_STRAT));

        OptionBuilder.withDescription("Do not create a Petri game strategy.");
        OptionBuilder.withLongOpt("no_pg_strat");
        options.put(PARAMETER_NPG_STRAT, OptionBuilder.create(PARAMETER_NPG_STRAT));

        OptionBuilder.withDescription("This game does not contain any type2 situations. Try to use this hint for optimizations.");
        OptionBuilder.withLongOpt("no_type2");
        options.put(PARAMETER_NO_TYPE2, OptionBuilder.create(PARAMETER_NO_TYPE2));

        return options;
    }

    public static boolean createGraphGame(CommandLine line) {
        return line.hasOption(PARAMETER_GG);
    }

    public static boolean createGraphGameStrategy(CommandLine line) {
        return line.hasOption(PARAMETER_GG_STRAT);
    }

    public static boolean createPetriGameStrategy(CommandLine line) {
        return !line.hasOption(PARAMETER_NPG_STRAT);
    }

    public static boolean hasNoType2(CommandLine line) {
        return line.hasOption(PARAMETER_NO_TYPE2);
    }
}
