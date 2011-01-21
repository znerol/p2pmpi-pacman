#!/bin/bash

BASEDIR="$(pwd)/$(dirname $0)"
java -ea -cp "${BASEDIR}/pacman-p2pmpi.jar:${BASEDIR}/lib/p2pmpi/log4j.jar" pacifism.PacmanSingleUser "$@"
