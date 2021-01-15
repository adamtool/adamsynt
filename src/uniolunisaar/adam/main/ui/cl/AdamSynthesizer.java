package uniolunisaar.adam.main.ui.cl;

import uniolunisaar.adam.logic.ui.cl.AdamUI;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.ModulesSynthesizer;

/**
 *
 * @author Manuel Gieseking
 */
public class AdamSynthesizer {

    public static boolean debug = false;

    public static void main(String[] args) {
//        int exitCode = AdamUI.main(args, new ModulesSynthesizer(), debug);
//        System.exit(exitCode);
        AdamUI.main(args, new ModulesSynthesizer(), debug);
    }
    
    
    
    
}
