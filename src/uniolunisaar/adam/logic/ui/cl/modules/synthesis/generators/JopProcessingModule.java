package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.ManufactorySystem;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolInputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class JopProcessingModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_workflow";
    private static final String descr = "Generates"
            + " the workflow examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Job Processing example of the ADAM paper.";
    private static final String PARAMETER_MACHINES = "machines";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_MACHINES, "The desired number of machines (>= 2).");
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_machines = getIntParameter(PARAMETER_MACHINES, line);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_machines);
            super.handleServer(AdamProtocolCmds.GEN_JP, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = ManufactorySystem.generate(nb_machines, doPartition(line), false);
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
