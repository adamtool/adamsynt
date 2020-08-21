package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.bounded.qbfconcurrent;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import uniolunisaar.adam.bounded.qbfconcurrent.solver.QbfConSolver;
import uniolunisaar.adam.bounded.qbfconcurrent.solver.QbfConSolverFactory;
import uniolunisaar.adam.bounded.qbfconcurrent.solver.QbfConSolverOptions;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pg.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.pg.NotSupportedGameException;
import uniolunisaar.adam.exceptions.pg.SolvingException;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.pg.CalculationInterruptedException;
import uniolunisaar.adam.exceptions.pg.ParameterMissingException;
import uniolunisaar.adam.ui.cl.serverprotocol.AdamProtocolOutputKeys;
import uniolunisaar.adam.ui.cl.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.bounded.qbfconcurrent.QBFConParameters;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.SolverHandle;

/**
 *
 * @author niklas metzger
 */
public class QBFConSolverHandle extends SolverHandle<QbfConSolver<? extends Condition<?>>, QBFConParameters> {

    public QBFConSolverHandle(String input, boolean skip, String name, QBFConParameters parameters, String parameterLine) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, NotSupportedGameException, ParameterMissingException, SolvingException, NetNotSafeException {
        super(input, skip, name, parameters, parameterLine);
    }

    @Override
    protected QbfConSolver<? extends Condition<?>> createSolver(String input, boolean skip) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, CommandLineParseException, NotSupportedGameException, ParameterMissingException, SolvingException {
        QbfConSolverOptions options = new QbfConSolverOptions(skip);
        parameters.setParameters(options, parameterLine);
        return QbfConSolverFactory.getInstance().getSolver(input, options);
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
