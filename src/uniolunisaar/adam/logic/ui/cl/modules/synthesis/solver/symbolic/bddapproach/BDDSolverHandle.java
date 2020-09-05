package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.symbolic.bddapproach;

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import uniol.apt.util.Pair;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CalculationInterruptedException;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.ds.graph.synthesis.twoplayergame.symbolic.bddapproach.BDDGraph;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.BDDSolver;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.util.symbolic.bddapproach.BDDTools;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.symbolic.bddapproach.BDDParameters;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolvingObject;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.SolverHandle;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ui.cl.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.util.PGTools;

/**
 *
 * @author Manuel Gieseking
 */
public class BDDSolverHandle extends SolverHandle<BDDSolver<? extends Condition<?>, ? extends BDDSolvingObject<?>, ? extends BDDSolverOptions>, BDDParameters> {

    public BDDSolverHandle(String input, boolean skip, String name, BDDParameters parameters, String parameterLine) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, SolvingException {
        super(input, skip, name, parameters, parameterLine);
    }

    @Override
    protected BDDSolver<? extends Condition<?>, ? extends BDDSolvingObject<?>, ? extends BDDSolverOptions> createSolver(String input, boolean skip) throws SolvingException, ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, CommandLineParseException {
        BDDSolverOptions options = new BDDSolverOptions(skip);
        parameters.setBDDParameters(options, parameterLine);
        options.setNoType2(parameters.hasNoType2(parameterLine));
        return DistrSysBDDSolverFactory.getInstance().getSolver(input, options);
    }

    @Override
    public void createWinningStrategy(ProtocolOutput pout, String output, boolean tikz, boolean dot, boolean nopdf) throws NoStrategyExistentException, IOException, InterruptedException, CalculationInterruptedException {
        boolean gg = parameters.createGraphGame(parameterLine);
        boolean ggs = parameters.createGraphGameStrategy(parameterLine);
        boolean pgs = parameters.createPetriGameStrategy(parameterLine);
        if (!gg && !ggs && !pgs) {
            Logger.getInstance().addMessage("Nothing to do. No job choosen", false);
            return;
        }
        if (gg) { // Create Graph Game
            if (pout == null) { // local version
                BDDGraph graph = solver.getGraphGame();
                if (tikz) {
                    Tools.saveFile(output + "_gg.tex", BDDTools.graph2Tikz(graph, solver));
                }
                saveGraphGame(output + "_gg", graph, solver, dot, nopdf);
            } else { // server version
                if (tikz) {
                    String tikzContent = pout.getTextOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_GG);
                    Tools.saveFile(output + "_graph.tex", tikzContent);
                }
                byte[] pdf = pout.getFileOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_GG);
                Tools.saveFile(output + "_graph.pdf", pdf);
            }
        }

        if (!ggs && !pgs) {
            return; // only graph game was ordered
        }

        if (pout == null) { // local version
            if (ggs && !pgs) { // Create only Graph Game Strategy
                BDDGraph graphStrat = solver.getGraphStrategy();
                if (tikz) {
                    Tools.saveFile(output + "_gg_strat.tex", BDDTools.graph2Tikz(graphStrat, solver));
                }
                saveGraphGame(output + "_gg_strat", graphStrat, solver, dot, nopdf);
            } else {
                Pair<BDDGraph, PetriGameWithTransits> strats = solver.getStrategies();
                if (tikz) {
                    if (ggs) {
                        Tools.saveFile(output + "_gg_strat.tex", BDDTools.graph2Tikz(strats.getFirst(), solver));
                    }
                    Tools.saveFile(output + "_pg_strat.tex", PGTools.pg2Tikz(strats.getSecond()));
                }
                savePetriGame(output, solver.getGame(), solver.getSolvingObject().getMaxTokenCountInt(), dot, nopdf);
                if (ggs) {
                    saveGraphGame(output + "_gg_strat", strats.getFirst(), solver, dot, nopdf);
                }
                savePetriGame(output + "_pg_strat", strats.getSecond(), dot, nopdf);
            }
        } else { // Server version
            if (ggs) {
                if (tikz) {
                    String tikzContent = pout.getTextOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_GGS);
                    Tools.saveFile(output + "_gg_strat.tex", tikzContent);
                }
                byte[] pdf = pout.getFileOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_GGS);
                Tools.saveFile(output + "_gg_strat.pdf", pdf);
            }

            if (pgs) {
                if (tikz) {
                    String tikzContent = pout.getTextOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_PGS);
                    Tools.saveFile(output + "_pg_strat.tex", tikzContent);
                }
                byte[] pdf = pout.getFileOfStringAndByteFile(AdamProtocolOutputKeys.RESULT_PGS);
                Tools.saveFile(output + "_pg_strat.pdf", pdf);
            }
        }
    }

    protected void savePetriGame(String output, PetriGameWithTransits game, Integer maxTokenCount, boolean dot, boolean nopdf) throws IOException, InterruptedException {
        if (dot && !nopdf) {
            PNWTTools.savePnwt2DotAndPDF(output, game, true, maxTokenCount);
            Logger.getInstance().addMessage("Saved to: " + output + ".dot", false);
            Logger.getInstance().addMessage("Saved to: " + output + ".pdf", false);
        } else if (dot) {
            PNWTTools.savePnwt2Dot(output, game, true, maxTokenCount);
            Logger.getInstance().addMessage("Saved to: " + output + ".dot", false);
        } else if (!nopdf) {
            PNWTTools.savePnwt2PDF(output, game, true, maxTokenCount);
            Logger.getInstance().addMessage("Saved to: " + output + ".pdf", false);
        }
    }

    protected <W extends Condition<W>, SO extends BDDSolvingObject<W>, SOP extends BDDSolverOptions, S extends BDDSolver<W, SO, SOP>>
            void saveGraphGame(String output, BDDGraph graph, BDDSolver<W, SO, SOP> solver, boolean dot, boolean nopdf) throws IOException, InterruptedException {
        Logger.getInstance().addMessage("The two-player game over a finite graph contains " + graph.getSize() + " states.");
        if (dot && !nopdf) {
            BDDTools.saveGraph2DotAndPDF(output, graph, solver);
            Logger.getInstance().addMessage("Saved to: " + output + ".dot", false);
            Logger.getInstance().addMessage("Saved to: " + output + ".pdf", false);
        } else if (dot) {
            BDDTools.saveGraph2Dot(output, graph, solver);
            Logger.getInstance().addMessage("Saved to: " + output + ".dot", false);
        } else if (!nopdf) {
            BDDTools.saveGraph2PDF(output, graph, solver);
            Logger.getInstance().addMessage("Saved to: " + output + ".pdf", false);
        }
    }
}
