package uniolunisaar.adam.logic.ui.cl.modules.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolCmds;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.objects.ProtocolError;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.objects.ProtocolInput;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.objects.ProtocolOutput;

/**
 *
 * @author Manuel Gieseking
 */
public class AdamClient {

    public static final String VERSION = "0.3";
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private Socket server;
    private final String ADDRESS = "adam.informatik.uni-oldenburg.de";
    private final int PORT = 10042;

    public void startConnection() throws ClassNotFoundException, UnknownHostException, IOException, Exception {
        server = new Socket(ADDRESS, PORT);

        objOut = new ObjectOutputStream(server.getOutputStream());
        objIn = new ObjectInputStream(server.getInputStream());
        // Receive welcome message as acknowledgement
        Logger.getInstance().addMessage(objIn.readObject().toString(), false);
        // Send version number
        objOut.writeObject(VERSION);
        // Check acceptance
        AdamProtocolCmds acc = (AdamProtocolCmds) objIn.readObject();
        // if it's an error
        handlePossibleServerError(acc);
        // if not...
        Logger.getInstance().addMessage("[SERVER] Connection accepted.");
    }

    public void startCommand(AdamProtocolCmds cmd) throws IOException, ClassNotFoundException, Exception {
        // sends command
        objOut.writeObject(cmd);
        // receives command as acknowledgement
        AdamProtocolCmds res = (AdamProtocolCmds) get();
        // if it's an error
        handlePossibleServerError(res);
    }

    public void sendParameters(ProtocolInput paras) throws IOException {
        objOut.writeObject(paras);
    }

    /**
     * When leaving this method, we just read the result command.
     *
     * @return 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ProtocolOutput handleServerOutput() throws IOException, ClassNotFoundException, Exception {
        AdamProtocolCmds cmd = (AdamProtocolCmds) objIn.readObject();
        handlePossibleServerError(cmd);
        while (!cmd.equals(AdamProtocolCmds.RESULT)) { // As long it is not the result, expect messages and print them
            if (cmd.equals(AdamProtocolCmds.TEXT)) {
                boolean verbose = objIn.readBoolean();
                Logger.getInstance().addMessage(objIn.readObject().toString(), verbose);
            }
            cmd = (AdamProtocolCmds) objIn.readObject();
        }
        return (ProtocolOutput) get();
    }

    private void handlePossibleServerError(AdamProtocolCmds cmd) throws IOException, ClassNotFoundException, Exception {
        if (cmd.equals(AdamProtocolCmds.ERROR)) {
            ProtocolError errObj = (ProtocolError) objIn.readObject();
            if (errObj.getMsg() != null) {
                Logger.getInstance().addMessage(errObj.getMsg());
            }
            throw errObj.getEx();
        }
    }

    public void closeServer() throws IOException {
        objOut.writeObject(AdamProtocolCmds.CLOSE);
        server.close();
    }

    public void send(Object obj) throws IOException {
        objOut.writeObject(obj);
    }

    public Object get() throws IOException, ClassNotFoundException {
        Object obj = objIn.readObject();
        return obj;
    }
}
