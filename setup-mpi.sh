#!/bin/bash

BASEDIR="$(pwd)/$(dirname $0)"
export P2PMPI_HOME="${BASEDIR}/lib/p2pmpi"
export PATH="${BASEDIR}/lib/p2pmpi/bin:${PATH}"

