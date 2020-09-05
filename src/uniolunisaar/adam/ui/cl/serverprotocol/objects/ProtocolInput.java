package uniolunisaar.adam.ui.cl.serverprotocol.objects;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptPNParser;
import uniolunisaar.adam.ds.synthesis.solver.SolverOptions;
import uniolunisaar.adam.ds.synthesis.ui.cl.serverprotocol.AdamProtocolInputKeys;

/**
 * This class serves for exchanging the parameters of a command with the server.
 *
 * @author Manuel Gieseking
 */
public class ProtocolInput implements Serializable {

    public static final long serialVersionUID = 0xdeadbeef00000008l;
    private final Map<AdamProtocolInputKeys, Object> parameter;

    /**
     * Creates object for handling the parameters of a command sent from the
     * client.
     *
     */
    public ProtocolInput() {
        parameter = new HashMap<>();
    }

    /**
     * Saves a new string parameter of the command sent from the client.
     *
     * @param key - the key of the parameter sent from the client.
     * @param val - the value of the parameter sent from the client.
     */
    public void add(AdamProtocolInputKeys key, String val) {
        parameter.put(key, val);
    }

    /**
     * Saves a new integer parameter of the command sent from the client.
     *
     * @param key - the key of the parameter sent from the client.
     * @param val - the value of the parameter sent from the client.
     */
    public void add(AdamProtocolInputKeys key, int val) {
        parameter.put(key, val);
    }

    /**
     * Saves a new boolean parameter of the command sent from the client.
     *
     * @param key - the key of the parameter sent from the client.
     * @param val - the value of the parameter sent from the client.
     *
     */
    public void add(AdamProtocolInputKeys key, boolean val) {
        parameter.put(key, val);
    }

    public void add(AdamProtocolInputKeys key, SolverOptions val) {
        parameter.put(key, val);
    }

    /**
     * Saves a file parameter of the command sent from the client which was
     * given as a string. I.e. creates a byte array from the given path and
     * saves it with the given key.
     *
     * @param key - the key of the parameter sent from the client.
     * @param path - the path to a file which was set as parameter by the
     * client.
     * @throws FileNotFoundException - thrown when the file at the given 'path'
     * could not be found.
     * @throws IOException - thrown when the opening of the file at the given
     * 'path' caused some trouble.
     */
    public void addFile(AdamProtocolInputKeys key, String path) throws FileNotFoundException, IOException {
        byte[] file = IOUtils.toByteArray(new FileInputStream(path));
        parameter.put(key, file);
    }

    /**
     * Returns the value of the parameter given by the key as a Petri net, where
     * the value must have been saved as a String. So the PetriNet has been
     * saved as a String and is parsed to a PetriNet.
     *
     * @param key - the key of the parameter which value should be returned. -
     * @return - the PetriNet saved as String with the given key.
     * @throws IOException - thrown if the String could not be streamt.
     * @throws ParseException - thrown if the parsing of the String went wrong.
     */
    public PetriNet getPetriNetFromString(AdamProtocolInputKeys key) throws IOException, ParseException {
        String in = getString(key);
        InputStream input = new ByteArrayInputStream(in.getBytes(StandardCharsets.UTF_8));
        return new AptPNParser().parse(input);
    }

    /**
     * Returns the value of the parameter given by the key as a String value.
     * With this key there should really be saved a String value. Otherwise
     * there will be a ClassCast-Exception.
     *
     * @param key - the key of the parameter which value should be returned.
     * @return - the value saved with the given key casted to a string.
     */
    public String getString(AdamProtocolInputKeys key) {
        return (String) parameter.get(key);
    }

    /**
     * Returns the value of the parameter given by the key as a integer value.
     * With this key there should really be saved a int value. Otherwise there
     * will be a ClassCast-Exception.
     *
     * @param key - the key of the parameter which value should be returned.
     * @return - the value saved with the given key casted to an int.
     */
    public int getInteger(AdamProtocolInputKeys key) {
        return (int) parameter.get(key);
    }

    /**
     * Returns the value of the parameter given by the key as a boolean value.
     * With this key there should really be saved a boolean value. Otherwise
     * there will be a ClassCast-Exception.
     *
     * @param key - the key of the parameter which value should be returned.
     * @return - the value saved with the given key casted to a boolean.
     */
    public boolean getBoolean(AdamProtocolInputKeys key) {
        return (boolean) parameter.get(key);
    }

    /**
     * Returns the value of the parameter given by the key as a byte array. With
     * this key there should really be saved a byte array. Otherwise there will
     * be a ClassCast-Exception.
     *
     * @param key - the key of the parameter which value should be returned.
     * @return - the value saved with the given key casted to a byte array.
     */
    public byte[] getFile(AdamProtocolInputKeys key) {
        return (byte[]) parameter.get(key);
    }

    public SolverOptions getSolverOptions(AdamProtocolInputKeys key) {
        return (SolverOptions) parameter.get(key);
    }

    /**
     * Returns true if a parameter with the given key has been stored.
     *
     * @param key - the key of the parameter to look up.
     * @return - true iff a parameter is stored with the given key.
     */
    public boolean hasKey(AdamProtocolInputKeys key) {
        return parameter.containsKey(key);
    }

}
