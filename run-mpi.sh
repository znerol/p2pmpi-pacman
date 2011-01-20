#!/bin/bash

BASEDIR="$(dirname $(readlink -f $0))"
export P2PMPI_HOME="${BASEDIR}/lib/p2pmpi"
export PATH="${BASEDIR}/lib/p2pmpi/bin:${PATH}"
"${BASEDIR}/lib/p2pmpi/bin/p2pmpirun" -n 3 -l "${BASEDIR}/etc/pacman-p2pmpi-xfer.txt" pacifism.PacmanMpi
