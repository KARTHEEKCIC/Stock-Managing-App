package com.example.android.autonomistock;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.CategoriesDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kartheek on 20/4/18.
 */

public class Utility {

    static DynamoDBMapper dynamoDBMapper;

    <T> void writeToDatabase(final Context mContext, final T item) {

        dynamoDBMapper = connectToDatabase(mContext);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Utility", "Saving the item to the database");
                dynamoDBMapper.save(item);
            }
        });

        t.start();
    }

    static DynamoDBMapper connectToDatabase(Context mContext) {

        if(dynamoDBMapper == null) {
            AWSMobileClient.getInstance().initialize(mContext, new AWSStartupHandler() {
                @Override
                public void onComplete(AWSStartupResult awsStartupResult) {
                    Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
                }
            }).execute();

            // Instantiate a AmazonDynamoDBMapperClient
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());

            dynamoDBMapper = DynamoDBMapper.builder()
                    .dynamoDBClient(dynamoDBClient)
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .build();
        }

        return dynamoDBMapper;
    }

    <T> void deleteFromDatabase(final Context mContext, final T item) {

        dynamoDBMapper = connectToDatabase(mContext);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Utility", "Saving the item to the database");
                dynamoDBMapper.delete(item);
            }
        });

        t.start();
    }

}



