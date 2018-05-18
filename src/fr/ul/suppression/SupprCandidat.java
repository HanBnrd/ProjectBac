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
 * Classe de suppression d'un candidat de la base de donnée
  * @author Johann Benerradi
  * dec 2017
*/
package fr.ul.suppression;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;


public class SupprCandidat {
    private static final Logger LOG = Logger.getLogger(SupprCandidat.class.getName());

    //attributs
	private String id;


	//constructeurs
	public SupprCandidat(String id) {
		super();
		this.id = id;
	}
	/**
	 * supprimer un candidat
	 * @param candidat
	 * @return
	 */
	private boolean suppr(String candidat) {
		boolean res = true;
		String sqlNote = "";
        PreparedStatement psNote = null;
        sqlNote = "DELETE FROM note WHERE candidat_id=?";
        try {
			psNote = DBManager.CONNECTION.prepareStatement(sqlNote);
	        psNote.setString(1, candidat);
	        LOG.info(psNote.toString());
	        psNote.executeUpdate();
		} catch (SQLException e) {
			LOG.warning(e.getMessage());
			res = false;
		}
        String sqlCandidat = "";
        PreparedStatement psCandidat = null;
        sqlCandidat = "DELETE FROM candidat WHERE id=?";
        try {
			psCandidat = DBManager.CONNECTION.prepareStatement(sqlCandidat);
	        psCandidat.setString(1, candidat);
	        LOG.info(psCandidat.toString());
	        psCandidat.executeUpdate();
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
	public int updateDB(String candidat) {
		int res = 0;
		DBManager.connect();
		if (suppr(candidat)){
			res++;
		}
		DBManager.quit();
		return res;
	}

	//setters & getters
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
