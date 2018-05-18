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
 * Classe de chargement d'une liste de notes au format CSV
 * et de stockage dans la base de donnée
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.note;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class ImportNote {
    private static final Logger LOG = Logger.getLogger(ImportNote.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ImportNote(String filename) {
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
	 * insérer ou mettre à jour une note
	 * @param id
	 * @param code
	 * @param note
	 * @return
	 */
	private boolean add(String id, String code, String note) {
		boolean res = true;
		String sql = "";
        PreparedStatement ps = null;
        sql = "INSERT into note(candidat_id,epreuve_code,note)"
        		+" VALUES(?,?,?)"
        		+" ON DUPLICATE KEY UPDATE"
        		+" note=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
	        ps.setString(1, id);
	        ps.setString(2, code);
	        ps.setString(3, note);
	        ps.setString(4, note);
	        LOG.info(ps.toString());
	        ps.executeUpdate();
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			res = false;
		}
		return res;
	}
	/**
	 * insérer ou mettre à jour une foreign key matière
	 * @param id
	 * @param code
	 * @param matiere
	 * @return
	 */
	private boolean addMatiere(String id, String code, String matiere) {
		boolean res = true;
		String sql = "";
        PreparedStatement ps = null;
        sql = "UPDATE note"
        		+" SET matiere_code=?"
        		+" WHERE candidat_id=? AND epreuve_code=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
	        ps.setString(1, matiere);
	        ps.setString(2, id);
	        ps.setString(3, code);
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
			String code = item.get(1).trim();
			String note = item.get(2).trim();
			if (note.isEmpty()) {
				note=null;
			}
			//enregistrer
			if (add(id, code, note)){
				res++;
			}
		}
		DBManager.quit();
		return res;
	}
	/**
	 * sauvegarde dans la base de données des foreign keys
	 * @param parser
	 * @return
	 */
	public int updateFkDB(CSVParser parser) {
		int res = 0;
		DBManager.connect();
		for (CSVRecord item : parser) {
			String id = item.get(0).trim();
			String code = item.get(1).trim();
			String matiere = item.get(3).trim();
			//enregistrer
			if (!matiere.isEmpty()){
				if (addMatiere(id, code, matiere)){
					res++;
				}
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
