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

import static org.tmatesoft.hg.web.ParamExtractor.REVISION_PARAM;
import static org.tmatesoft.hg.web.ParamExtractor.REV_INDEX_PARAM;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.repo.HgRepository;

/**
 * 
 * @author Artem Tikhomirov
 * @author TMate Software Ltd.
 */
public class LogServlet extends RepoServlet {
	private static final long serialVersionUID = 7983667593405508682L;
	private final String manifestLink = "ls";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HgLogCommand cmd = hgRepo.createLogCommand();
		cmd.limit(50);
		cmd.range(hgRepo.getRepository().getChangelog().getLastRevision()-50, HgRepository.TIP);
		try {
			List<HgChangeset> res = cmd.execute();
			PrintWriter pw = resp.getWriter();
			pw.print("<head><title>Hg4J LogCommand</title></head><body>");
			pw.print("<table width=\"90%\"><tr>");
			pw.print("<th>");
			pw.print("Changeset");
			pw.print("<th>");
			pw.print("Commit");
			pw.print("<th>");
			pw.print("Author");
			pw.print("<th>");
			pw.print("When");
			pw.print("</tr>");
			for (ListIterator<HgChangeset> it = res.listIterator(res.size()); it.hasPrevious(); ) {
				pw.print("<tr>");
				HgChangeset cs = it.previous();
				pw.print("<td>");
				pw.printf("<a href=\"%s?%s=%d\">%3$d</a>", manifestLink, REV_INDEX_PARAM, cs.getRevisionIndex());
				pw.print(" : ");
				pw.printf("<a href=\"%s?%s=%s\">%s</a>", manifestLink, REVISION_PARAM, cs.getNodeid(), cs.getNodeid().shortNotation());
				pw.print("<td>");
				pw.print(cs.getComment());
				pw.print("<td>");
				pw.print(cs.getUser());
				pw.print("<td>");
				pw.print(cs.getDate());
				pw.print("</tr>");
			}
			pw.print("</table>");
			pw.print("</body>");
			pw.flush();
		} catch (HgException ex) {
			throw new ServletException(ex);
		}
	}
}
