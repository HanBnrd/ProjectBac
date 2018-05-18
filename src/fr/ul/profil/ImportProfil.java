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
 * Classe de chargement d'une liste de profils au format CSV
 * et de stockage dans la base de donnée
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.profil;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class ImportProfil {
    private static final Logger LOG = Logger.getLogger(ImportProfil.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ImportProfil(String filename) {
		super();
		this.filename = filename;
	}
	/**
	 * chargement d'un fichier au format CSV
	 * dont la première ligne est le nom des champs
	 * @return CSVParser
	 * @throws IOException
	 */
	public CSVParser buildCVSParser() throws IOException {
		CSVParser res = null;
		Reader in;
		in = new FileReader(filename);
		CSVFormat csvf = CSVFormat.DEFAULT.withCommentMarker('#').withDelimiter(';');
		res = new CSVParser(in, csvf);
		return res;
	}
	/**
	 * insérer ou mettre à jour un profil
	 * @param serie
	 * @param mention
	 * @param specialite
	 * @param section
	 * @return
	 */
	private boolean add(String serie, String mention, String specialite, String section) {
		boolean res = true;
		String sql = "";
        PreparedStatement ps = null;
        sql = "INSERT into profil(serie,mention,specialite,section)"
        		+" VALUES(?,?,?,?)";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
	        ps.setString(1, serie);
	        ps.setString(2, mention);
	        ps.setString(3, specialite);
	        ps.setString(4, section);
	        LOG.info(ps.toString());
	        ps.executeUpdate();
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			res = false;
		}
		return res;
	}
	/**
	 * sauvegarde dans la base de données
	 * @param parser
	 * @return
	 */
	public int updateDB(CSVParser parser) {
		int res = 0;
		DBManager.connect();
		for (CSVRecord item : parser) {
			String serie = item.get(0).trim();
			String mention = item.get(1).trim();
			String specialite = item.get(2).trim();
			String section = item.get(3).trim();
			//enregistrer
			if (add(serie, mention, specialite, section)){
				res++;
			}
		}
		DBManager.quit();
		return res;
	}

	//setters & getters
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

}
