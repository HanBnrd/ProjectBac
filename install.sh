#!/bin/sh
# ----------------------------------------
# Script d'installation
# Author : Azim Roussanaly
# Organization : Université de Lorraine
# date : oct, 2017
# ---------------------------------------
# exemple:
# > ./install.sh

java -cp tools/ant.jar:tools/ant-launcher.jar:$JAVA_HOME/lib/tools.jar org.apache.tools.ant.Main
