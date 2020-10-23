Synthesis of Distributed System with Petri Games with Transits
==============================================================
A framework for the synthesis of distributed systems modeled with Petri games with transits.
This module is the model and controller part of [AdamSYNT](https://github.com/adamtool/adamsynt).

Contains:
---------
- data structures for
  * Petri games (cf. [Inf. Comp. 17](https://doi.org/10.1016/j.ic.2016.07.006))
  * the corresponding two-player game (cf. [Inf. Comp. 17](https://doi.org/10.1016/j.ic.2016.07.006))
  * Petri games with transits
- a general structure for easily integrating solvers for Petri games with transits
- heuristics to partition the places of a Petri net in disjunctive sets regarding their occurrence in reachable markings
- algorithms for solving Petri games with one environment and an arbitrary number of system players with a local safety objective by using BDDs
  (cf. [CAV'15](https://doi.org/10.1007/978-3-319-21690-4_25), [SYNT@CAV'17](https://doi.org/10.4204/EPTCS.260.5))
- renderer for Petri games with transits and the two-player game into the dot format
- parser for Petri nets games with transits
- generators for examples of Petri games with transits

Integration:
------------
This modules can be used as separate library and
- is integrated in: [adam](https://github.com/adamtool/adam)
- contains the packages: petrigames, bddapproach, mtbddapproach,
- depends on the repos: [libs](https://github.com/adamtool/libs), [framework](https://github.com/adamtool/framework).

Related Publications:
---------------------
The theoretical background for Petri games and the decision procedure for one environment and a bounded number of system players:
- Bernd Finkbeiner, Ernst-Rüdiger Olderog:
  [Petri games: Synthesis of distributed systems with causal memory](https://doi.org/10.1016/j.ic.2016.07.006). Inf. Comput. 253: 181-203 (2017)

The practical parts and the BDD approach for the algorithms to solve Petri games with one environment and a bounded number of system players:
- Bernd Finkbeiner, Manuel Gieseking, Ernst-Rüdiger Olderog:
  [Adam: Causality-Based Synthesis of Distributed Systems](https://doi.org/10.1007/978-3-319-21690-4_25). CAV (1) 2015: 433-439
- Bernd Finkbeiner, Manuel Gieseking, Jesko Hecking-Harbusch, Ernst-Rüdiger Olderog:
  [Symbolic vs. Bounded Synthesis for Petri Games](https://doi.org/10.4204/EPTCS.260.5). SYNT@CAV 2017: 23-43

------------------------------------

How To Build
------------
A __Makefile__ is located in the main folder.
First, pull a local copy of the dependencies with
```
make pull_dependencies
```
then build the whole framework with all the dependencies with
```
make
```
To build a single dependencies separately, use, e.g,
```
make tools
```
To delete the build files and clean-up
```
make clean
```
To also delete the files generated by the test and all temporary files use
```
make clean-all
```
Some of the algorithms depend on external libraries or tools. To locate them properly create a file in the main folder
```
touch ADAM.properties
```
and add the absolute paths of the necessary libraries or tools:
```
libraryFolder=<path2Repo>/dependencies/libs/
dot=dot
time=/usr/bin/time
buddy=
cudd=
cal=
```
You may leave some of the properties open if you don't use the corresponding libraries/tools.

Tests
-----
Both modules contain tests. You can run the tests by entering the corresponding folder, e.g.,
```
cd petriGames
```
and run all tests for the module by just typing
```
ant test
```
For testing a specific class in the package _bddapproach_ (in ./symbolicalgorithms/bddapproach/
) use for example
```
ant test-class -Dclass.name=uniolunisaar.adam.tests.synthesis.symbolic.bddapproach.distrsys.graph.TestStepwiseGraphBuilder
```
and for testing a specific method use for example
```
ant test-method -Dclass.name=uniolunisaar.adam.tests.synthesis.symbolic.bddapproach.distrsys.safety.TestingSomeFiles -Dmethod.name=testBurglar
```
