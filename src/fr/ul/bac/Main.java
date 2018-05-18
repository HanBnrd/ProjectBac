package fr.ul.bac;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVParser;

public class Main {

	private static final Logger LOG = Logger.getLogger(ImportEpreuve.class.getName());
	
	public static void main(String[] args) {
		//les paramètres
		String filenameEpreuve = null;
		String filenameMatiere = null;
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;
		
		//les options de la ligne de commande
		Options options = new Options();
		Option epreuve = new Option("e", "epreuve", true, "nom du fichier .csv contenant les épreuves");
		epreuve.setRequired(true);
		options.addOption(epreuve);
		Option matiere = new Option("m", "matiere", true, "nom du fichier .csv contenant les matières");
		matiere.setRequired(true);
		options.addOption(matiere);
		Option uri = new Option("r", "uri", true, "uri de la bd");
		uri.setRequired(true);
		options.addOption(uri);
		Option user = new Option("u", "user", true, "nom utilisateur");
		user.setRequired(true);
		options.addOption(user);
		Option pwd = new Option("p", "password", true, "mot de passe utilisateur");
		pwd.setRequired(true);
		options.addOption(pwd);

		//parser la ligne de commande
	    CommandLineParser parser = new DefaultParser();
	    try {
	        CommandLine line = parser.parse( options, args );
	        if (line.hasOption("e")) {
				filenameEpreuve = line.getOptionValue("e");
			}
	        if (line.hasOption("m")) {
				filenameMatiere = line.getOptionValue("m");
			}
	        if (line.hasOption("r")) {
				dburi = line.getOptionValue("r");
			}
	        if (line.hasOption("u")) {
				dbuser = line.getOptionValue("u");
			}
	        if (line.hasOption("p")) {
				dbpwd = line.getOptionValue("p");
			}
	    }
	    catch( ParseException exp ) {
	    	LOG.severe("Erreur dans la ligne de commande");
	    	HelpFormatter formatter = new HelpFormatter();
	    	formatter.printHelp( "epreuveloader", options );
	    	System.exit(1);
	    }
	    //traitement
	    ImportEpreuve iep = new ImportEpreuve(filenameEpreuve);
	    ImportMatiere ima = new ImportMatiere(filenameMatiere);
	    DBManager.URI= dburi;
	    DBManager.USER = dbuser;
	    DBManager.PASSWORD = dbpwd;
	    CSVParser pa = null;
	    try {
			pa = iep.buildCVSParser();
			iep.updateDB(pa);
			pa = iep.buildCVSParser();
			iep.updateFkDB(pa);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}
	    try {
			pa = ima.buildCVSParser();
			ima.updateDB(pa);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}
	    
	}

}
