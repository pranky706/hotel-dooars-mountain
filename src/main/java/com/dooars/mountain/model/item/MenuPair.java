/**
 * 
 */
package com.dooars.mountain.model.item;


/**
 * @author Prantik Guha
 * 21-May-2021 
 * MenuPair.java
 */
public class MenuPair {
	
	private String groupName;
	private Item item;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MenuPair [groupName=");
		builder.append(groupName);
		builder.append(", item=");
		builder.append(item);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
