package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.highlevel.HLPetriGame;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.generators.hl.DocumentWorkflowHL;
import uniolunisaar.adam.generators.pgwt.Clerks;
import uniolunisaar.adam.logic.pg.converter.hl.HL2PGConverter;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolInputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class DocumentWorkflowModule extends AbstractPGGeneratorModule {

    private static final String name = "gen_clerks";
    private static final String descr = "Generates"
            + " the given number of clerks signing a document and saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Document Workflow examples of the ADAM paper.";
    private static final String PARAMETER_CLERKS = "clerks";
    private static final String PARAMETER_SIMPLE = "s";
    private static final String PARAMETER_HL = "hl";
    private static final String PARAMETER_LLHL = "llhl";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        addIntParameter(options, PARAMETER_CLERKS, "The desired number of Clerks signing the document (>= 1).");

        OptionBuilder.withDescription("If set, it will be generated the liberal version"
                + " (every clerk has to vote for yes). Concurrency-preserving version (DWs in ADAM paper).");
        OptionBuilder.withLongOpt("simple");
        options.put(PARAMETER_SIMPLE, OptionBuilder.create(PARAMETER_SIMPLE));

        OptionBuilder.withDescription("If set, creates the corresponding high-level version.");
        OptionBuilder.withLongOpt("high-level");
        options.put(PARAMETER_HL, OptionBuilder.create(PARAMETER_HL));

        OptionBuilder.withDescription("If set, creates the automatically created low-level version from the corresponding high-level version.");
        OptionBuilder.withLongOpt("low-level");
        options.put(PARAMETER_LLHL, OptionBuilder.create(PARAMETER_LLHL));

        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, InterruptedException, FileNotFoundException, ModuleException, ParseException, Exception {
        super.execute(line);

        int nb_clerks = getIntParameter(PARAMETER_CLERKS, line);
        boolean allyes = line.hasOption(PARAMETER_SIMPLE);
        if (isServerActive()) {
            super.addServerParameter(AdamProtocolInputKeys.GEN_INT_1, nb_clerks);
            super.addServerParameter(AdamProtocolInputKeys.GEN_DW_YES, allyes);
            super.handleServer(AdamProtocolCmds.GEN_DW, line.getOptionValue(PARAMETER_OUTPUT));
        } else {

            boolean hl = line.hasOption(PARAMETER_HL);
            if (hl) {
                HLPetriGame net = allyes ? DocumentWorkflowHL.generateDWs(nb_clerks, doPartition(line))
                        : DocumentWorkflowHL.generateDW(nb_clerks, doPartition(line));
                save(net, line);
            }
            boolean ll = line.hasOption(PARAMETER_LLHL);
            if (ll) {
                HLPetriGame net = allyes ? DocumentWorkflowHL.generateDWs(nb_clerks, doPartition(line))
                        : DocumentWorkflowHL.generateDW(nb_clerks, doPartition(line));
                PetriGameWithTransits pnet = HL2PGConverter.convert(net);
                save(pnet, line);
            }

            if (!hl && !ll) {
                PetriGameWithTransits net = allyes ? Clerks.generateCP(nb_clerks, doPartition(line), false)
                        : Clerks.generateNonCP(nb_clerks, doPartition(line), false);
                save(net, line);
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
