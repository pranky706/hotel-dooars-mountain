/**
 * 
 */
package com.dooars.mountain.model.menugroup;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * MenuGroup.java
 */
public class MenuGroup implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private int groupId;
	private String groupName;
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("groupId", groupId)
				.add("groupName", groupName)
				.toString();
	}
	
	
	
}
