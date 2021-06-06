package com.dooars.mountain.model.item;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 06-06-2021
 **/
public class Category {

    private int categoryId;
    private String categoryName;
    private String categoryImageName;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImageName() {
        return categoryImageName;
    }

    public void setCategoryImageName(String categoryImageName) {
        this.categoryImageName = categoryImageName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("categoryId", categoryId)
                .add("categoryName", categoryName)
                .add("categoryImageName", categoryImageName)
                .toString();
    }
}
