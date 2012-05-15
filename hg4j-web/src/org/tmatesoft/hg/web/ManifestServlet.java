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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tmatesoft.hg.core.HgCallbackTargetException;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgFileRevision;
import org.tmatesoft.hg.core.HgManifestCommand;
import org.tmatesoft.hg.core.HgManifestHandler;
import org.tmatesoft.hg.core.Nodeid;
import org.tmatesoft.hg.repo.HgManifest.Flags;
import org.tmatesoft.hg.repo.HgRepository;
import org.tmatesoft.hg.util.CancelledException;
import org.tmatesoft.hg.util.Path;

/**
 * 
 * @author Artem Tikhomirov
 * @author TMate Software Ltd.
 */
public class ManifestServlet extends RepoServlet {
	private static final long serialVersionUID = 1141826997162337214L;
	
	private final String fileHistoryLink = "history";
	private final String fileCheckoutLink = "checkout";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ParamExtractor pe = new ParamExtractor(req);
		try {
			HgManifestCommand cmd = hgRepo.createManifestCommand();
			if (pe.hasRevisionIndex()) {
				cmd.revision(pe.getRevisionIndex());
			} else if (pe.hasRevision()) {
				int csetIndex = hgRepo.getRepository().getChangelog().getRevisionIndex(pe.getRevision());
				cmd.revision(csetIndex);
			} else {
				cmd.revision(HgRepository.TIP);
			}
			final PrintWriter pw = resp.getWriter();
			pw.print("<head><title>Hg4J ManifestCommand</title></head><body>");
			cmd.dirs(true).execute(new HgManifestHandler() {
				
				@Override
				public void begin(Nodeid manifestRevision) throws HgCallbackTargetException {
					pw.print("<table><col align='center'/><col align='left'/><col align='right'/>");
				}

				@Override
				public void file(HgFileRevision fileRevision) throws HgCallbackTargetException {
					Flags ff = fileRevision.getFileFlags();
					char f = new char[] { 'x', 'l', 'r'}[ff.ordinal()];
					String fmt = "<tr><td> %c </td<td><a href='%5$s/%2$s'>%2$s</a></td><td><a href='%6$s/%2$s?%7$s=%4$s'>%3$s</a></td></tr>";
					Nodeid nid = fileRevision.getRevision();
					pw.printf(fmt, f, fileRevision.getPath(), nid.shortNotation(), nid, fileHistoryLink, fileCheckoutLink, REVISION_PARAM);
				}
				
				@Override
				public void end(Nodeid manifestRevision) throws HgCallbackTargetException {
					pw.print("</table>");
				}
				
				@Override
				public void dir(Path path) throws HgCallbackTargetException {
					// TODO use styles instead of <em>
					pw.printf("<tr><td colspan=3><em>%s</em></td></tr>", path);
				}
			});
			pw.print("</body></html>");
		} catch (HgCallbackTargetException ex) {
			throw new ServletException(ex.getTargetException());
		} catch (CancelledException ex) {
			throw new ServletException(ex);
		} catch (HgException ex) {
			throw new ServletException(ex);
		}
	}
}
