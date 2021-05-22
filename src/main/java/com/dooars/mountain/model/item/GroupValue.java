/**
 * 
 */
package com.dooars.mountain.model.item;

import java.util.List;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * GroupValue.java
 */
public class GroupValue {
	
	private String groupName;
	private List<Item> items;
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
		StringBuilder builder = new StringBuilder();
		builder.append("GroupValue [groupName=");
		builder.append(groupName);
		builder.append(", items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}
	
	

}
