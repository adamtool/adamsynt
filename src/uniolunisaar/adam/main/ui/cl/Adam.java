package uniolunisaar.adam.main.ui.cl;

import java.io.FileNotFoundException;
import java.io.IOException;

import uniol.apt.adt.exception.StructureException;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.analysis.coverability.CoverabilityGraph;
import uniol.apt.analysis.exception.UnboundedException;
import uniol.apt.io.parser.ParseException;
import uniol.apt.module.exception.ModuleException;
import uniol.apt.util.Pair;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NetNotConcurrencyPreservingException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoStrategyExistentException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolverDontFitPetriGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.synthesis.pgwt.PetriGameWithTransits;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CalculationInterruptedException;
import uniolunisaar.adam.generators.pgwt.Clerks;
import uniolunisaar.adam.generators.pgwt.ManufactorySystem;
import uniolunisaar.adam.generators.pgwt.Philosopher;
import uniolunisaar.adam.generators.pgwt.SelfOrganizingRobots;
import uniolunisaar.adam.generators.pgwt.Workflow;
import uniolunisaar.adam.logic.synthesis.pgwt.calculators.CalculatorIDs;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.util.benchmarks.synthesis.Benchmarks;
import uniolunisaar.adam.ds.graph.synthesis.twoplayergame.symbolic.bddapproach.BDDGraph;
import uniolunisaar.adam.ds.synthesis.solver.symbolic.bddapproach.BDDSolverOptions;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.bddapproach.distrsys.DistrSysBDDSolverFactory;
import uniolunisaar.adam.util.symbolic.bddapproach.BDDTools;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;

/**
 * deprecated
 *
 * @author thewn
 */
@Deprecated
public class Adam {

    private static final String pg2dot = "pg2dot";
    private static final String pg2dot_descr = "Converts a Petri game in"
            + " APT format to a dot file.";
    private static final String pg2pdf = "pg2pdf";
    private static final String pg2pdf_descr = "Converts a Petri game in"
            + " APT format to a pdf file by using Graphviz (dot has to be executable).";
    private static final String exWinStrat = "ex_win_strat";
    private static final String exWinStrat_descr = "Returns true if there is a"
            + " deadlock-avoiding winning strategy (system players) for the in APT format given Petri game.";
    private static final String winStratGraph = "win_strat_graph";
    private static final String winStratGraph_descr = "Creates a deadlock-avoiding winning "
            + "strategy (system players) in the finite graph game of the in APT format given Petri game if existent."
            + " Saves the strategy in the given folder as dot, and if dot is executable, as pdf.";
    private static final String winStratPetriGame = "win_strat_pg";
    private static final String winStratPetriGame_descr = "Creates a deadlock-avoiding winning "
            + " strategy (system players) for the in APT format given Petri game if existent."
            + " Saves the strategy in the given folder as dot, and if dot is executable, as pdf.";
    private static final String winStrat = "win_strat";
    private static final String winStrat_descr = "Creates a deadlock-avoiding winning "
            + " strategy (system players) in the finite graph game and the Petri game for the in APT format given Petri game if existent."
            + " Saves the strategies in the given folder as dot, and if dot is executable, as pdf. Adding '_gg' for the graph and '_pg' for the Petri game strategy to the filename, respectively.";
    private static final String bench = "bench";
    private static final String bench_descr = "Just for benchmarks. Does not check any preconditions of the Petri game."
            + " Tests existence of strategy, calculates graph and Petri game strategy, saves Petri game strategy as dot without rendering it to a pdf file.";
    private static final String benchCover = "bench_cover";
    private static final String benchCover_descr = "Just for comparing benchmarks with the calculation of a coverability graph.";
    private static final String genPhil = "gen_phil";
    private static final String genPhil_descr = "Generates the philosopher problem for"
            + " the given number of philosophers and saves the resulting net in the APT and dot format and, if dot is executable, as pdf.";
    private static final String genClerks = "gen_clerks";
    private static final String genClerks_descr = "Generates"
            + " the given number of clerks signing a document and saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Document Workflow examples of the ADAM paper.";
    private static final String genRobots = "gen_robots";
    private static final String genRobots_descr = "Generates"
            + " the self-organizing robots examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Self-reconfiguring Robots example of the ADAM paper.";
    private static final String genWorkflow = "gen_workflow";
    private static final String genWorkFlow_descr = "Generates"
            + " the workflow examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Job Processing example of the ADAM paper.";
    private static final String genWorkflow2 = "gen_workflow2";
    private static final String genWorkFlow2_descr = "Generates"
            + " the workflow2 examples. Saves the resulting net in APT and dot format and, if dot is executable, as pdf."
            + " This module generates the Concurrent Machines example of the ADAM paper.";
    private static final String boundedSynthesis = "bounded_synthesis";
    private static final String boundedSynthesis_descr = "Performs"
            + " the bounded synthesis (QBF) approach to find a winning strategy for a given safe Petri game.";

