/**
 * 
 */
package com.dooars.mountain.model.item;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * GroupValue.java
 */
public class GroupValue {
	
	private String groupName;
	private int groupId;
	private List<Item> items;

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
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("groupName", groupName)
				.add("groupId", groupId)
				.add("items", items)
				.toString();
	}
}
