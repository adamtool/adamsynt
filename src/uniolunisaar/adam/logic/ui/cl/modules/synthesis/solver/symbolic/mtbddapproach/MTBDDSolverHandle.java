package uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.symbolic.mtbddapproach;

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.SolverHandle;
import uniolunisaar.adam.data.ui.cl.parameters.synthesis.symbolic.mtbddapproach.MTBDDParameters;
import uniolunisaar.adam.exceptions.ui.cl.CommandLineParseException;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.mtbdd.MTBDDSolver;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.mtbdd.MTBDDSolverFactory;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.mtbdd.MTBDDSolverOptions;

/**
 *
 * @author Manuel Gieseking
 */
public class MTBDDSolverHandle extends SolverHandle<MTBDDSolver<? extends Condition<?>>, MTBDDParameters> {

    public MTBDDSolverHandle(String input, boolean skip, String name, MTBDDParameters parameters, String parameterLine) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, SolvingException {
        super(input, skip, name, parameters, parameterLine);
    }

    @Override
    protected MTBDDSolver<? extends Condition<?>> createSolver(String input, boolean skip) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, CommandLineParseException, SolvingException {
        MTBDDSolverOptions options = new MTBDDSolverOptions(skip);
        parameters.setMTBDDParameters(options, parameterLine);
        return MTBDDSolverFactory.getInstance().getSolver(input, options);
    }

}
