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
 * Classe de chargement d'une liste de matières au format CSV
 * et de stockage dans la base de donnée
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.bac;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class ImportMatiere {
    private static final Logger LOG = Logger.getLogger(ImportMatiere.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ImportMatiere(String filename) {
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
	 * insérer ou mettre à jour une matière
	 * @param code
	 * @param libelle
	 * @param epreuve
	 * @return
	 */
	private boolean add(String code, String libelle, String epreuve) {
		boolean res = true;
		String sql = "";
        PreparedStatement ps = null;
        sql = "INSERT into matiere(code,libelle,epreuve_code)"
        		+" VALUES(?,?,?)"
        		+" ON DUPLICATE KEY UPDATE"
        		+" libelle=?,epreuve_code=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
	        ps.setString(1, code);
	        ps.setString(2, libelle);
	        ps.setString(3, epreuve);
	        ps.setString(4, libelle);
	        ps.setString(5, epreuve);
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
			String code = item.get(0).trim();
			String libelle = item.get(1).trim();
			String epreuve = item.get(2).trim();
			//enregistrer
			if (add(code, libelle, epreuve)){
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
