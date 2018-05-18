package fr.ul.suppression;

import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	private static final Logger LOG = Logger.getLogger(SupprCandidat.class.getName());
	
	public static void main(String[] args) {
		//les paramètres
		String idCandidat = null;
		String dburi = null;
		String dbuser = null;
		String dbpwd = null;
		
		//les options de la ligne de commande
		Options options = new Options();
		Option candidat = new Option("c", "candidat", true, "id du candidat à supprimer");
		candidat.setRequired(true);
		options.addOption(candidat);
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
	        if (line.hasOption("c")) {
				idCandidat = line.getOptionValue("c");
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
	    SupprCandidat sca = new SupprCandidat(idCandidat);
	    DBManager.URI= dburi;
	    DBManager.USER = dbuser;
	    DBManager.PASSWORD = dbpwd;
	    sca.updateDB(idCandidat);
		
	}

}
