package com.dooars.mountain.model.operation;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 09-06-2021
 **/
public class OperationTime {

    private int id;
    private int openHr;
    private int openMin;
    private int closeHr;
    private int closeMin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOpenHr() {
        return openHr;
    }

    public void setOpenHr(int openHr) {
        this.openHr = openHr;
    }

    public int getOpenMin() {
        return openMin;
    }

    public void setOpenMin(int openMin) {
        this.openMin = openMin;
    }

    public int getCloseHr() {
        return closeHr;
    }

    public void setCloseHr(int closeHr) {
        this.closeHr = closeHr;
    }

    public int getCloseMin() {
        return closeMin;
    }

    public void setCloseMin(int closeMin) {
        this.closeMin = closeMin;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("openHr", openHr)
                .add("openMin", openMin)
                .add("closeHr", closeHr)
                .add("closeMin", closeMin)
                .toString();
    }
}
