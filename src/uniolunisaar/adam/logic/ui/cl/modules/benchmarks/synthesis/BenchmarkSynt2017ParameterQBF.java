package uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis;

import java.util.HashMap;
import java.util.Map;
import uniol.apt.util.Pair;

/**
 *
 * @author Manuel Gieseking
 */
public class BenchmarkSynt2017ParameterQBF {

    private static BenchmarkSynt2017ParameterQBF instance = null;
    private static Map<String, Pair<Integer, Integer>> parameters = new HashMap<>();

    public static BenchmarkSynt2017ParameterQBF getInstance() {
        if (instance == null) {
            instance = new BenchmarkSynt2017ParameterQBF();
        }
        return instance;
    }

    private BenchmarkSynt2017ParameterQBF() {
        // Security System (SS)
        // erzeugt durch gen_securitySystem
        // also durch SecuritySystem.createSafetyVersion
        parameters.put("2_SS", new Pair<>(7, 2));
        parameters.put("3_SS", new Pair<>(7, 3));
        parameters.put("4_SS", new Pair<>(7, 4));
        parameters.put("5_SS", new Pair<>(7, 5));
       /* parameters.put("6_SS", new Pair<>(5, 1));
        parameters.put("7_SS", new Pair<>(5, 1));
        parameters.put("8_SS", new Pair<>(5, 1));
        parameters.put("9_SS", new Pair<>(5, 1));
        parameters.put("10_SS", new Pair<>(5, 1));
        parameters.put("11_SS", new Pair<>(5, 1));
        parameters.put("12_SS", new Pair<>(5, 1));
        parameters.put("13_SS", new Pair<>(5, 1));
        parameters.put("14_SS", new Pair<>(5, 1));*/
        // workflow 2 (CM)
        // erzeugt durch gen_workflow2
        // also durch Workflow.generateNewAnnotationPoster
        parameters.put("m2_w1_CM", new Pair<>(6, 3));
        parameters.put("m3_w1_CM", new Pair<>(6, 3));
        parameters.put("m3_w2_CM", new Pair<>(6, 4));
        parameters.put("m4_w1_CM", new Pair<>(6, 3));
        parameters.put("m4_w2_CM", new Pair<>(6, 4));
        parameters.put("m4_w3_CM", new Pair<>(6, 5));
        parameters.put("m5_w1_CM", new Pair<>(6, 3));
        parameters.put("m5_w2_CM", new Pair<>(6, 4));
        parameters.put("m5_w3_CM", new Pair<>(6, 5));
        parameters.put("m5_w4_CM", new Pair<>(6, 6));
        parameters.put("m6_w1_CM", new Pair<>(6, 3));
        parameters.put("m6_w2_CM", new Pair<>(6, 4));
        parameters.put("m6_w3_CM", new Pair<>(6, 5));
        parameters.put("m6_w4_CM", new Pair<>(6, 6));
        parameters.put("m6_w5_CM", new Pair<>(6, 7));
        parameters.put("m7_w1_CM", new Pair<>(6, 3));
        parameters.put("m7_w2_CM", new Pair<>(6, 4));
        parameters.put("m8_w1_CM", new Pair<>(6, 3));
        parameters.put("m8_w2_CM", new Pair<>(6, 4));
        parameters.put("m9_w1_CM", new Pair<>(6, 3));
        parameters.put("m9_w2_CM", new Pair<>(6, 4));
        parameters.put("m10_w1_CM", new Pair<>(6, 3));
        parameters.put("m10_w2_CM", new Pair<>(6, 4));
        parameters.put("m11_w1_CM", new Pair<>(6, 3));
        parameters.put("m11_w2_CM", new Pair<>(6, 4));
        parameters.put("m12_w1_CM", new Pair<>(6, 3));
        parameters.put("m12_w2_CM", new Pair<>(6, 4));
        parameters.put("m13_w1_CM", new Pair<>(6, 3));
        parameters.put("m13_w2_CM", new Pair<>(6, 4));
        parameters.put("m14_w1_CM", new Pair<>(6, 3));
        parameters.put("m14_w2_CM", new Pair<>(6, 4));
        parameters.put("m15_w1_CM", new Pair<>(6, 3));
        parameters.put("m15_w2_CM", new Pair<>(6, 4));
        parameters.put("m16_w1_CM", new Pair<>(6, 3));
        parameters.put("m16_w2_CM", new Pair<>(6, 4));
        parameters.put("m17_w1_CM", new Pair<>(6, 3));
        parameters.put("m17_w2_CM", new Pair<>(6, 4));
        parameters.put("m18_w1_CM", new Pair<>(6, 3));
        parameters.put("m18_w2_CM", new Pair<>(6, 4));
        parameters.put("m19_w1_CM", new Pair<>(6, 3));
        parameters.put("m19_w2_CM", new Pair<>(6, 4));
        parameters.put("m20_w1_CM", new Pair<>(6, 3));
        parameters.put("m20_w2_CM", new Pair<>(6, 4));
        parameters.put("m21_w1_CM", new Pair<>(6, 3));
        parameters.put("m22_w1_CM", new Pair<>(6, 3));
        parameters.put("m23_w1_CM", new Pair<>(6, 3));
        parameters.put("m24_w1_CM", new Pair<>(6, 3));
        parameters.put("m25_w1_CM", new Pair<>(6, 3));
        parameters.put("m26_w1_CM", new Pair<>(6, 3));
        parameters.put("m27_w1_CM", new Pair<>(6, 3));
        parameters.put("m28_w1_CM", new Pair<>(6, 3));
        parameters.put("m29_w1_CM", new Pair<>(6, 3));
        parameters.put("m30_w1_CM", new Pair<>(6, 3));
        // Self-organizing robots (SR)
        // erzeugt durch gen_robots
        // also durch SelfOrganizingRobots.generate
        parameters.put("r2_d1_SR", new Pair<>(6, 2));
        parameters.put("r3_d1_SR", new Pair<>(7, 2));
        parameters.put("r3_d2_SR", new Pair<>(8, 3));	// no idea
        parameters.put("r4_d1_SR", new Pair<>(8, 2));   // ''
        parameters.put("r4_d2_SR", new Pair<>(9, 3));   // ''
        parameters.put("r4_d3_SR", new Pair<>(10, 4));  // ''
        parameters.put("r5_d1_SR", new Pair<>(9, 2));
        // Workflow (JP) (job processing)
        // erzeugt durch gen_workflow
        // also durch ManufactorySystem.generate
        parameters.put("2_JP", new Pair<>(7, 3));
        parameters.put("3_JP", new Pair<>(8, 4));
        parameters.put("4_JP", new Pair<>(9, 5));
        parameters.put("5_JP", new Pair<>(10, 6));
        parameters.put("6_JP", new Pair<>(11, 7));
        // Clerks (allyes) (DWs) (Document workflow simple
        // erzeugt durch gen_clerks -s
        // also durch Clerks.generateCP
        parameters.put("1_DWs", new Pair<>(5, 1));
        parameters.put("2_DWs", new Pair<>(7, 1));
        parameters.put("3_DWs", new Pair<>(9, 1));
        parameters.put("4_DWs", new Pair<>(11, 1));
        parameters.put("5_DWs", new Pair<>(13, 1));
        parameters.put("6_DWs", new Pair<>(15, 1));
        parameters.put("7_DWs", new Pair<>(17, 1));
        parameters.put("8_DWs", new Pair<>(19, 1));
        parameters.put("9_DWs", new Pair<>(21, 1));
        parameters.put("10_DWs", new Pair<>(23, 1));
        parameters.put("11_DWs", new Pair<>(25, 1));
        parameters.put("12_DWs", new Pair<>(27, 1));
        parameters.put("13_DWs", new Pair<>(29, 1));
        parameters.put("14_DWs", new Pair<>(31, 1));
        parameters.put("15_DWs", new Pair<>(33, 1));
        parameters.put("16_DWs", new Pair<>(25, 1));
        parameters.put("17_DWs", new Pair<>(37, 1));
        parameters.put("18_DWs", new Pair<>(39, 1));
        parameters.put("19_DWs", new Pair<>(41, 1));
        parameters.put("20_DWs", new Pair<>(43, 1));
        parameters.put("21_DWs", new Pair<>(45, 1));
        parameters.put("22_DWs", new Pair<>(47, 1));
        parameters.put("23_DWs", new Pair<>(49, 1));
        parameters.put("24_DWs", new Pair<>(51, 1));
        // Clerks (all free) (DW) (Document workflow)
        // erzeugt durch gen_clerks 
        // also durch Clerks.generateNonCP
        parameters.put("1_DW", new Pair<>(8, 1));
        parameters.put("2_DW", new Pair<>(10, 1));
        parameters.put("3_DW", new Pair<>(12, 1));
        parameters.put("4_DW", new Pair<>(14, 1));
        parameters.put("5_DW", new Pair<>(16, 1));
        parameters.put("6_DW", new Pair<>(18, 1));
        parameters.put("7_DW", new Pair<>(20, 1));
        parameters.put("8_DW", new Pair<>(22, 1));
        parameters.put("9_DW", new Pair<>(24, 1));
        parameters.put("10_DW", new Pair<>(26, 1));
        parameters.put("11_DW", new Pair<>(28, 1));
        parameters.put("12_DW", new Pair<>(30, 1));
        parameters.put("13_DW", new Pair<>(32, 1));
        parameters.put("14_DW", new Pair<>(34, 1));
        parameters.put("15_DW", new Pair<>(36, 1));
        parameters.put("16_DW", new Pair<>(38, 1));
        parameters.put("17_DW", new Pair<>(40, 1));
        parameters.put("18_DW", new Pair<>(42, 1));
        parameters.put("19_DW", new Pair<>(44, 1));
        parameters.put("20_DW", new Pair<>(46, 1));
        parameters.put("21_DW", new Pair<>(48, 1));
        parameters.put("22_DW", new Pair<>(50, 1));
        parameters.put("23_DW", new Pair<>(52, 1));
        parameters.put("24_DW", new Pair<>(54, 1));
    }

    public Pair<Integer, Integer> getParameters(String key) {
        return parameters.get(key);
    }
}
