## @author Manuel Gieseking

# dependencies (folders and repos should be equally ordered)
DEPENDENCIES_FOLDERS="libs,framework,synthesizer,boundedSynthesis,high-level,server-command-line-protocol,ui"
DEPENDENCIES_REPOS="https://github.com/adamtool/libs.git,https://github.com/adamtool/framework.git,https://github.com/adamtool/synthesizer.git,https://github.com/adamtool/boundedSynthesis.git,https://github.com/adamtool/high-level.git,https://github.com/adamtool/server-command-line-protocol.git,https://github.com/adamtool/ui.git"
DEPENDENCIES_REV="HEAD,HEAD,HEAD,HEAD,HEAD,HEAD,HEAD"
# the build target
FRAMEWORK_TARGETS = tools petrinetwithtransits
SYNTHESIZER_TARGETS = petrigames symbolic bounded highlevel
UI_TARGETS = protocol ui adamsynt
t=javac

# should be executed no matter if a file with the same name exists or not
.PHONY: check_dependencies
.PHONY: pull_dependencies
.PHONY: rm_dependencies
.PHONY: tools
.PHONY: petrinetwithtransits
.PHONY: petrigames
.PHONY: bounded
.PHONY: symbolic
.PHONY: bdd
.PHONY: mtbdd
.PHONY: highlevel
.PHONY: protocol
.PHONY: ui
.PHONY: adamsynt
#.PHONY: javadoc
.PHONY: setJavac
.PHONY: setJar
.PHONY: setStandalone
.PHONY: setDeploy
.PHONY: setClean
.PHONY: setCleanAll
.PHONY: clean
.PHONY: clean-all
.PHONY: src_withlibs
.PHONY: src

# functions
create_bashscript = \#!/bin/bash\n\nBASEDIR=\"\044(dirname \044\060)\"\n\nif [ ! -f \"\044BASEDIR/adam$(strip $(1)).jar\" ] ; then\n\techo \"adam$(strip $(1)).jar not found! Run 'ant jar' first!\" >&2\n\texit 127\nfi\n\njava -DPROPERTY_FILE=./ADAM.properties -Dfile.encoding=UTF-8 -jar \"\044BASEDIR/adam$(strip $(1)).jar\" \"\044@\"

define generate_src
	mkdir -p adam_src
	if [ $(1) = true ]; then\
		cp -R ./dependencies/libs ./adam_src/libs/; \
		rm -rf ./adam_src/libs/.git; \
	fi
	for i in $$(find . -type d \( -path ./benchmarks -o -path ./test/lib -o -path ./lib -o -path ./adam_src -o -path ./dependencies -o -path ./.git \) -prune -o -name '*' -not -regex ".*\(class\|qcir\|pdf\|tex\|apt\|dot\|jar\|ods\|txt\|tar.gz\|aux\|log\|res\|aig\|aag\|lola\|cex\|properties\|json\|xml\|out\|pnml\|so\)" -type f); do \
		echo "cp" $$i; \
		cp --parent $$i ./adam_src/ ;\
	done
	tar -zcvf adam_src.tar.gz adam_src
	rm -r -f ./adam_src
endef

# targets
all: deploy

check_dependencies:
	@if [ ! -d "dependencies" ]; then \
		echo "The dependencies folder is missing. Please execute make pull_dependencies first.";\
	fi

pull_dependencies:
	./pull_dependencies.sh ${DEPENDENCIES_FOLDERS} ${DEPENDENCIES_REPOS} ${DEPENDENCIES_REV}

rm_dependencies:
	$(RM) -rf dependencies

tools: check_dependencies
	ant -buildfile ./dependencies/framework/tools/build.xml $(t)

petrinetwithtransits: check_dependencies
	ant -buildfile ./dependencies/framework/petrinetWithTransits/build.xml $(t)

petrigames: check_dependencies
	ant -buildfile ./dependencies/synthesizer/petriGames/build.xml $(t)

bounded: check_dependencies
	ant -buildfile ./dependencies/boundedSynthesis/build.xml $(t)

bdd: check_dependencies
	ant -buildfile ./dependencies/synthesizer/symbolicalgorithms/bddapproach/build.xml $(t)

mtbdd: check_dependencies
	ant -buildfile ./dependencies/synthesizer/symbolicalgorithms/mtbddapproach/build.xml $(t)

symbolic: bdd mtbdd

highlevel: check_dependencies
	ant -buildfile ./dependencies/high-level/build.xml $(t)

protocol: check_dependencies
	ant -buildfile ./dependencies/server-command-line-protocol/build.xml $(t)

ui: check_dependencies
	ant -buildfile ./dependencies/ui/build.xml $(t)

adamsynt: check_dependencies
	ant -buildfile ./build.xml $(t)

setJavac:
	$(eval t=javac)

setStandalone:
	$(eval t=jar-standalone)

setDeploy:
	$(eval t=deploy)

setClean:
	$(eval t=clean)

setCleanAll:
	$(eval t=clean-all)

deploy: $(FRAMEWORK_TARGETS) $(SYNTHESIZER_TARGETS) protocol ui setDeploy adamsynt
	mkdir -p deploy
	echo "$(call create_bashscript, SYNT)" > ./deploy/adamSYNT
	chmod +x ./deploy/adamSYNT
	cp ./adam_synt.jar ./deploy/adamSYNT.jar
	cp ./ADAM.properties ./deploy/ADAM.properties

clean: setClean $(FRAMEWORK_TARGETS) $(SYNTHESIZER_TARGETS) $(UI_TARGETS)
	$(RM) -r -f deploy
	$(RM) -r -f javadoc

clean-all: setCleanAll $(FRAMEWORK_TARGETS) $(SYNTHESIZER_TARGETS) $(UI_TARGETS)
	$(RM) -r -f deploy
	$(RM) -r -f javadoc

#javadoc:
#	ant javadoc

src_withlibs: clean-all
	$(call generate_src, true)

src: clean-all
	$(call generate_src, false)
