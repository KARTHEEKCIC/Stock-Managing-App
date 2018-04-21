package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "autonomistock-mobilehub-1223547770-Cart")

public class CartDO {
    private String _userEmail;
    private Double _itemId;
    private Double _catId;
    private String _itemName;
    private Integer _quantity;

    public CartDO () {

    }

    public CartDO(String _userEmail, Double _itemId, Double _catId, String _itemName, Integer _quantity) {
        this._userEmail = _userEmail;
        this._itemId = _itemId;
        this._catId = _catId;
        this._itemName = _itemName;
        this._quantity = _quantity;
    }

    @DynamoDBHashKey(attributeName = "userEmail")
    @DynamoDBAttribute(attributeName = "userEmail")
    public String getUserEmail() {
        return _userEmail;
    }

    public void setUserEmail(final String _userEmail) {
        this._userEmail = _userEmail;
    }
    @DynamoDBRangeKey(attributeName = "itemId")
    @DynamoDBAttribute(attributeName = "itemId")
    public Double getItemId() {
        return _itemId;
    }

    public void setItemId(final Double _itemId) {
        this._itemId = _itemId;
    }
    @DynamoDBAttribute(attributeName = "catId")
    public Double getCatId() {
        return _catId;
    }

    public void setCatId(final Double _catId) {
        this._catId = _catId;
    }
    @DynamoDBAttribute(attributeName = "itemName")
    public String getItemName() {
        return _itemName;
    }

    public void setItemName(final String _itemName) {
        this._itemName = _itemName;
    }
    @DynamoDBAttribute(attributeName = "quantity")
    public Integer getQuantity() {
        return _quantity;
    }

    public void setQuantity(final Integer _quantity) {
        this._quantity = _quantity;
    }

}
