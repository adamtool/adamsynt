package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import uniol.apt.analysis.exception.UnboundedException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;

/**
 *
 * @author Manuel Gieseking
 */
public class WinStrat extends AbstractSolverModule {

    private static final String name = "win_strat";
    private static final String descr = "Creates a deadlock-avoiding winning"
            + " strategy (system players) for the in APT format given Petri game if existent."
            + " Saves the strategy in the given folder as dot, and if dot is executable, as pdf.";

    @Override
    public void execute(CommandLine line) throws IOException, NoSuitableDistributionFoundException, NoStrategyExistentException, InterruptedException, CommandLineParseException, uniol.apt.io.parser.ParseException, UnboundedException, ClassNotFoundException, Exception {
        super.execute(line, MODULES.WIN_STRAT);
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
