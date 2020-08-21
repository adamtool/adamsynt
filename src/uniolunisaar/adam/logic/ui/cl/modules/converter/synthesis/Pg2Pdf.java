package uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.data.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.ui.cl.modules.server.AbstractServerModule;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ui.cl.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.util.PGTools;

/**
 * Should also extend AbstractConverterModule. We copied the only method
 * 'createOptions' by hand.
 *
 * @author Manuel Gieseking
 */
public class Pg2Pdf extends AbstractServerModule {

    private static final String name = "pg2pdf";
    private static final String descr = "Converts a Petri game in"
            + " APT format to a pdf file by using Graphviz (dot has to be executable).";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        // Add IO
        options.putAll(IOParameters.createOptions());
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, ParseException, InterruptedException, CommandLineParseException, ClassNotFoundException, Exception {
        super.execute(line);
        if (isServerActive()) {
            String aptFile = Tools.readFile(IOParameters.getInput(line));
            super.addServerParameter(AdamProtocolInputKeys.INPUT, aptFile);
            // handle result
            ProtocolOutput out = getServerOutput(AdamProtocolCmds.CONV_PG2PDF);
            String output = IOParameters.getOutput(line);
            Tools.saveFile(output + ".pdf", out.getFile(AdamProtocolOutputKeys.RESULT_PDF));
            closeServer();
        } else {
            PGTools.savePG2DotAndPDF(IOParameters.getInput(line), IOParameters.getOutput(line), false, isDebugging(line));
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
