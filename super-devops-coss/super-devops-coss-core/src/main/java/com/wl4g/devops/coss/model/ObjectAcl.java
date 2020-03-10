package com.wl4g.devops.coss.model;

/**
 * COSS Object ACL。
 */
public class ObjectAcl {

	private Owner owner;
	private ACL permission;

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

}
