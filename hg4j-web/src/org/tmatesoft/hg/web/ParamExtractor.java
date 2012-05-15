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

import javax.servlet.http.HttpServletRequest;

import org.tmatesoft.hg.core.Nodeid;
import org.tmatesoft.hg.repo.HgRepository;
import org.tmatesoft.hg.util.Path;

/**
 * 
 * @author Artem Tikhomirov
 * @author TMate Software Ltd.
 */
class ParamExtractor {
	public static final String REVISION_PARAM = "rev";
	public static final String REV_INDEX_PARAM = "revIndex";
	
	private final HttpServletRequest request;
	private int revIndex = HgRepository.BAD_REVISION;
	private Nodeid revision = null;
	
	public ParamExtractor(HttpServletRequest req) {
		request = req;
	}

	public boolean hasRevisionIndex() {
		return getRevisionIndex() != HgRepository.NO_REVISION;
	}

	public int getRevisionIndex() {
		if (revIndex == HgRepository.BAD_REVISION) {
			String pv;
			revIndex = (pv = request.getParameter(REV_INDEX_PARAM)) == null ? HgRepository.NO_REVISION : Integer.parseInt(pv);
		}
		return revIndex;
	}
	
	public boolean hasRevision() {
		return !getRevision().isNull();
	}
	
	public Nodeid getRevision() {
		if (revision == null) {
			String pv;
			revision = (pv = request.getParameter(REVISION_PARAM)) == null ? Nodeid.NULL : Nodeid.fromAscii(pv);
		}
		return revision;
	}

	public Path getFile() {
		String pi = request.getPathInfo();
		return pi == null ? null : Path.create(pi.charAt(0) == '/' ? pi.substring(1) : pi);
	}
}
