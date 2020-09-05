package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.bounded.qbfapproach;

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import uniolunisaar.adam.logic.synthesis.solver.bounded.qbfapproach.QbfSolver;
import uniolunisaar.adam.logic.synthesis.solver.bounded.qbfapproach.QbfSolverFactory;
import uniolunisaar.adam.ds.synthesis.solver.bounded.qbfapproach.QbfSolverOptions;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CalculationInterruptedException;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.ds.ui.cl.parameters.synthesis.bounded.qbfapproach.QBFParameters;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.SolverHandle;

/**
 *
 * @author Manuel Gieseking
 */
public class QBFSolverHandle extends SolverHandle<QbfSolver<? extends Condition<?>>, QBFParameters> {

    public QBFSolverHandle(String input, boolean skip, String name, QBFParameters parameters, String parameterLine) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, SolvingException {
        super(input, skip, name, parameters, parameterLine);
    }

    @Override
    protected QbfSolver<? extends Condition<?>> createSolver(String input, boolean skip) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, CommandLineParseException, SolvingException {
        QbfSolverOptions options = new QbfSolverOptions(skip);
        parameters.setParameters(options, parameterLine);
        return QbfSolverFactory.getInstance().getSolver(input, options);
    }

    @Override
    public void existsWinningStrategy(ProtocolOutput pout) throws CalculationInterruptedException {
        boolean succ = false;
        if (pout == null) {
            succ = solver.existsWinningStrategy();
        } else {
            succ = pout.getBoolean(AdamProtocolOutputKeys.RESULT_TXT);
        }
        //Logger.getInstance().addMessage("A deadlock-avoiding winning strategy for the system players for length "
        //        + solver.getSolvingObject().getN() + " and size "
        //        + solver.getSolvingObject().getB() + " of the bound on the unfolding is existent: " + succ, false);
    }

}
