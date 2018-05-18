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
 * Classe d'écriture des règles dans un fichier csv
 * à partir de la base de données pour un profil
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.regle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


public class ExportCoeff {
    private static final Logger LOG = Logger.getLogger(ExportCoeff.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ExportCoeff(String filename) {
		super();
		this.filename = filename;
	}
	/**
	 * chargement d'un fichier csv pour écriture
	 * @return CSVPrinter
	 * @throws IOException
	 */
	public CSVPrinter buildCVSPrinter() throws IOException {
		CSVPrinter res = null;
		Writer out;
		out = new FileWriter(filename);
		CSVFormat csvf = CSVFormat.DEFAULT.withCommentMarker('#').withDelimiter(';');
		res = new CSVPrinter(out, csvf);
		return res;
	}
	/**
	 * écriture des règles dans un fichier csv
	 * @param csvp
	 * @param serie
	 * @param mention
	 * @param specialite
	 * @param section
	 * @return
	 */
	public boolean writeDB(CSVPrinter csvp,String serie,String mention,String specialite,String section) {
		boolean res = true;
		DBManager.connect();
		String sql = "";
        PreparedStatement ps = null;
    	sql = "SELECT * FROM coeff INNER JOIN epreuve ON coeff.epreuve_code=epreuve.code WHERE " +
        		"profil_serie=? AND profil_mention=? AND profil_specialite=? AND profil_section=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps.setString(1, serie);
			ps.setString(2, mention);
			ps.setString(3, specialite);
			ps.setString(4, section);
	        LOG.info(ps.toString());
	        ResultSet rs=ps.executeQuery();
	        try {
				csvp.print("code");
				csvp.print("libelle");
				csvp.print("coeff");
				csvp.print("composition");
				csvp.print("bonus");
				csvp.print("facultatif");
		        csvp.println();
		        while (rs.next()){
		        	csvp.print(rs.getString("epreuve_code"));
		        	csvp.print(rs.getString("epreuve.libelle"));
		        	csvp.print(rs.getString("coeff"));
		        	csvp.print(rs.getString("epreuve.composition"));
		        	csvp.print(rs.getString("bonus"));
		        	csvp.print(rs.getString("facultatif"));
		        	csvp.println();
		        }
		        csvp.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			res = false;
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
