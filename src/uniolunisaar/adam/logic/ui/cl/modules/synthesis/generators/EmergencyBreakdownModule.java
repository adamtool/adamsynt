package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.pgwt.EmergencyBreakdown;

/**
 *
 * @author Manuel Gieseking
 */
public class EmergencyBreakdownModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_emergencyBreakdown";
    private static final String descr = "Generates TODO"
            + " the security system examples. Saves the resulting net in APT and dot format and,"
            + " if dot is executable, as pdf. (systems = 2 it is the burglar example)\n"
            + "A burglar (env) decides to break into some of the 'nb_systems' locations."
            + " the intruded system has to inform the other so that all systems can set "
            + "up the alarm. No false alarm, or not informing the other should occure.";
    private static final String PARAMETER_CRIT_MACH = "critical_machines";
    private static final String PARAMETER_NORM_MACH = "normal_machines";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        addIntParameter(options, PARAMETER_CRIT_MACH, "The desired number of security system (>= 2).");
        addIntParameter(options, PARAMETER_NORM_MACH, "The desired number of security system (>= 2).");
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);
        int nb_crit = getIntParameter(PARAMETER_CRIT_MACH, line);
        int nb_norm = getIntParameter(PARAMETER_NORM_MACH, line);
        if (isServerActive()) { // TODO: implement
//            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_systems);
//            super.handleServer(AdamProtocolCmds.GEN_SS, line.getOptionValue(PARAMETER_OUTPUT));
        } else {
            PetriGameWithTransits net = EmergencyBreakdown.createSafetyVersion(nb_crit, nb_norm, true);
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
