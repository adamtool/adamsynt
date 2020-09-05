package uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.ds.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.ui.cl.modules.server.AbstractServerModule;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.util.PGTools;

/**
 * Should also extend AbstractConverterModule. We copied the only method
 * 'createOptions' by hand.
 *
 * @author Manuel Gieseking
 */
public class Pg2Dot extends AbstractServerModule {

    private final String name = "pg2dot";
    private final String descr = "Converts a Petri game in APT format to a dot file.";

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();
        // Add IO
        options.putAll(IOParameters.createOptions());
        return options;
    }

    @Override
    public void execute(CommandLine line) throws IOException, CommandLineParseException, ClassNotFoundException, ParseException, Exception {
        super.execute(line);
        if (isServerActive()) {
            String aptFile = Tools.readFile(IOParameters.getInput(line));
            super.addServerParameter(AdamProtocolInputKeys.INPUT, aptFile);
            // handle result
            ProtocolOutput out = getServerOutput(AdamProtocolCmds.CONV_PG2DOT);
            String dot = out.getString(AdamProtocolOutputKeys.RESULT_DOT);
            String output = IOParameters.getOutput(line);
            Tools.saveFile(output + ".dot", dot);
            closeServer();
        } else {
            PGTools.savePG2Dot(IOParameters.getInput(line), IOParameters.getOutput(line), false);
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
