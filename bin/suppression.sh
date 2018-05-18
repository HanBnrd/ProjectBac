#!/bin/sh
# ----------------------------------------
# Script de démarrage de l'application : ProjectBac
# Author : Johann Benerradi
# Organization : Université de Lorraine
# date : dec, 2017
# ---------------------------------------
# exemple:
# > ./suppression.sh -c <id> -r jdbc:mysql://localhost:3306/mydb -u <user> -p <password>

# -----------------------------------------
# A modifier
# -----------------------------------------
# --->1) répertoire d'installation
PROJ_HOME="`dirname $0`/.."

# --->2) répertoire où se trouvent les *.jar
LIB_DIR="$PROJ_HOME/lib"

# --->3) répertoire où se trouvent les *.class
CLASSES_DIR="$PROJ_HOME/build/classes"


# --->4) nom de la classe à démarrer
CLASS_NAME=fr.ul.suppression.Main

# -->5) paramètres de la commande
PARAMS=""

# -----------------------------------------
# A ne pas modifier
# -----------------------------------------

# ---->Classpath automatique
PROJ_CP="$PROJ_HOME/build/classes"
for x in `ls "$LIB_DIR"`
do
PROJ_CP="$PROJ_CP:$LIB_DIR/$x"
done

# ---->java command
COMMAND="java -classpath \"$PROJ_CP\" $CLASS_NAME $PARAMS $@"
#echo $COMMAND
eval $COMMAND

# ----------------------------------------
#                  END
# ----------------------------------------
