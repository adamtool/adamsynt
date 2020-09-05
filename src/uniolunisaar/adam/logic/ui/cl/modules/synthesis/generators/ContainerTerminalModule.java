package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.ContainerTerminal;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;

/**
 *
 * @author Manuel Gieseking
 */
public class ContainerTerminalModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_containerTerminal";
    private static final String descr = "Generates"
            + " the container terminal examples (synt2017). Saves the resulting net in APT and dot format and,"
            + " if dot is executable, as pdf. \n"
            + "Two robots should repetitively transport containers to specified container places (the parameter 'cps')"
            + " while avoiding an empty battery.";
    private static final String PARAMETER_CPS = "cps";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_CPS, "The desired number of container places (>= 2).");
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_systems = getIntParameter(PARAMETER_CPS, line);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_systems);
            super.handleServer(AdamProtocolCmds.GEN_CT, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = ContainerTerminal.createSafetyVersion(nb_systems, true);
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
