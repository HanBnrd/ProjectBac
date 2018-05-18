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
 * Classe d'écriture des notes du premier ou deuxième
 * tour, ou des notes remplaçables, dans un fichier csv
 * à partir de la base de données
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.session;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


public class ExportNote {
    private static final Logger LOG = Logger.getLogger(ExportNote.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ExportNote(String filename) {
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
	 * écriture des notes dans un fichier csv
	 * @param csvp
	 * @param serie
	 * @param tour
	 * @return
	 */
	public boolean writeDB(CSVPrinter csvp, String serie, String tour) {
		boolean res = true;
		DBManager.connect();
		String sql = "";
        PreparedStatement ps = null;
        if (tour.equals("1")) { // premier tour
        	sql = "SELECT * FROM note INNER JOIN epreuve ON note.epreuve_code=epreuve.code " +
            		"INNER JOIN candidat ON note.candidat_id=candidat.id " +
            		"WHERE epreuve.rattrapage IS NULL AND candidat.profil_serie=?";
        }
        else if (tour.equals("2")) { // deuxieme tour
        	sql = "SELECT * FROM note INNER JOIN epreuve ON note.epreuve_code=epreuve.code " +
            		"INNER JOIN candidat ON note.candidat_id=candidat.id " +
            		"WHERE epreuve.rattrapage IS NOT NULL AND candidat.profil_serie=?";
        }
        else if (tour.equals("R")) { // remplacables
        	sql = "SELECT * FROM note INNER JOIN epreuve ON note.epreuve_code=epreuve.code " +
            		"INNER JOIN candidat ON note.candidat_id=candidat.id " +
            		"WHERE epreuve.code IN (SELECT rattrapage FROM epreuve WHERE rattrapage IS NOT NULL) " +
            		"AND candidat.profil_serie=?";
        }
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps.setString(1, serie);
	        LOG.info(ps.toString());
	        ResultSet rs=ps.executeQuery();
	        try {
				csvp.print("id");
				csvp.print("epreuve_code");
				csvp.print("epreuve_libelle");
				csvp.print("matiere_code");
				csvp.print("matiere_libelle");
				csvp.print("note");
		        csvp.println();
		        while (rs.next()){
		        	// id
		        	csvp.print(rs.getString("candidat_id"));
		        	// epreuve_code
		        	csvp.print(rs.getString("epreuve_code"));
		        	// epreuve_libelle
		        	csvp.print(rs.getString("epreuve.libelle"));
		            // matiere_code
		        	csvp.print(rs.getString("matiere_code"));
		        	// matiere_libelle
		        	if (rs.getString("matiere_code") != null) {
			        	String sqlMatiere = "";
			            PreparedStatement psMatiere = null;
			            sqlMatiere = "SELECT libelle FROM matiere WHERE code=?";
			            try {
			    			psMatiere = DBManager.CONNECTION.prepareStatement(sqlMatiere);
			    			psMatiere.setString(1, rs.getString("matiere_code"));
			    	        LOG.info(psMatiere.toString());
			    	        ResultSet rsMatiere=psMatiere.executeQuery();
			    	        rsMatiere.next();
			    	        csvp.print(rsMatiere.getString("libelle"));
			            } catch (SQLException e) {
			    			LOG.warning(e.getMessage());
			    			res = false;
			    		}
		        	}
		        	else {
		        		csvp.print("");
		        	}
		        	// note
		        	csvp.print(rs.getString("note"));
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
