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

@DynamoDBTable(tableName = "autonomistock-mobilehub-1223547770-Issues")

public class IssuesDO {
    private Integer _issueId;
    private String _userEmail;
    private String _description;

    public IssuesDO() {

    }

    public IssuesDO(String _userEmail, String _description, Integer _issueId) {
        this._userEmail = _userEmail;
        this._issueId = _issueId;
        this._description = _description;
    }

    @DynamoDBHashKey(attributeName = "IssueId")
    @DynamoDBAttribute(attributeName = "IssueId")
    public Integer getIssueId() {
        return _issueId;
    }

    public void setIssueId(final Integer _issueId) {
        this._issueId = _issueId;
    }
    @DynamoDBRangeKey(attributeName = "userEmail")
    @DynamoDBAttribute(attributeName = "userEmail")
    public String getUserEmail() {
        return _userEmail;
    }

    public void setUserEmail(final String _userEmail) {
        this._userEmail = _userEmail;
    }
    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return _description;
    }

    public void setDescription(final String _description) {
        this._description = _description;
    }

}
