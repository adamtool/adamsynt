package uniolunisaar.adam.ui.cl.serverprotocol.objects;

import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.objects.ProtocolStringAndByteFile;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.objects.ProtocolByteFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import uniolunisaar.adam.ds.ui.cl.synthesis.serverprotocol.AdamProtocolOutputKeys;

/**
 *
 * @author Manuel Gieseking
 */
public class ProtocolOutput implements Serializable {

    public static final long serialVersionUID = 0xdeadbeef00000042l;
    private final Map<AdamProtocolOutputKeys, Object> parameter;

    public ProtocolOutput() {
        parameter = new HashMap<>();
    }

    public void add(AdamProtocolOutputKeys key, String string) {
        parameter.put(key, string);
    }

    public void add(AdamProtocolOutputKeys key, boolean bool) {
        parameter.put(key, bool);
    }

    public void add(AdamProtocolOutputKeys key, ProtocolByteFile file) {
        parameter.put(key, file);
    }

    public void add(AdamProtocolOutputKeys key, ProtocolStringAndByteFile file) {
        parameter.put(key, file);
    }

    public String getString(AdamProtocolOutputKeys key) {
        return (String) parameter.get(key);
    }

    public boolean getBoolean(AdamProtocolOutputKeys key) {
        return (boolean) parameter.get(key);
    }

    public byte[] getFile(AdamProtocolOutputKeys key) {
        return ((ProtocolByteFile) parameter.get(key)).getFile();
    }

    public String getTextOfStringAndByteFile(AdamProtocolOutputKeys key) {
        return ((ProtocolStringAndByteFile) parameter.get(key)).getStr();
    }

    public byte[] getFileOfStringAndByteFile(AdamProtocolOutputKeys key) {
        return ((ProtocolStringAndByteFile) parameter.get(key)).getFile();
    }
}
