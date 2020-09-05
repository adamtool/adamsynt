package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

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
import uniolunisaar.adam.generators.highlevel.ConcurrentMachinesHL;
import uniolunisaar.adam.generators.pgwt.Workflow;
import uniolunisaar.adam.logic.synthesis.transformers.highlevel.HL2PGConverter;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class ConcurrentMachinesModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_workflow2";
    private static final String descr = "Generates"
            + " the workflow2 examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Concurrent Machines example of the ADAM paper.";
    private static final String PARAMETER_MACHINES = "machines";
    private static final String PARAMETER_WORKPIECES = "workpieces";
    private static final String PARAMETER_VERSION = "bv";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        addIntParameter(options, PARAMETER_MACHINES, "The desired number of concurrent machines (>= 2).");
        addIntParameter(options, PARAMETER_WORKPIECES, "The desired number of workpieces to produce (>= 1).");

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("version");
        OptionBuilder.withDescription("The desired version of the benchmark:"
                + " CAV15,"
                + " BJ19 - version for BJ festschrift"
                + " IMP - version only having one place for testing"
                + " HLBJ19 - high-level version"
                + " HLBJ19-A - high-level version with setMinus term"
                + " LLHLBJ19 - low-level version automatically converted from HLBJ19"
                + " LLHLBJ19-A - low-level version automatically converted from HLBJ19-A"
                + " (CAV15 is standard).");
        OptionBuilder.withLongOpt("version");
        options.put(PARAMETER_VERSION, OptionBuilder.create(PARAMETER_VERSION));

        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_machines = getIntParameter(PARAMETER_MACHINES, line);
        int nb_workpieces = getIntParameter(PARAMETER_WORKPIECES, line);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_machines);
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_2, nb_workpieces);
            super.handleServer(AdamProtocolCmds.GEN_CM, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = null;
            HLPetriGame hlnet = null;
            if (line.hasOption(PARAMETER_VERSION)) {
                String version = line.getOptionValue(PARAMETER_VERSION);
                if (version.equals("CAV15")) {
                    net = Workflow.generateNewAnnotationPoster(nb_machines, nb_workpieces, doPartition(line), false);
                } else if (version.equals("IMP")) {
                    net = Workflow.generateImprovedVersion(nb_machines, nb_workpieces, doPartition(line), false);
                } else if (version.equals("BJ19")) {
                    net = Workflow.generateBJVersion(nb_machines, nb_workpieces, doPartition(line), false);
                } else if (version.equals("HLBJ19")) {
                    hlnet = ConcurrentMachinesHL.generateImprovedVersion(nb_machines, nb_workpieces, doPartition(line));
                } else if (version.equals("LLHLBJ19")) {
                    HLPetriGame game = ConcurrentMachinesHL.generateImprovedVersion(nb_machines, nb_workpieces, doPartition(line));
                    net = HL2PGConverter.convert(game);
                } else if (version.equals("HLBJ19-A")) {
                    hlnet = ConcurrentMachinesHL.generateImprovedVersionWithSetMinus(nb_machines, nb_workpieces, doPartition(line));
                } else if (version.equals("LLHLBJ19-A")) {
                    HLPetriGame game = ConcurrentMachinesHL.generateImprovedVersionWithSetMinus(nb_machines, nb_workpieces, doPartition(line));
                    net = HL2PGConverter.convert(game);
                } else {
                    throw new ModuleException("The version '" + version + "' not yet implemented.");
                }
            } else {
                net = Workflow.generateNewAnnotationPoster(nb_machines, nb_workpieces, doPartition(line), false);
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
