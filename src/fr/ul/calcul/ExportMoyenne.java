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
 * Classe d'écriture des moyennes du premier ou deuxième
 * tour dans un fichier csv à partir de la base de données
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.calcul;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


public class ExportMoyenne {
    private static final Logger LOG = Logger.getLogger(ExportMoyenne.class.getName());

    //attributs
	private String filename;


	//constructeurs
	public ExportMoyenne(String filename) {
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
	 * écriture des moyennes dans un fichier csv
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
    	sql = "SELECT id FROM candidat WHERE profil_serie=?";
        try {
			ps = DBManager.CONNECTION.prepareStatement(sql);
			ps.setString(1, serie);
	        LOG.info(ps.toString());
	        ResultSet rs=ps.executeQuery();
	        try {
				csvp.print("id");
				csvp.print("moyenne");
		        csvp.println();
		        while (rs.next()){
		        	// id
		        	csvp.print(rs.getString("id"));
		        	// moyenne
		        	String sqlCoeff = "";
		            PreparedStatement psCoeff = null;
		            if (tour.equals("1")) {
			            sqlCoeff = "SELECT note.candidat_id,note.epreuve_code,note.note,coeff.coeff,coeff.bonus FROM note " +
			            		"INNER JOIN candidat ON " +
			            		"note.candidat_id=candidat.id " +
			            		"INNER JOIN coeff ON " +
			            		"note.epreuve_code=coeff.epreuve_code AND " +
			            		"candidat.profil_serie=coeff.profil_serie AND " +
			            		"candidat.profil_mention=coeff.profil_mention AND " +
			            		"candidat.profil_specialite=coeff.profil_specialite AND " +
			            		"candidat.profil_section=coeff.profil_section " +
			            		"INNER JOIN epreuve ON " +
			            		"note.epreuve_code=epreuve.code " +
			            		"WHERE " +
			            		"note.candidat_id=? AND " +
			            		"epreuve.composition IS NULL AND " +
			            		"epreuve.rattrapage IS NULL AND " +
			            		"note.note IS NOT NULL AND " +
			            		"note.note!=\"AB\" AND " +
			            		"note.note!=\"DI\"";
		            }
		            else if (tour.equals("2")) {
		            	sqlCoeff = "SELECT note.candidat_id,note.epreuve_code,note.note,coeff.coeff,coeff.bonus FROM note " +
			            		"INNER JOIN candidat ON " +
			            		"note.candidat_id=candidat.id " +
			            		"INNER JOIN coeff ON " +
			            		"note.epreuve_code=coeff.epreuve_code AND " +
			            		"candidat.profil_serie=coeff.profil_serie AND " +
			            		"candidat.profil_mention=coeff.profil_mention AND " +
			            		"candidat.profil_specialite=coeff.profil_specialite AND " +
			            		"candidat.profil_section=coeff.profil_section " +
			            		"INNER JOIN epreuve ON " +
			            		"note.epreuve_code=epreuve.code " +
			            		"WHERE " +
			            		"note.candidat_id=? AND " +
			            		"epreuve.composition IS NULL AND " +
			            		"epreuve.code NOT IN(SELECT rattrapage FROM epreuve INNER JOIN note ON note.epreuve_code=epreuve.code WHERE candidat_id=? AND rattrapage IS NOT NULL) AND " +
			            		"note.note IS NOT NULL AND " +
			            		"note.note!=\"AB\" AND " +
			            		"note.note!=\"DI\"";
		            }
		            try {
		    			psCoeff = DBManager.CONNECTION.prepareStatement(sqlCoeff);
		    			psCoeff.setString(1, rs.getString("id"));
		    			if (tour.equals("2")) {
		    				psCoeff.setString(2, rs.getString("id"));
		    			}
		    	        LOG.info(psCoeff.toString());
		    	        ResultSet rsCoeff=psCoeff.executeQuery();
		    	        float up = 0;
		    	        float down = 0;
		    	        while (rsCoeff.next()) {
		    	        	if (rsCoeff.getString("coeff.bonus") == null) {
			    	        	up+=Float.parseFloat(rsCoeff.getString("note"))*Float.parseFloat(rsCoeff.getString("coeff"));
			    	        	down+=Float.parseFloat(rsCoeff.getString("coeff"));
		    	        	}
		    	        	else if (Float.parseFloat(rsCoeff.getString("note"))>10) {
		    	        		up+=(Float.parseFloat(rsCoeff.getString("note"))-10)*Float.parseFloat(rsCoeff.getString("coeff"));
		    	        	}
		    	        }
		    	        float moyenne = up/down;
		    	        csvp.print(moyenne);
		    	        csvp.println();
		            } catch (SQLException e) {
		    			LOG.warning(e.getMessage());
		    			res = false;
		    		}
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
