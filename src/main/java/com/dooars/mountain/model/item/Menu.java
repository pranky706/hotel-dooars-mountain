/**
 * 
 */
package com.dooars.mountain.model.item;

import com.dooars.mountain.constants.AllGolbalConstants;

import java.util.List;


/**
 * @author Prantik Guha
 * 21-May-2021 
 * Menu.java
 */
public class Menu {
	

	private final String bucketUrl = AllGolbalConstants.BUCKET_URL;
	
	
	
	public String getBucketUrl() {
		return bucketUrl;
	}

	private List<GroupValue> groupValues;

	public List<GroupValue> getGroupValues() {
		return groupValues;
	}

	public void setGroupValues(List<GroupValue> groupValues) {
		this.groupValues = groupValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Menu [bucketUrl=");
		builder.append(bucketUrl);
		builder.append(", groupValues=");
		builder.append(groupValues);
		builder.append("]");
		return builder.toString();
	}

	
	
	

}
