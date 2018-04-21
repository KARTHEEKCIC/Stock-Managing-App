package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "autonomistock-mobilehub-1223547770-Categories")

public class CategoriesDO {
    private Double _catId;
    private String _catName;

    @DynamoDBHashKey(attributeName = "catId")
    @DynamoDBAttribute(attributeName = "catId")
    public Double getCatId() {
        return _catId;
    }

    public void setCatId(final Double _catId) {
        this._catId = _catId;
    }
    @DynamoDBAttribute(attributeName = "catName")
    public String getCatName() {
        return _catName;
    }

    public void setCatName(final String _catName) {
        this._catName = _catName;
    }

}
