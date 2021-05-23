package com.dooars.mountain.model.centraloffer;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Prantik Guha on 23-05-2021
 **/
public class CentralOffer implements Serializable {

    private static final long serialVersionUID = 1323232L;
    private float offer;
    private LocalDate offerFrom;
    private LocalDate offerUpto;
    private long offerId;
    private String offerDescription;
    private String offerImageName;
    private String offerName;

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

    public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getOfferImageName() {
        return offerImageName;
    }

    public void setOfferImageName(String offerImageName) {
        this.offerImageName = offerImageName;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("offer", offer)
                .add("offerFrom", offerFrom)
                .add("offerUpto", offerUpto)
                .add("offerId", offerId)
                .add("offerDescription", offerDescription)
                .add("offerImageName", offerImageName)
                .add("offerName", offerName)
                .toString();
    }
}
