package uniolunisaar.adam.ds.ui.cl.parameters.synthesis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import uniolunisaar.adam.ds.synthesis.pgwt.IPetriGame;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.logic.synthesis.solver.Solver;
import uniolunisaar.adam.ds.synthesis.solver.SolverOptions;
import uniolunisaar.adam.ds.synthesis.solver.SolvingObject;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.SolverHandle;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class SpecificSolverParameters {

    protected abstract SolverHandle<? extends Solver<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ?>>, ? extends SolverOptions>, ? extends SpecificSolverParameters> createSolverHandle(String input, boolean skip, String name, String substring) throws SolvingException, ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException;

    protected abstract Map<String, Option> createOptions();

    public Options getOptions() {
        Options opts = new Options();
        for (Option opt : createOptions().values()) {
            opts.addOption(opt);
        }
        return opts;
    }

    /**
     * Converts the given parameterLine to a line understood by the CommandLine
     * parser. This means change : to blank and , to blank. This is needed
     * because the parser is not able to handle blanks on a nested level. Also
     * not with ' or " or "' and so on.
     *
     * @param parameterLine
     * @return
     * @throws ParseException
     */
    public String convertParameterLine(String parameterLine) throws ParseException {
//        if (parameterLine.charAt(0) == '{' && parameterLine.endsWith("}")) {
//            return parameterLine.substring(1, parameterLine.length() - 1);
//        }
        if (parameterLine.length() > 0) {
            if (parameterLine.charAt(0) != '=') {
                throw new ParseException("If any solver options given, then they have to be prefixed by '=''.");
            }
            String parameters = parameterLine.substring(1);
//            if (parameters.charAt(0) != '\'' || !parameters.endsWith("\'")) {
//                throw new ParseException("The parameters of the solver aren't enclosed with \'.");
//            }
// return parameters.substring(1, parameters.length() - 1);
            parameters = parameters.replace(':', ' ');
            parameters = parameters.replace(',', ' ');
            return parameters;
        }
        return parameterLine;
    }

    public String getHelp(String name) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(os);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH - 2, name, null, getOptions(), HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, false);
        pw.flush();
        String help = os.toString();
        String usage = getUsage(name);
        return usage + "\n" + help;
    }

    public void printHelp(String name) {
        Logger.getInstance().addMessage(getHelp(name), false);
    }

//    public void printHelp(String name) {
//        HelpFormatter formatter = new HelpFormatter();
//        PrintWriter pw = new PrintWriter(System.out);
//        formatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH - 2, name, null, getOptions(), HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null, true);
//        pw.flush();
//    }
    public String getUsage(String name) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(os);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(pw, HelpFormatter.DEFAULT_WIDTH + 100, "", getOptions());
        pw.flush();
        // deleting "usage "
        String out = os.toString().substring(7).trim();
        out = out.replaceAll(" (<\\w+>)", ":$1");
        out = out.replace(' ', ',');
        return name + "=" + out + "";
    }
}
