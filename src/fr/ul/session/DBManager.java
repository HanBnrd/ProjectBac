/*
 *  License and Copyright:
 *  This file is part of "ProjectBac" project.
 *
 *   It is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   It is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2017 by LORIA, Université de Lorraine
 *  All right reserved
 */
/**
 * Projet : ProjectBac
 * Classe (statique) de gestion de la base de données
 * @author Azim Roussanaly
 * sept 2017
 */
package fr.ul.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBManager {
	private static final Logger LOG = Logger.getLogger(DBManager.class.getName());

	public static String URI; 		//URI la base de données
	public static String USER; 		//id de l'utilisateur
	public static String PASSWORD; 	//mot de passe

	public static Connection CONNECTION = null;

	/**
	 * connexion à la bd
	 * @return
	 */
	public static void connect() {
		/* Chargement du driver JDBC pour MySQL */
		if (CONNECTION == null){
			try {
				Class.forName( "com.mysql.jdbc.Driver" );
			} catch ( ClassNotFoundException e ) {
				LOG.warning(e.getMessage());
				return;
			}
		}
		/* Connexion à la base de données */
		try {
			CONNECTION = DriverManager.getConnection( URI, USER, PASSWORD );
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			return;
		}
		return;
	}
	/**
	 * Fermer la connexion à la bd
	 * @param connexion
	 */
	public static void quit() {
		if ( CONNECTION != null )
			try {
				CONNECTION.close();
			} catch (SQLException e) {
			}
		CONNECTION = null;
	}


}
