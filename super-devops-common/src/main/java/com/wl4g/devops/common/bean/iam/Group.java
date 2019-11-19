package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.iam.model.GroupExt;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Group extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer id;

	private String name;

	private String displayName;

	private Integer type;

	private Integer parentId;

	private Integer dutyUserId;

	private Integer status;

	// other
	private List<Group> children;

	private List<Integer> menuIds;

	private List<Integer> roleIds;

	private GroupExt groupExt;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName == null ? null : displayName.trim();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getDutyUserId() {
		return dutyUserId;
	}

	public void setDutyUserId(Integer dutyUserId) {
		this.dutyUserId = dutyUserId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Group> getChildren() {
		return children;
	}

	public void setChildren(List<Group> children) {
		this.children = children;
	}

	public List<Integer> getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(List<Integer> menuIds) {
		this.menuIds = menuIds;
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public GroupExt getGroupExt() {
		return groupExt;
	}

	public void setGroupExt(GroupExt groupExt) {
		this.groupExt = groupExt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Group group = (Group) o;
		return Objects.equals(id, group.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}