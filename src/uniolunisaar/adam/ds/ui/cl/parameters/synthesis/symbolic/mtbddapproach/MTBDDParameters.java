package uniolunisaar.adam.ds.ui.cl.parameters.synthesis.symbolic.mtbddapproach;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.exceptions.pnwt.NetNotSafeException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NoSuitableDistributionFoundException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.ParameterMissingException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.NotSupportedGameException;
import uniolunisaar.adam.exceptions.synthesis.pgwt.SolvingException;
import uniolunisaar.adam.ds.ui.cl.parameters.synthesis.SpecificSolverParameters;
import uniolunisaar.adam.ds.ui.cl.parameters.synthesis.symbolic.SymbolicSolverParameters;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.symbolic.mtbddapproach.MTBDDSolverHandle;
import uniolunisaar.adam.logic.synthesis.solver.symbolic.mtbdd.MTBDDSolverOptions;

/**
 *
 * @author Manuel
 */
public class MTBDDParameters extends SpecificSolverParameters {

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = new HashMap<>();
      
        // For the different game outputs
        options.putAll(SymbolicSolverParameters.createOptions());

        return options;
    }

    @Override
    protected MTBDDSolverHandle createSolverHandle(String input, boolean skip, String name, String parameterLine) throws ParseException, uniol.apt.io.parser.ParseException, IOException, NotSupportedGameException, NetNotSafeException, NoSuitableDistributionFoundException, CouldNotFindSuitableConditionException, ParameterMissingException, SolvingException {
        return new MTBDDSolverHandle(input, skip, name, this, parameterLine);
    }

    // DELEGATES
    public boolean createGraphGame(CommandLine line) {
        return SymbolicSolverParameters.createGraphGame(line);
    }

    public boolean createGraphGameStrategy(CommandLine line) {
        return SymbolicSolverParameters.createGraphGameStrategy(line);
    }

    public boolean createPetriGameStrategy(CommandLine line) {
        return SymbolicSolverParameters.createPetriGameStrategy(line);
    }

    public void setMTBDDParameters(MTBDDSolverOptions options, CommandLine line) throws ParseException {
        options.setGg(SymbolicSolverParameters.createGraphGame(line));
        options.setGgs(SymbolicSolverParameters.createGraphGameStrategy(line));
        options.setGgs(SymbolicSolverParameters.createPetriGameStrategy(line));
    }

}
