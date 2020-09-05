package uniolunisaar.adam.logic.ui.cl.modules.converter.highlevel;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.synthesis.highlevel.HLPetriGame;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.logic.synthesis.transformers.highlevel.HL2PGConverter;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.ds.ui.cl.parameters.IOParameters;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.ui.cl.modules.converter.AbstractConverterModule;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 */
public class HL2LL extends AbstractConverterModule {

    private static final String name = "hl2ll";
    private static final String descr = "Converts a high-level Petri game in"
            + " APT format to a low-level Petri game and saves it as APT and pdf file by using Graphviz (dot has to be executable).";

    @Override
    public void execute(CommandLine line) throws IOException, ParseException, InterruptedException, CommandLineParseException, ClassNotFoundException, Exception {
        super.execute(line);
        String output = IOParameters.getOutput(line);
        HLPetriGame hlgame = null; // TODO: write a APT-HL-Parser
        PetriGameWithTransits game = HL2PGConverter.convert(hlgame);
        String apt = PGTools.getAPT(game, false, false);
        Tools.saveFile(output, apt);
        PGTools.savePG2DotAndPDF(apt, IOParameters.getOutput(line), false);
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
