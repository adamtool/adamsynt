package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.SelfOrganizingRobots;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolInputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class SelfReconfiguringRobotsModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_robots";
    private static final String descr = "Generates"
            + " the self-organizing robots examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Self-reconfiguring Robots example of the ADAM paper.";
    private static final String PARAMETER_ROBOTS = "robots";
    private static final String PARAMETER_WORKPIECES = "destroy";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        addIntParameter(options, PARAMETER_ROBOTS, "The desired number of robots and tools to use (>= 2).");
        addIntParameter(options, PARAMETER_WORKPIECES, "The desired number of phases to destroy tools (>= 0).");

        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);

        int nb_robots = getIntParameter(PARAMETER_ROBOTS, line);
        int nb_destroy = getIntParameter(PARAMETER_WORKPIECES, line);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_robots);
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_2, nb_destroy);
            super.handleServer(AdamProtocolCmds.GEN_SR, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = SelfOrganizingRobots.generate(nb_robots, nb_destroy, doPartition(line), false);
            save(net, line);
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
