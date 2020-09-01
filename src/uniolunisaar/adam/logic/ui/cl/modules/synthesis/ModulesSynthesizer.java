package uniolunisaar.adam.logic.ui.cl.modules.synthesis;

import uniolunisaar.adam.logic.ui.cl.modules.AbstractModule;
import uniolunisaar.adam.logic.ui.cl.modules.Modules;
import uniolunisaar.adam.logic.ui.cl.modules.converter.petrinet.Pn2Pdf;
import uniolunisaar.adam.logic.ui.cl.modules.converter.petrinet.Pn2Unfolding;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.benchmarks.Benchmark;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.benchmarks.BenchmarkHL2019;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.benchmarks.BenchmarkSynt2017;
import uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis.Pg2Dot;
import uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis.Pg2Pdf;
import uniolunisaar.adam.logic.ui.cl.modules.converter.synthesis.Pg2Tikz;
import uniolunisaar.adam.logic.ui.cl.modules.exporter.synthesis.ExporterSynth;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.ConcurrentMachinesModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.ContainerTerminalModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.DocumentWorkflowModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.EmergencyBreakdownModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.JopProcessingModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.PhilosophersModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.SecuritySystemModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.SelfReconfiguringRobotsModule;
import uniolunisaar.adam.logic.ui.cl.modules.synthesis.generators.WatchdogModule;
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

}
