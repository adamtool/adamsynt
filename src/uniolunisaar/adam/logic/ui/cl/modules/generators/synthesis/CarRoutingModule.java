package uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.highlevel.HLPetriGame;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.CarRouting;

/**
 *
 * @author Manuel Gieseking
 */
public class CarRoutingModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_carRouting";
    private static final String descr = "Generates"
            + " the car routing example. Saves the resulting net in APT and dot format and,"
            + " if dot is executable, as pdf.";
    private static final String PARAMETER_CARS = "cars";
    private static final String PARAMETER_ROUTES = "routes";
    private static final String PARAMETER_VERSION = "bv";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_CARS, "The desired number of cars (>= 2).");
        addIntParameter(options, PARAMETER_ROUTES, "The desired number of routes (>= 2).");

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("version");
        OptionBuilder.withDescription("The desired version of the benchmark: A - all reach, E - exists reach, AR - all reach with re-routing (A is standard).");
        OptionBuilder.withLongOpt("version");
        options.put(PARAMETER_VERSION, OptionBuilder.create(PARAMETER_VERSION));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_cars = getIntParameter(PARAMETER_CARS, line);
        int nb_routes = getIntParameter(PARAMETER_ROUTES, line);
        if (isServerActive()) {
//            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_systems);
//            super.handleServer(AdamProtocolCmds.GEN_SS, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = null;
            HLPetriGame hlnet = null;
            if (line.hasOption(PARAMETER_VERSION)) {
                String version = line.getOptionValue(PARAMETER_VERSION);
                if (version.equals("A")) {
                    net = CarRouting.createAReachabilityVersion(nb_routes, nb_cars, doPartition(line));
                } else if (version.equals("E")) {
                    net = CarRouting.createEReachabilityVersion(nb_routes, nb_cars, doPartition(line));
                } else if (version.equals("AR")) {
                    net = CarRouting.createAReachabilityVersionWithRerouting(nb_routes, nb_cars, doPartition(line));
                } else {
                    throw new ModuleException("The version '" + version + "' not yet implemented.");
                }
            } else {
                net = CarRouting.createAReachabilityVersion(nb_routes, nb_cars, doPartition(line));
            }
            if (net != null) {
                save(net, line);
            } else {
                save(hlnet, line);
            }
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
