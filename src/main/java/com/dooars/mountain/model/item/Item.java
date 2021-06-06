/**
 * 
 */
package com.dooars.mountain.model.item;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * Item.java
 */
public class Item implements Serializable{

	
	private static final long serialVersionUID = 12323232L;
	private int itemId;
	private String itemName;
	private String description;
	private float offer;
	private LocalDate offerFrom;
	private LocalDate offerUpto;
	private float price;
	private int groupId;
	private String isAvailable;
	private String imageName;
	private int categoryId;
	
	
	
	public String getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(String isAvailable) {
		this.isAvailable = isAvailable;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public float getOffer() {
		return offer;
	}
	public void setOffer(float offer) {
		this.offer = offer;
	}
	public LocalDate getOfferFrom() {
		return offerFrom;
	}
	public void setOfferFrom(LocalDate offerFrom) {
		this.offerFrom = offerFrom;
	}
	public LocalDate getOfferUpto() {
		return offerUpto;
	}
	public void setOfferUpto(LocalDate offerUpto) {
		this.offerUpto = offerUpto;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("itemId", itemId)
				.add("itemName", itemName)
				.add("description", description)
				.add("offer", offer)
				.add("offerFrom", offerFrom)
				.add("offerUpto", offerUpto)
				.add("price", price)
				.add("groupId", groupId)
				.add("isAvailable", isAvailable)
				.add("imageName", imageName)
				.add("categoryId", categoryId)
				.toString();
	}
}
