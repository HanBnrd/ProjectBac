package fr.ul.calcul;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVPrinter;

public class Main {

	private static final Logger LOG = Logger.getLogger(ExportMoyenne.class.getName());
	
	public static void main(String[] args) {
		//les paramètres
		String nameSerie = null;
		String numberTour = null;
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;
		
		//les options de la ligne de commande
		Options options = new Options();
		Option serie = new Option("s", "serie", true, "série à exporter");
		serie.setRequired(true);
		options.addOption(serie);
		Option tour = new Option("t", "tour", true, "premier ou deuxième ou remplacables");
		tour.setRequired(true);
		options.addOption(tour);
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
	        if (line.hasOption("s")) {
				nameSerie = line.getOptionValue("s");
			}
	        if (line.hasOption("t")) {
				numberTour = line.getOptionValue("t");
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
	    ExportMoyenne emo = new ExportMoyenne("../samples/export/moyenne.csv");
	    DBManager.URI= dburi;
	    DBManager.USER = dbuser;
	    DBManager.PASSWORD = dbpwd;
	    CSVPrinter pr = null;
	    try {
			pr = emo.buildCVSPrinter();
			emo.writeDB(pr,nameSerie,numberTour);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}
	    
	}

}
