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
import uniolunisaar.adam.generators.highlevel.AlarmSystemHL;
import uniolunisaar.adam.generators.pgwt.SecuritySystem;
import uniolunisaar.adam.logic.synthesis.transformers.highlevel.HL2PGConverter;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;

/**
 *
 * @author Manuel Gieseking
 */
public class SecuritySystemModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_securitySystem";
    private static final String descr = "Generates"
            + " the security system examples. Saves the resulting net in APT and dot format and,"
            + " if dot is executable, as pdf. (systems = 2 it is the burglar example)\n"
            + "A burglar (env) decides to break into some of the 'nb_systems' locations."
            + " the intruded system has to inform the other so that all systems can set "
            + "up the alarm. No false alarm, or not informing the other should occure.";
    private static final String PARAMETER_SYSTEMS = "systems";
    private static final String PARAMETER_VERSION = "bv";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_SYSTEMS, "The desired number of security system (>= 2).");

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("version");
        OptionBuilder.withDescription("The desired version of the benchmark:"
                + " SLL - safety low-level,"
                + " RLL - reachability low-level,"
                + " SLL19 - low-level safety version of xx paper,"
                + " SHL - safety high-level version,"
                + " SHL1 - safety high-level version with setMinus term,"
                + " SLLHL - low-level safety version automatically converted from SHL,"
                + " SLLHL1 - low-level safety version automatically converted from SHL1,"
                + " (SLL is standard).");
        OptionBuilder.withLongOpt("version");
        options.put(PARAMETER_VERSION, OptionBuilder.create(PARAMETER_VERSION));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_systems = getIntParameter(PARAMETER_SYSTEMS, line);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_systems);
            super.handleServer(AdamProtocolCmds.GEN_SS, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = null;
            HLPetriGame hlnet = null;
            if (line.hasOption(PARAMETER_VERSION)) {
                String version = line.getOptionValue(PARAMETER_VERSION);
                if (version.equals("SLL")) {
                    net = SecuritySystem.createSafetyVersion(nb_systems, doPartition(line));
                } else if (version.equals("RLL")) {
                    net = SecuritySystem.createReachabilityVersion(nb_systems, doPartition(line));
                } else if (version.equals("SLL19")) {
                    net = SecuritySystem.createSafetyVersionForHLRep(nb_systems, doPartition(line));
                } else if (version.equals("SHL")) {
                    hlnet = AlarmSystemHL.createSafetyVersionForHLRep(nb_systems, doPartition(line));
                } else if (version.equals("SLLHL")) {
                    HLPetriGame game = AlarmSystemHL.createSafetyVersionForHLRep(nb_systems, doPartition(line));
                    net = HL2PGConverter.convert(game);
                } else if (version.equals("SHL1")) {
                    hlnet = AlarmSystemHL.createSafetyVersionForHLRepWithSetMinus(nb_systems, doPartition(line));
                } else if (version.equals("SLLHL1")) {
                    HLPetriGame game = AlarmSystemHL.createSafetyVersionForHLRepWithSetMinus(nb_systems, doPartition(line));
                    net = HL2PGConverter.convert(game);
                } else {
                    throw new ModuleException("The version '" + version + "' not yet implemented.");
                }
            } else {
                net = SecuritySystem.createSafetyVersion(nb_systems, true);
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
