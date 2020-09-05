package uniolunisaar.adam.logic.ui.cl.modules.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import uniolunisaar.adam.ds.synthesis.solver.SolverOptions;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolInputKeys;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.objects.ProtocolInput;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.objects.ProtocolOutput;
import uniolunisaar.adam.logic.ui.cl.modules.AbstractSimpleModule;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class AbstractServerModule extends AbstractSimpleModule {

    private final String PARAMETER_USE_SERVER = "ext";
    private ProtocolInput parameters;
    private boolean active;
    private AdamClient server;

    @Override
    public Map<String, Option> createOptions() {
        Map<String, Option> options = super.createOptions();

        OptionBuilder.withDescription("If this flag is set, the calculation is done on an external server.");
        OptionBuilder.withLongOpt("server");
        options.put(PARAMETER_USE_SERVER, OptionBuilder.create(PARAMETER_USE_SERVER));
        return options;
    }

    @Override
    public void execute(CommandLine line) throws Exception {
        super.execute(line);
        parameters = new ProtocolInput();
        parameters.add(AdamProtocolInputKeys.VERBOSE, line.hasOption(AbstractSimpleModule.PARAMETER_VERBOSE));
        this.active = line.hasOption(PARAMETER_USE_SERVER);
    }

    protected ProtocolOutput getServerOutput(AdamProtocolCmds cmd) throws UnknownHostException, Exception {
        server = new AdamClient();
        server.startConnection();
        server.startCommand(cmd);
        server.sendParameters(getServerParameters());
        return server.handleServerOutput();
    }

    protected void closeServer() throws IOException {
        server.closeServer();
    }

    public void addServerParameter(AdamProtocolInputKeys key, String val) {
        parameters.add(key, val);
    }

    public void addServerParameter(AdamProtocolInputKeys key, SolverOptions opts) {
        parameters.add(key, opts);
    }

    public void addServerParameter(AdamProtocolInputKeys key, int val) {
        parameters.add(key, val);
    }

    public void addServerParameter(AdamProtocolInputKeys key, boolean val) {
        parameters.add(key, val);
    }

    public boolean isServerActive() {
        return active;
    }

    public ProtocolInput getServerParameters() {
        return parameters;
    }
}