    private static void printPossibleModules() {
        System.out.println("Possible modules:");
        System.out.println(pg2dot + " - " + pg2dot_descr);
        System.out.println(pg2pdf + " - " + pg2pdf_descr);
        System.out.println(exWinStrat + " - " + exWinStrat_descr);
        System.out.println(winStratGraph + " - " + winStratGraph_descr);
        System.out.println(winStratPetriGame + " - " + winStratPetriGame_descr);
        System.out.println(winStrat + " - " + winStrat_descr);
        System.out.println(genPhil + " - " + genPhil_descr);
        System.out.println(genClerks + " - " + genClerks_descr);
        System.out.println(genRobots + " - " + genRobots_descr);
        System.out.println(genWorkflow + " - " + genWorkFlow_descr);
        System.out.println(genWorkflow2 + " - " + genWorkFlow2_descr);
        System.out.println(bench + " - " + bench_descr);
        System.out.println(benchCover + " - " + benchCover_descr);
        System.out.println(boundedSynthesis + " - " + boundedSynthesis_descr);
    }

    public static void main(String[] args) throws NoSuitableDistributionFoundException, NotSupportedGameException, NetNotSafeException, ParameterMissingException, CalculationInterruptedException {
        try {
            if (args.length == 0) {
                printPossibleModules();
                return;
            }
            switch (args[0]) {
                case pg2dot:
                    pg2dot(args);
                    break;
                case pg2pdf:
                    pg2pdf(args);
                    break;
                case exWinStrat:
                    existsWinningStrategy(args);
                    break;
                case winStratGraph:
                    winningStrategyGraphGame(args);
                    break;
                case winStratPetriGame:
                    winningStrategyPetriGame(args, true);
                    break;
                case bench:
                    bench(args);
                    break;
                case benchCover:
                    bench_cover(args);
                    break;
                case winStrat:
                    winningStrategy(args);
                    break;
                case genPhil:
                    generatePhilosophers(args);
                    break;
                case genClerks:
                    generateClerks(args);
                    break;
                case genRobots:
                    generateRobots(args);
                    break;
                case genWorkflow:
                    generateWorkflow(args);
                    break;
                case genWorkflow2:
                    generateWorkflow2(args);
                    break;
                case boundedSynthesis:
                    boundSyn(args);
                    break;
                default:
                    System.out.println(args[0] + " is not a suitable module.");
                    printPossibleModules();
                    break;
            }
        } catch (IOException | InterruptedException | ParseException | SolvingException
                | NoStrategyExistentException | NetNotConcurrencyPreservingException
                | ModuleException | CouldNotFindSuitableConditionException | SolverDontFitPetriGameException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void pg2dot(String[] args) throws IOException, ParseException, CouldNotFindSuitableConditionException, NotSupportedGameException {
        if (args.length != 3) {
            System.out.println("Usage: " + pg2dot + " <path2PetriGame/filename.apt> <path2DesiredOutputFolder/filename>");
            return;
        }
        PNWTTools.savePnwt2Dot(args[1], args[2], false);
    }

    private static void pg2pdf(String[] args) throws ParseException, IOException, InterruptedException, NotSupportedGameException {
        if (args.length != 3) {
            System.out.println("Usage: " + pg2pdf + " <path2PetriGame/filename.apt> <path2DesiredOutputFolder/filename>");
            return;
        }
        PNWTTools.savePnwt2DotAndPDF(args[1], args[2], false);
    }

    private static void existsWinningStrategy(String[] args) throws IOException, ParseException, NetNotSafeException, NetNotConcurrencyPreservingException, NoSuitableDistributionFoundException, NotSupportedGameException, CouldNotFindSuitableConditionException, SolverDontFitPetriGameException, ParameterMissingException, SolvingException, CalculationInterruptedException {
        if (args.length != 2) {
            System.out.println("Usage: " + exWinStrat + " <path2PetriGame/filename.apt>");
            return;
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().start(Benchmarks.Parts.OVERALL);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        var sol = DistrSysBDDSolverFactory.getInstance().getSolver(args[1]);
        boolean succ = sol.existsWinningStrategy();
        System.out.println("A deadlock-avoiding winning strategy for the system players is existent: " + succ);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.OVERALL);
        Logger.getInstance().addMessage(Benchmarks.getInstance().toString());
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
    }

    private static void winningStrategyGraphGame(String[] args) throws IOException, ParseException, NetNotSafeException, NetNotConcurrencyPreservingException, NoStrategyExistentException, InterruptedException, NoSuitableDistributionFoundException, NotSupportedGameException, CouldNotFindSuitableConditionException, SolverDontFitPetriGameException, ParameterMissingException, SolvingException, CalculationInterruptedException {
        if (args.length != 3) {
            System.out.println("Usage: " + winStratGraph + " <path2PetriGame/filename.apt> <path2DesiredOutputFolder/filename>");
            return;
        }
        var sol = DistrSysBDDSolverFactory.getInstance().getSolver(args[1]);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().start(Benchmarks.Parts.OVERALL);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        BDDGraph g = sol.getGraphStrategy();
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.OVERALL);
        Benchmarks.getInstance().start(Benchmarks.Parts.DOT_SAVING);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        BDDTools.saveGraph2DotAndPDF(args[2], g, sol);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.DOT_SAVING);
        Logger.getInstance().addMessage(Benchmarks.getInstance().toString());
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
    }

