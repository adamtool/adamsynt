package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import uniol.apt.analysis.exception.UnboundedException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.data.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.SolverParameters;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;

/**
 *
 * @author Manuel Gieseking
 */
public class ExWinStrat extends AbstractSolverModule {

    private static final String name = "ex_win_strat";
    private static final String descr = "Returns true if there is a"
            + " deadlock-avoiding winning strategy (system players) for the in APT format given Petri game.";

    @Override
    /**
     * Deletes the output and the export to tikz flag.
     * TODO: And remove the additional output types -gg -ggs -npgs for bdd solver
     */
    public Map<String, Option> createOptions() {
        Map<String, Option> ops = super.createOptions();
        ops.remove(IOParameters.getPARAMETER_OUTPUT());
        ops.remove(SolverParameters.getPARAMETER_TIKZ());
        return ops;
    }

    @Override
    public void execute(CommandLine line) throws IOException, NetNotSafeException, NoSuitableDistributionFoundException, CommandLineParseException, uniol.apt.io.parser.ParseException, UnboundedException, ClassNotFoundException, Exception {
        super.execute(line, MODULES.EX_WIN_STRAT);
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
