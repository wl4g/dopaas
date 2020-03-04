package com.wl4g.devops.coss.model;

/**
 * COSS Object ACL。
 */
public class ObjectAcl {

	private Owner owner;
	private ACL permission;
	private String versionId;

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public ACL getPermission() {
		return permission;
	}

	public void setPermission(ACL permission) {
		this.permission = permission;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}