    private static void winningStrategyPetriGame(String[] args, boolean print) throws IOException, ParseException, NetNotSafeException, NetNotConcurrencyPreservingException, NoStrategyExistentException, InterruptedException, NoSuitableDistributionFoundException, NotSupportedGameException, CouldNotFindSuitableConditionException, SolverDontFitPetriGameException, ParameterMissingException, SolvingException, CalculationInterruptedException {
        if (!(args.length == 3 || args.length == 4)) {
            System.out.println("Usage: " + winStratPetriGame + " [optionalLoggerOutputFile] <path2PetriGame/filename.apt> <path2DesiredOutputFolder/filename>");
            return;
        }
        if (args.length == 4) {
            Logger.getInstance().setPath(args[1]);
            Logger.getInstance().setOutput(Logger.OUTPUT.FILE);
        }
        int offset = (args.length == 4) ? 1 : 0;
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().start(Benchmarks.Parts.OVERALL);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        BDDSolverOptions opts = new BDDSolverOptions(!(print), true);
        var sol = DistrSysBDDSolverFactory.getInstance().getSolver(args[1 + offset], opts);
        PetriGameWithTransits pn = sol.getStrategy();
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.OVERALL);
        if (print) {
            Benchmarks.getInstance().start(Benchmarks.Parts.DOT_SAVING);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
            PNWTTools.savePnwt2DotAndPDF(args[2 + offset], pn, true);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
            Benchmarks.getInstance().stop(Benchmarks.Parts.DOT_SAVING);
            Logger.getInstance().addMessage(Benchmarks.getInstance().toString());
        } else {
            Benchmarks.getInstance().start(Benchmarks.Parts.DOT_SAVING);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
            PNWTTools.savePnwt2Dot(args[2 + offset], pn, true);
            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
            Benchmarks.getInstance().stop(Benchmarks.Parts.DOT_SAVING);
            // todo: just directly added those data here
            boolean truthValue = sol.getGame().getValue(CalculatorIDs.CONCURRENCY_PRESERVING.name()); // eclipse compiler does not work with generics and ternary operator
            String sizes = "," + sol.getGame().getPlaces().size() + "," + sol.getGame().getTransitions().size()
                    + "," + pn.getPlaces().size() + "," + pn.getTransitions().size() + ","
                    + sol.getGame().getValue(CalculatorIDs.MAX_TOKEN_COUNT.name()) + ", " + (truthValue ? 1 : 0) + ", " + sol.getVariableNumber();
            Logger.getInstance().addMessage(Benchmarks.getInstance().toCSVString() + sizes);
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
    }

    private static void bench(String[] args) throws IOException, ParseException, NetNotSafeException, NetNotConcurrencyPreservingException, NoStrategyExistentException, InterruptedException, NoSuitableDistributionFoundException, NotSupportedGameException, CouldNotFindSuitableConditionException, SolverDontFitPetriGameException, ParameterMissingException, SolvingException, CalculationInterruptedException {
        winningStrategyPetriGame(args, false);
    }

    private static void bench_cover(String[] args) throws IOException, ParseException, CouldNotFindSuitableConditionException {
        if (args.length != 2) {
            System.out.println("Usage: " + benchCover + " <path2PetriGame/filename.apt>");
            return;
        }
        PetriNet net = Tools.getPetriNet(args[1]);
        CoverabilityGraph cover = CoverabilityGraph.get(net);
        int size = cover.toCoverabilityLTS().getNodes().size();
        System.err.println(size);
    }

