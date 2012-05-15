/*
 * Copyright (c) 2012 TMate Software Ltd
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For information on how to redistribute this software under
 * the terms of a license other than GNU General Public License
 * contact TMate Software at support@hg4j.com
 */
package org.tmatesoft.hg.web;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.tmatesoft.hg.core.HgRepoFacade;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;

/**
 * 
 * @author Artem Tikhomirov
 * @author TMate Software Ltd.
 */
public abstract class RepoServlet extends HttpServlet {
	private static final long serialVersionUID = 7033829943432968866L;

	protected HgRepoFacade hgRepo;

	public static HgRepoFacade getRepo(HttpServlet servlet) throws ServletException {
		String repoLoc = servlet.getServletContext().getInitParameter("hg4j.web.repo.location");
		if (repoLoc == null) {
			throw new ServletException("Repository location not confugured");
		}
		try {
			HgRepoFacade rf = new HgRepoFacade();
			rf.initFrom(new File(repoLoc));
			return rf;
		} catch (HgRepositoryNotFoundException ex) {
			throw new ServletException(ex);
		}
	}

	@Override
	public void init() throws ServletException {
		hgRepo = getRepo(this);
	}

}
