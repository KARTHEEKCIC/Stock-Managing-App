package com.example.android.autonomistock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.CartDO;
import com.amazonaws.models.nosql.ItemsDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryItemsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView quantity;
    CategoryItemAdapter mAdapter;
    ArrayList<CategoryItem> itemList;
    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    AmazonDynamoDBClient dynamoDBClient;

    static double total_quantity = 0;

    // boolean value to check total quantity from the database only once
    static Boolean check = false;

    private int catId;

    LinearLayout ViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        itemList = new ArrayList<>();
        catId = getIntent().getIntExtra("catId",0);

        ViewCart = (LinearLayout) findViewById(R.id.view_cart);
        quantity = (TextView) findViewById(R.id.quantity);

        ViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to the cart page;
                Intent StudentMainActIntent = new Intent(getApplicationContext(),StudentMainActivity.class);
                if (StudentMainActIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(StudentMainActIntent);
                }
                StudentMainActIntent.putExtra("position",1);
                startActivity(StudentMainActIntent);
            }
        });

        Log.e("CategoryItemActivity",catId+"");
        Log.e("CategoryItemActivity","Calling function");

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        // Instantiate a AmazonDynamoDBMapperClient
        dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        categoryItemsRead();

        Log.e("CategoryItemsActivity",""+total_quantity);

        if(total_quantity>0) {
            quantity.setText(""+ (int) total_quantity);
            ViewCart.setVisibility(View.VISIBLE);
        }

        recyclerView = (RecyclerView) findViewById(R.id.categoryitems);

        mAdapter = new CategoryItemAdapter(itemList,this,ViewCart,quantity,catId);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        //Set divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);
        //Give animation
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    public void categoryItemsRead() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e("CategoryItemsActivity", "Reading the items from the database");

                AttributeValue catIdAttVal = new AttributeValue();
                catIdAttVal.setN(""+catId);
                Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(catIdAttVal);
                Log.e("CategoryItemActivity", condition.toString());

                DynamoDBScanExpression queryExpression = new DynamoDBScanExpression();

                queryExpression.addFilterCondition("catId", condition);

                PaginatedList<ItemsDO> result = dynamoDBMapper.scan(
                        com.amazonaws.models.nosql.ItemsDO.class, queryExpression);


                final SharedPreferences emailSharedPref = CategoryItemsActivity.this.getSharedPreferences(CategoryItemsActivity.this.getString(R.string.login_status)
                        , Context.MODE_PRIVATE);

                DynamoDBQueryExpression query = new DynamoDBQueryExpression<CartDO>()
                        .withHashKeyValues(new CartDO(emailSharedPref.getString("userEmail",null),null,null,null,null));

                PaginatedList<CartDO>  cart_result = dynamoDBMapper.query(CartDO.class, query);

                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();

                String jsonFormOfItem = new String();

                stringBuilder.append("[");

                Log.e("CategoryItem","Entered Function");
                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    jsonFormOfItem = gson.toJson(result.get(i));
                    stringBuilder.append(jsonFormOfItem);
                    if (i != result.size() - 1) {
                        stringBuilder.append(",");
                    }
                }

                stringBuilder.append("]");

                StringBuilder stringBuilder1 = new StringBuilder();

                String jsonFormOfItem1 = new String();

                stringBuilder1.append("[");

                // Loop through query results
                for (int i = 0; i < cart_result.size(); i++) {
                    jsonFormOfItem1 = gson.toJson(cart_result.get(i));
                    stringBuilder1.append(jsonFormOfItem1);
                    if(i != cart_result.size()-1) {
                        stringBuilder1.append(",");
                    }
                }

                stringBuilder1.append("]");

                Log.e("CategoryItemsActivity","CategoryItems - " + stringBuilder.toString());
                Log.e("CategoryItemsActivity","CartItems - " + stringBuilder1.toString());

                try {
                    JSONArray array = new JSONArray(stringBuilder.toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        itemList.add(new CategoryItem(object.getInt("_availability"),object.getString("_itemName"),object.getInt("_itemId")));
                    }

                    JSONArray array1 = new JSONArray(stringBuilder1.toString());
                    for (int i = 0; i < array1.length(); i++) {
                        JSONObject object = (JSONObject) array1.get(i);
                        int j=0;

                        if(object.getDouble("_catId") == catId) {
                            while (object.getDouble("_itemId") != itemList.get(j).getmItemId()) {
                                j++;
                            }
                            itemList.get(j).setmQuantity((int) object.getDouble("_quantity"));
                        }
                        if(check == false) {
                            total_quantity += object.getDouble("_quantity");
                        }
                    }
                    check = true;

                } catch (Exception e) {
                    Log.e("StudentMainActivity", e.getMessage());
                }
            }
        });

        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("CategoryItemsActivity", e.getMessage());
        }
    }

}
