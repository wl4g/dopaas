package com.wl4g.devops.coss.model;

/**
 * COSS Object ACL。
 */
public class ObjectAcl {

	private Owner owner;
	private ACL acl;

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public ACL getAcl() {
		return acl;
	}

	public void setAcl(ACL acl) {
		this.acl = acl;
	}

}
