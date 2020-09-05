package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import uniol.apt.analysis.exception.UnboundedException;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.pg.NoSuitableDistributionFoundException;
import uniolunisaar.adam.ds.synthesis.solver.Solver;
import uniolunisaar.adam.ds.synthesis.solver.SolverOptions;
import uniolunisaar.adam.ds.synthesis.solver.SolvingObject;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.data.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.SolverParameters;
import uniolunisaar.adam.logic.ui.cl.modules.server.AbstractServerModule;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.ui.cl.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.SpecificSolverParameters;
import uniolunisaar.adam.ds.petrigame.IPetriGame;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class AbstractSolverModule extends AbstractServerModule {

    enum MODULES {
        EX_WIN_STRAT,
        WIN_STRAT
    }

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        // Add IO
        options.putAll(IOParameters.createOptions());

        // Add solver options
        options.putAll(SolverParameters.createOptions());

        return options;
    }

//    @Override
//    protected Map<String, OptionGroup> createOptionGroups() {
//        Map<String, OptionGroup> groups = super.createOptionGroups();
//        groups.putAll(SolverOptions.createOptionGroups());
//        return groups;
//    }
    void execute(CommandLine line, MODULES module) throws IOException, NoSuitableDistributionFoundException, CommandLineParseException, ParseException, UnboundedException, NetNotSafeException, ClassNotFoundException, Exception {
        super.execute(line);

        // Input parameter
        String input = IOParameters.getInput(line);

        // Solver parameters
        boolean skip = SolverParameters.skip(line);

        // Output + tikz export
        String output = null;
        boolean tikz = false;
        boolean dot = false;
        boolean nopdf = false;
        if (module != MODULES.EX_WIN_STRAT) {
            output = IOParameters.getOutput(line);
            tikz = SolverParameters.tikzExport(line);
            dot = SolverParameters.saveDot(line);
            nopdf = SolverParameters.noPdf(line);
        }
        // Specific solver options
        SolverHandle<? extends Solver<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ?>>, ? extends SolverOptions>, ? extends SpecificSolverParameters> solHandle = SolverParameters.getSolverHandle(input, skip, line);

        // Server actions
        boolean isServerActive = isServerActive();
        if (isServerActive) {
            // Set input parameter
            String aptFile = Tools.readFile(input);
            SolverOptions opts = solHandle.getSolver().getSolverOpts();
            super.addServerParameter(AdamProtocolInputKeys.INPUT, aptFile);
            super.addServerParameter(AdamProtocolInputKeys.SOL_SKIP, skip);
            super.addServerParameter(AdamProtocolInputKeys.SOL_TIKZ, tikz);
            super.addServerParameter(AdamProtocolInputKeys.SOL_OPTS, opts);
        }
        switch (module) {
            case EX_WIN_STRAT: {
                ProtocolOutput pout = null;
                if (isServerActive) {
                    pout = super.getServerOutput(AdamProtocolCmds.SOL_EXWIN);
                }
                solHandle.existsWinningStrategy(pout);
                break;
            }
            case WIN_STRAT: {
                ProtocolOutput pout = null;
                if (isServerActive) {
                    pout = super.getServerOutput(AdamProtocolCmds.SOL_STRAT);
                }
                solHandle.createWinningStrategy(pout, output, tikz, dot, nopdf);
                break;
            }
        }
        if (isServerActive) {
            super.closeServer();
        }
    }

}
