package uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.LoopUnrolling;

/**
 *
 * @author Manuel Gieseking
 */
public class LoopUnrollingModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_loopUnrolling";
    private static final String descr = "Generates"
            + " the loop unrolling example. Saves the resulting net in APT and dot format and,"
            + " if dot is executable, as pdf.";
    private static final String PARAMETER_NB_ROLLINGS = "rollings";
    private static final String PARAMETER_NEW_CHAINS = "nc";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_NB_ROLLINGS, "The desired number of unrollings (> 0).");
        OptionBuilder.withDescription("Should new chains be created.");
        OptionBuilder.withLongOpt("new_chains");
        options.put(PARAMETER_NEW_CHAINS, OptionBuilder.create(PARAMETER_NEW_CHAINS));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_unrollings = getIntParameter(PARAMETER_NB_ROLLINGS, line);
        if (isServerActive()) {
//            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_systems);
//            super.handleServer(AdamProtocolCmds.GEN_SS, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = (line.hasOption(PARAMETER_NEW_CHAINS)) ? LoopUnrolling.createESafetyVersion(nb_unrollings, true, doPartition(line)) : LoopUnrolling.createESafetyVersion(nb_unrollings, false, doPartition(line));
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
