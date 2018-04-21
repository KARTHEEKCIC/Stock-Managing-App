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

@DynamoDBTable(tableName = "autonomistock-mobilehub-1223547770-items")

public class ItemsDO {
    private Double _itemId;
    private Double _availability;
    private String _description;
    private String _itemName;
    private Double _catId;

    public ItemsDO() {

    }

    public ItemsDO(Double _itemId) {
        this._itemId = _itemId;
        _availability = null;
        _description = null;
        _itemName = null;
        _catId = null;
    }

    @DynamoDBHashKey(attributeName = "ItemId")
    @DynamoDBAttribute(attributeName = "ItemId")
    public Double getItemId() {
        return _itemId;
    }

    public void setItemId(final Double _itemId) {
        this._itemId = _itemId;
    }
    @DynamoDBAttribute(attributeName = "Availability")
    public Double getAvailability() {
        return _availability;
    }

    public void setAvailability(final Double _availability) {
        this._availability = _availability;
    }
    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return _description;
    }

    public void setDescription(final String _description) {
        this._description = _description;
    }
    @DynamoDBAttribute(attributeName = "ItemName")
    public String getItemName() {
        return _itemName;
    }

    public void setItemName(final String _itemName) {
        this._itemName = _itemName;
    }
    @DynamoDBAttribute(attributeName = "catId")
    public Double getCatId() {
        return _catId;
    }

    public void setCatId(final Double _catId) {
        this._catId = _catId;
    }

}
