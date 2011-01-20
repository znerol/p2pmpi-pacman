#!/bin/bash

BASEDIR="$(dirname $(readlink -f $0))"
java -cp "${BASEDIR}/pacman-p2pmpi.jar:${BASEDIR}/lib/p2pmpi/log4j.jar" pacifism.PacmanSingleUser "$@"
