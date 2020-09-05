package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.Watchdog;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class WatchdogModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_watchdog";
    private static final String descr = "Generates"
            + " the watchdog examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " Without partial observation: ============================ The enviroment set\n"
            + "  one machine of the n machines on fire. A smoke detector sounds the alarm. The\n"
            + "  watchdog can asked the smoke detector where the fire started and have to\n"
            + "  extinguish the right machine.\n"
            + " \n"
            + "  With partial observation: ========================= Same scenario as above,\n"
            + "  but in this case the smoke detector do not reveal where the fire started.\n"
            + "  Thus the watchdog has to go to each machine and take a look if it was set on\n"
            + "  fire. Still the goal is to extinguish the right machine.";
    private static final String PARAMETER_MACHINES = "machines";
    private static final String PARAMETER_SEARCH = "s";
    private static final String PARAMETER_PO = "po";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_MACHINES, "The desired number of machines (>= 1).");

        OptionBuilder.withDescription("If set, the version with the possibility for the robot to search"
                + " the burning machine is generated.");
        OptionBuilder.withLongOpt("search");
        options.put(PARAMETER_SEARCH, OptionBuilder.create(PARAMETER_SEARCH));

        OptionBuilder.withDescription("If set, the version where the environment don't reveal"
                + " which machine was set on fire is generated.");
        OptionBuilder.withLongOpt("partialObservation");
        options.put(PARAMETER_PO, OptionBuilder.create(PARAMETER_PO));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_machines = getIntParameter(PARAMETER_MACHINES, line);
        boolean search = line.hasOption(PARAMETER_SEARCH);
        boolean po = line.hasOption(PARAMETER_PO);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_machines);
            super.addServerParameter(AdamProtocolInputKeys.GEN_WD_SEARCH, search);
            super.addServerParameter(AdamProtocolInputKeys.GEN_WD_PO, po);
            super.handleServer(AdamProtocolCmds.GEN_WD, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = Watchdog.generate(nb_machines, search, po, true);
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
