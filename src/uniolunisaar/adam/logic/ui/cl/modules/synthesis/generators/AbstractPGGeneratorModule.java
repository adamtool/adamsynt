package uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniolunisaar.adam.ds.synthesis.highlevel.HLPetriGame;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.logic.ui.cl.modules.server.AbstractServerModule;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.util.HLTools;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class AbstractPGGeneratorModule extends AbstractServerModule {

    protected static final String PARAMETER_OUTPUT = "o";
    private static final String PARAMETER_NO_PARTITION = "np";
    private static final String PARAMETER_PDF = "pdf";
    private static final String PARAMETER_DOT = "dot";
    private int nbIntParameters = 0;

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        OptionBuilder.hasArg();
        OptionBuilder.withArgName("file");
        OptionBuilder.withDescription("The output path where the generated Petri game should be saved.");
        OptionBuilder.isRequired();
        OptionBuilder.withLongOpt("output");
        options.put(PARAMETER_OUTPUT, OptionBuilder.create(PARAMETER_OUTPUT));

        OptionBuilder.withDescription("If set, no automatical partition of the places is done."
                + " Thus, no annotation from token to places is printed in the resulting file in APT format.");
        OptionBuilder.withLongOpt("no_partition");
        options.put(PARAMETER_NO_PARTITION, OptionBuilder.create(PARAMETER_NO_PARTITION));

        OptionBuilder.withDescription("Additionally creates a dot file.");
        options.put(PARAMETER_DOT, OptionBuilder.create(PARAMETER_DOT));

        OptionBuilder.withDescription("Additionally creates a PDF file.");
        options.put(PARAMETER_PDF, OptionBuilder.create(PARAMETER_PDF));

//        // Add server behavior
//        options.putAll(ServerOptions.createOptions());
        ////        OptionBuilder.withDescription("If set, no maximal number of token needed for a disjunct partition "
////                + "of the places is annotated to the net.");
////        OptionBuilder.withLongOpt("no_maxToken");
////        options.addOption(OptionBuilder.create("nmt"));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws Exception {
        super.execute(line);
        super.addServerParameter(AdamProtocolInputKeys.GEN_PARTITION, !line.hasOption(PARAMETER_NO_PARTITION));
    }

    void addIntParameter(Map<String, Option> options, String id, String desc) {
        ++nbIntParameters;
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("numberOf_" + id);
        OptionBuilder.withDescription(desc);
        OptionBuilder.isRequired();
        OptionBuilder.withLongOpt("nb_" + id);
        OptionBuilder.withType(Number.class);
        String name = "nb" + nbIntParameters;
        options.put(name, OptionBuilder.create(name));
    }

    int getIntParameter(String id, CommandLine line) throws ParseException {
        return ((Number) line.getParsedOptionValue("nb_" + id)).intValue();
    }

    boolean doPartition(CommandLine line) {
        return !(line.hasOption(PARAMETER_NO_PARTITION));
    }

    void save(PetriGameWithTransits game, CommandLine line) throws FileNotFoundException, ModuleException, IOException, InterruptedException {
        String output = line.getOptionValue(PARAMETER_OUTPUT);
        Tools.savePN(output, game);
        boolean pdf = line.hasOption(PARAMETER_PDF);
        boolean dot = line.hasOption(PARAMETER_DOT);
        if (pdf && !dot) {
            PGTools.savePG2PDF(output, game, true);
        } else if (pdf && dot) {
            PGTools.savePG2DotAndPDF(output, game, true);
        } else if (!pdf && dot) {
            PGTools.savePG2Dot(output, game, true);
        }
        //        boolean maxToken = !(line.hasOption("nmt"));
    }

    void save(HLPetriGame game, CommandLine line) throws FileNotFoundException, ModuleException, IOException, InterruptedException {
        String output = line.getOptionValue(PARAMETER_OUTPUT);
//        Tools.savePN(output, game);
        boolean pdf = line.hasOption(PARAMETER_PDF);
        boolean dot = line.hasOption(PARAMETER_DOT);
        if (pdf && !dot) {
            HLTools.saveHLPG2PDF(output, game);
        } else if (pdf && dot) {
            HLTools.saveHLPG2DotAndPDF(output, game, true);
        } else if (!pdf && dot) {
            HLTools.saveHLPG2Dot(output, game, true);
        }
    }

    void handleServer(AdamProtocolCmds cmd, String output) throws IOException, UnknownHostException, Exception {
        ProtocolOutput out = super.getServerOutput(cmd);
        String aptFile = out.getString(AdamProtocolOutputKeys.RESULT_APT);
        byte[] aptPDF = out.getFile(AdamProtocolOutputKeys.RESULT_PDF);
        Tools.saveFile(output + ".apt", aptFile);
        Tools.saveFile(output + ".pdf", aptPDF);
        super.closeServer();
    }
}
