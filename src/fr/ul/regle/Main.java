package fr.ul.regle;

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

	private static final Logger LOG = Logger.getLogger(ExportCoeff.class.getName());
	
	public static void main(String[] args) {
		//les paramètres
		String nameSerie = null;
		String nameMention = null;
		String nameOption = null;
		String nameEuro = null;
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;
		
		//les options de la ligne de commande
		Options options = new Options();
		Option serie = new Option("s", "serie", true, "série à exporter");
		serie.setRequired(true);
		options.addOption(serie);
		Option mention = new Option("m", "mention", true, "mention à exporter");
		mention.setRequired(true);
		options.addOption(mention);
		Option option = new Option("o", "option", true, "spécialité à exporter");
		option.setRequired(true);
		options.addOption(option);
		Option euro = new Option("e", "euro", true, "section à exporter");
		euro.setRequired(true);
		options.addOption(euro);
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
	        if (line.hasOption("m")) {
				nameMention = line.getOptionValue("m");
			}
	        if (line.hasOption("o")) {
				nameOption = line.getOptionValue("o");
			}
	        if (line.hasOption("e")) {
				nameEuro = line.getOptionValue("e");
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
	    ExportCoeff eco = new ExportCoeff("../samples/export/regle.csv");
	    DBManager.URI= dburi;
	    DBManager.USER = dbuser;
	    DBManager.PASSWORD = dbpwd;
	    CSVPrinter pr = null;
	    try {
			pr = eco.buildCVSPrinter();
			eco.writeDB(pr,nameSerie,nameMention,nameOption,nameEuro);
		} catch (IOException e) {
			LOG.severe(e.getMessage());
		}
	    
	}

}
