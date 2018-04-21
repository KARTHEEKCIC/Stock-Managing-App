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

@DynamoDBTable(tableName = "autonomistock-mobilehub-1223547770-Count")

public class CountDO {
    private String _count;
    private Integer _value;

    @DynamoDBHashKey(attributeName = "Count")
    @DynamoDBAttribute(attributeName = "Count")
    public String getCount() {
        return _count;
    }

    public void setCount(final String _count) {
        this._count = _count;
    }
    @DynamoDBAttribute(attributeName = "Value")
    public Integer getValue() {
        return _value;
    }

    public void setValue(final Integer _value) {
        this._value = _value;
    }

}
