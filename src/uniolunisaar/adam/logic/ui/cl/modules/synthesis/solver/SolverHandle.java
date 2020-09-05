package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;

import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.logic.synthesis.solver.Solver;
import uniolunisaar.adam.ds.synthesis.solver.SolverOptions;
import uniolunisaar.adam.ds.synthesis.solver.SolvingObject;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CalculationInterruptedException;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.SpecificSolverParameters;
import uniolunisaar.adam.ds.synthesis.pgwt.IPetriGame;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 * @param <SSP>
 */
public abstract class SolverHandle<S extends Solver<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ? extends SolvingObject<? extends IPetriGame, ? extends Condition<?>, ?>>, ? extends SolverOptions>, SSP extends SpecificSolverParameters> {

    protected final String name;
    protected final CommandLine parameterLine;
    protected final S solver;
    protected final SSP parameters;

    public SolverHandle(String input, boolean skip, String name, SSP parameters, String parameterLine) throws SolvingException, ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, uniol.apt.io.parser.ParseException, ParameterMissingException {
        this.parameters = parameters;
        this.name = name;
        parameterLine = parameters.convertParameterLine(parameterLine);
        // create the command line parser
        CommandLineParser parser = new BasicParser();
        try {
            this.parameterLine = parser.parse(parameters.getOptions(), parameterLine.split("\\s+"));
            this.solver = createSolver(input, skip);
        } catch (ParseException exp) {
            String msg = "Invalid use of solver '" + name + "': " + exp.getMessage();
            Logger.getInstance().addMessage(msg, false);
            parameters.printHelp(name);
            throw new ParseException("alreadyHandled");
        }
    }

    protected abstract S createSolver(String input, boolean skip) throws SolvingException, ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException;

    public void existsWinningStrategy(ProtocolOutput pout) throws CalculationInterruptedException {
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
//        Benchmarks.getInstance().start(Benchmarks.Parts.OVERALL);
        boolean succ;
        if (pout == null) {
            succ = solver.existsWinningStrategy();
        } else {
            succ = pout.getBoolean(AdamProtocolOutputKeys.RESULT_TXT);
        }
        Logger.getInstance().addMessage("A deadlock-avoiding winning strategy for the system players is existent: " + succ, false, true);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
//        Benchmarks.getInstance().addData(solver, null);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
    }

    public void createWinningStrategy(ProtocolOutput pout, String output, boolean tikz, boolean dot, boolean nopdf) throws NoStrategyExistentException, IOException, InterruptedException, CalculationInterruptedException {
        if (pout == null) {
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
//            Benchmarks.getInstance().stop(Benchmarks.Parts.OVERALL);
            PetriGameWithTransits pn = solver.getStrategy();
//            Benchmarks.getInstance().start(Benchmarks.Parts.DOT_SAVING);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS                   
            if (tikz) {
                Tools.saveFile(output + "_strat.tex", PGTools.pg2Tikz(pn));
            }
            savePetriGame(output + "_strat", pn, dot, nopdf);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
//            Benchmarks.getInstance().stop(Benchmarks.Parts.DOT_SAVING);
//            Benchmarks.getInstance().addData(solver, pn);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        } else {
            if (tikz) {
                String tikzContent = pout.getTextOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_PGS);
                Tools.saveFile(output + "_strat.tex", tikzContent);
            }
            byte[] pdf = pout.getFileOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_PGS);
            Tools.saveFile(output + "._strat.pdf", pdf);
        }
    }

    public S getSolver() {
        return solver;
    }

    protected void savePetriGame(String output, PetriGameWithTransits game, boolean dot, boolean nopdf) throws IOException, InterruptedException {
        if (dot && !nopdf) {
            PGTools.savePG2DotAndPDF(output, game, false);
            Logger.getInstance().addMessage("Saved to: " + output + ".dot", false);
            Logger.getInstance().addMessage("Saved to: " + output + ".pdf", false);
        } else if (dot) {
            PGTools.savePG2Dot(output, game, false);
            Logger.getInstance().addMessage("Saved to: " + output + ".dot", false);
        } else if (!nopdf) {
            PGTools.savePG2PDF(output, game, false);
            Logger.getInstance().addMessage("Saved to: " + output + ".pdf", false);
        }
    }
}
