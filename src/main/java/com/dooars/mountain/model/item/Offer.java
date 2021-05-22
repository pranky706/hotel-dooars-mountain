/**
 * 
 */
package com.dooars.mountain.model.item;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * Offer.java
 */
public class Offer implements Serializable{
	
	
	private static final long serialVersionUID = 12323878232L;
	private float offer;
	private LocalDate offerFrom;
	private LocalDate offerUpto;
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Offer [offer=");
		builder.append(offer);
		builder.append(", offerFrom=");
		builder.append(offerFrom);
		builder.append(", offerUpto=");
		builder.append(offerUpto);
		builder.append("]");
		return builder.toString();
	}
	
	

}