    private static void winningStrategy(String[] args) throws IOException, ParseException, NetNotSafeException, NetNotConcurrencyPreservingException, NoStrategyExistentException, InterruptedException, NoSuitableDistributionFoundException, NotSupportedGameException, CouldNotFindSuitableConditionException, SolverDontFitPetriGameException, ParameterMissingException, SolvingException, CalculationInterruptedException {
        if (args.length != 3) {
            System.out.println("Usage: " + winStrat + " <path2PetriGame/filename.apt> <path2DesiredOutputFolder/filename>");
            return;
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().start(Benchmarks.Parts.OVERALL);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        var sol = DistrSysBDDSolverFactory.getInstance().getSolver(args[1]);
        Pair<BDDGraph, PetriGameWithTransits> strats = sol.getStrategies();
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.OVERALL);
        Benchmarks.getInstance().start(Benchmarks.Parts.DOT_SAVING);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        PNWTTools.savePnwt2DotAndPDF(args[2] + "_debug", sol.getGame(), true, sol.getSolvingObject().getMaxTokenCountInt());
        BDDTools.saveGraph2DotAndPDF(args[2] + "_gg", strats.getFirst(), sol);
        PNWTTools.savePnwt2DotAndPDF(args[2] + "_pg", strats.getSecond(), true);
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
        Benchmarks.getInstance().stop(Benchmarks.Parts.DOT_SAVING);
        Logger.getInstance().addMessage(Benchmarks.getInstance().toString());
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TODO : FOR BENCHMARKS
    }

    private static void generatePhilosophers(String[] args) throws IOException, InterruptedException, FileNotFoundException, ModuleException {
        if (args.length != 4) {
            System.out.println("Usage: " + genPhil + " <g/ng> <numberOfPhilosophers> <path2DesiredOutputFolder/filename>");
            System.out.println("g - Use the guided variant, where the environment orders every philosopher to eat after another.");
            System.out.println("ng - Use the variant, where the environment is just one of the philosophers.");
            return;
        }
        PetriGameWithTransits net = (args[1].equals("ng")) ? Philosopher.generateIndividual(Integer.parseInt(args[2]), true, true)
                : Philosopher.generateGuided2(Integer.parseInt(args[2]), true, true);
        Tools.savePN(args[3], net);
        PNWTTools.savePnwt2DotAndPDF(args[3], net, true);
    }

    private static void generateClerks(String[] args) throws IOException, InterruptedException, FileNotFoundException, ModuleException {
        if (args.length != 4) {
            System.out.println("Usage: " + genClerks + " <yn/y> <numberOfClerks> <path2DesiredOutputFolder/filename>");
            System.out.println("yn - Use the standard version");
            System.out.println("y - Use the liberal version (every clerk has to vote for yes)");
            return;
        }
        PetriGameWithTransits net = (args[1].equals("y")) ? Clerks.generateCP(Integer.parseInt(args[2]), true, true)
                : Clerks.generateNonCP(Integer.parseInt(args[2]), true, true);
        Tools.savePN(args[3], net);
        PNWTTools.savePnwt2DotAndPDF(args[3], net, true);
    }

    private static void generateRobots(String[] args) throws IOException, InterruptedException, FileNotFoundException, ModuleException {
        if (args.length != 4) {
            System.out.println("Usage: " + genRobots + " <numberOfRobotsAndTools> <numberOfDestroyPhases> <path2DesiredOutputFolder/filename>");
            return;
        }
        PetriGameWithTransits net = SelfOrganizingRobots.generate(Integer.parseInt(args[1]), Integer.parseInt(args[2]), true, true);
        Tools.savePN(args[3], net);
        PNWTTools.savePnwt2DotAndPDF(args[3], net, true);
    }

    private static void generateWorkflow(String[] args) throws IOException, InterruptedException, FileNotFoundException, ModuleException {
        if (args.length != 3) {
            System.out.println("Usage: " + genWorkflow + " <numberOfMachines> <path2DesiredOutputFolder/filename>");
            return;
        }
        PetriGameWithTransits net = ManufactorySystem.generate(Integer.parseInt(args[1]), true, true);
        Tools.savePN(args[2], net);
        PNWTTools.savePnwt2DotAndPDF(args[2], net, true);
    }

    private static void generateWorkflow2(String[] args) throws IOException, InterruptedException, FileNotFoundException, ModuleException {
        if (args.length != 4) {
            System.out.println("Usage: " + genWorkflow2 + " <numberOfMachines> <numberOfWorkpieces> <path2DesiredOutputFolder/filename>");
            return;
        }
        PetriGameWithTransits net = Workflow.generate(Integer.parseInt(args[1]), Integer.parseInt(args[2]), true, true);
        Tools.savePN(args[3], net);
        PNWTTools.savePnwt2DotAndPDF(args[3], net, true);
    }

    private static void boundSyn(String[] args) throws StructureException, IOException, ParseException, UnboundedException, CouldNotFindSuitableConditionException {
        if (args.length != 4) {
            System.out.println("Usage: " + boundedSynthesis + " <lengthOfSimulation n> <numberOfUnfoldings b> <path2DesiredInputFolder/filename>");
            return;
        }
        PetriNet pn = Tools.getPetriNet(args[3]);
//        PetriGame pg = new PetriGameQBF(pn, args[1], args[2]);
    }
}
