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
import uniolunisaar.adam.generators.pgwt.Philosopher;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class PhilosophersModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_phil";
    private static final String descr = "Generates the philosopher problem for"
            + " the given number of philosophers and saves the resulting net in the APT and dot format and, if dot is executable, as pdf.";
    private static final String PARAMETER_PHILOS = "philosophers";
    private static final String PARAMETER_NON_GUIDED = "ng";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        addIntParameter(options, PARAMETER_PHILOS, "The desired number of Philosophers eating (>= 2).");

        OptionBuilder.withDescription("If set, it will generated the non-guided variant, where the "
                + " environment is just one of the philosophers, otherwise the guided variant will be generated,"
                + " where the environment orders every philosopher to eat after another."
                + " (every clerk has to vote for yes). Concurrency-preserving version (DW in ADAM paper).");
        options.put(PARAMETER_NON_GUIDED, OptionBuilder.create(PARAMETER_NON_GUIDED));

        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_phils = getIntParameter(PARAMETER_PHILOS, line);
        boolean nonguided = line.hasOption("ng");
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_phils);
            super.addServerParameter(AdamProtocolInputKeys.GEN_PHIL_GUIDED, !nonguided);
            super.handleServer(AdamProtocolCmds.GEN_PHIL, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            boolean partition = doPartition(line);
            PetriGameWithTransits net = nonguided ? Philosopher.generateIndividual(nb_phils, partition, false)
                    : Philosopher.generateGuided2(nb_phils, partition, false);
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
