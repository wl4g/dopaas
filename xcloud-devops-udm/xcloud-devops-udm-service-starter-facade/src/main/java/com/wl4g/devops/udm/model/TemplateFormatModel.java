package com.wl4g.devops.udm.model;

import com.wl4g.devops.udm.service.md.MdMenuTree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2021-01-15 11:03:00
 */
public class TemplateFormatModel {

	private String path;
	private String mdHtml;

	private List<MdMenuTree> mdMenuTrees = new ArrayList<>();

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMdHtml() {
		return mdHtml;
	}

	public void setMdHtml(String mdHtml) {
		this.mdHtml = mdHtml;
	}

	public List<MdMenuTree> getMdMenuTrees() {
		return mdMenuTrees;
	}

	public void setMdMenuTrees(List<MdMenuTree> mdMenuTrees) {
		this.mdMenuTrees = mdMenuTrees;
	}
}
