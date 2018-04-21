package com.example.android.autonomistock;

/**
 * Created by kartheek on 15/4/18.
 */

public class CategoryHolder {
    private String CategoryName;
    private int CategoryId;

    CategoryHolder(String CategoryName, int CategoryId) {
        this.CategoryName = CategoryName;
        this.CategoryId = CategoryId;
    }

    String getCategoryName() {
        return CategoryName;
    }

    int getCategoryId() {
        return CategoryId;
    }

    void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }
}
