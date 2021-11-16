package uniolunisaar.adam.logic.ui.cl.modules.synthesis;

import uniolunisaar.adam.logic.ui.cl.modules.AbstractModule;
import uniolunisaar.adam.logic.ui.cl.modules.Modules;
import uniolunisaar.adam.logic.ui.cl.modules.converter.petrinet.Pn2Pdf;
import uniolunisaar.adam.logic.ui.cl.modules.converter.petrinet.Pn2Unfolding;
import uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis.Benchmark;
import uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis.BenchmarkCanonical2021;
import uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis.BenchmarkHL2019;
import uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis.BenchmarkSynt2017;
import uniolunisaar.adam.logic.ui.cl.modules.benchmarks.synthesis.BenchmarkSynthesis2021;
import uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis.pgwt.Pg2Dot;
import uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis.pgwt.Pg2Pdf;
import uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis.pgwt.Pg2Tikz;
import uniolunisaar.adam.logic.ui.cl.modules.exporter.synthesis.ExporterSynth;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.ConcurrentMachinesModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.ContainerTerminalModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.DocumentWorkflowModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.EmergencyBreakdownModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.JopProcessingModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.PhilosophersModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.SecuritySystemModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.SelfReconfiguringRobotsModule;
import uniolunisaar.adam.logic.ui.cl.modules.generators.synthesis.WatchdogModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.ExWinStrat;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.solver.WinStrat;

/**
 *
 * @author Manuel Gieseking
 */
public class ModulesSynthesizer extends Modules {

    private static final AbstractModule[] modules = {
        // Converter
        new Pn2Pdf(),
        new Pn2Unfolding(),
        new Pg2Dot(),
        new Pg2Pdf(),
        new Pg2Tikz(),
        // Solver
        new ExWinStrat(),
        new WinStrat(),
        // Benchmark
        new Benchmark(),
        new BenchmarkSynt2017(),
        new BenchmarkHL2019(),
        new BenchmarkCanonical2021(),
        new BenchmarkSynthesis2021(),
        // Exporter
        new ExporterSynth(),
        // Generators Petri Games
        new PhilosophersModule(),
        new DocumentWorkflowModule(),
        new JopProcessingModule(),
        new SelfReconfiguringRobotsModule(),
        new ConcurrentMachinesModule(),
        new WatchdogModule(),
        new SecuritySystemModule(),
        new ContainerTerminalModule(),
        new EmergencyBreakdownModule()
    };

    @Override
    public AbstractModule[] getModules() {
        return modules;
    }

    @Override
    public String getToolName() {
        return "adamSYNT";
    }

}
