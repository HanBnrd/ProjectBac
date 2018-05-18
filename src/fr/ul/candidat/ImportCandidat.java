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
 * Classe de chargement d'une liste de candidats au format CSV
 * et de stockage dans la base de donnée
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.candidat;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class ImportCandidat {
    private static final Logger LOG = Logger.getLogger(ImportCandidat.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ImportCandidat(String filename) {
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
	 * insérer ou mettre à jour un candidat
	 * @param id
	 * @param serie
	 * @param mention
	 * @param specialite
	 * @param section
	 * @return
	 */
	private boolean add(String id, String serie, String mention, String specialite, String section) {
		boolean res = true;
		String sql = "";
        PreparedStatement ps = null;
        sql = "INSERT into candidat(id,profil_serie,profil_mention,profil_specialite,profil_section)"
        		+" VALUES(?,?,?,?,?)"
        		+" ON DUPLICATE KEY UPDATE"
        		+" profil_serie=?,profil_mention=?,profil_specialite=?,profil_section=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
	        ps.setString(1, id);
	        ps.setString(2, serie);
	        ps.setString(3, mention);
	        ps.setString(4, specialite);
	        ps.setString(5, section);
	        ps.setString(6, serie);
	        ps.setString(7, mention);
	        ps.setString(8, specialite);
	        ps.setString(9, section);
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
			String id = item.get(0).trim();
			String serie = item.get(1).trim();
			String mention = item.get(2).trim();
			String specialite = item.get(3).trim();
			String section = item.get(4).trim();
			//enregistrer
			if (add(id, serie, mention, specialite, section)){
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
