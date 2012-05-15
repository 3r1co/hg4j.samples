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

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tmatesoft.hg.core.HgCatCommand;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.repo.HgRuntimeException;
import org.tmatesoft.hg.util.ByteChannel;
import org.tmatesoft.hg.util.CancelledException;
import org.tmatesoft.hg.util.Path;

/**
 * 
 * @author Artem Tikhomirov
 * @author TMate Software Ltd.
 */
public class CatServlet extends RepoServlet {
	private static final long serialVersionUID = 2862038117140904684L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final ParamExtractor pe = new ParamExtractor(req);
		try {
			if (!pe.hasRevisionIndex() && !pe.hasRevision()) {
				resp.getWriter().printf("Need to specify either '%s' or '%s' parameter", ParamExtractor.REV_INDEX_PARAM, ParamExtractor.REVISION_PARAM);
				resp.getWriter().flush();
				return;
			}
			Path file = pe.getFile();
			HgCatCommand cmd = hgRepo.createCatCommand();
			cmd.file(file);
			if (pe.hasRevisionIndex()) {
				cmd.revision(pe.getRevisionIndex());
			} else {
				cmd.revision(pe.getRevision());
			}
			final ServletOutputStream out = resp.getOutputStream();
			// XXX taken from Cat.OutputStreamChannel, which may deserve to be utility class in hg4j
			// if it's common
			cmd.execute(new ByteChannel() {
				
				@Override
				public int write(ByteBuffer buffer) throws IOException, CancelledException {
					// FIXME !!! what's the purpose of cancellation from the channel?
					// Why can't it come from channel being adaptable to CancelSupport?
					int count = buffer.remaining();
					while(buffer.hasRemaining()) {
						out.write(buffer.get());
					}
					return count;
				}
			});
		} catch (IllegalArgumentException ex) {
			throw new ServletException(ex);
		} catch (HgRuntimeException ex) {
			throw new ServletException(ex);
		} catch (HgException ex) {
			throw new ServletException(ex);
		} catch (CancelledException ex) {
			// shall not happen - we don't support cancellation
			throw new ServletException(ex);
		}
	}
}
