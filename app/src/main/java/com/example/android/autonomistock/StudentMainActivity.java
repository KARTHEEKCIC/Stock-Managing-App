package com.example.android.autonomistock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.CartDO;
import com.amazonaws.models.nosql.CategoriesDO;
import com.amazonaws.models.nosql.CountDO;
import com.amazonaws.models.nosql.IssuesDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.android.autonomistock.R.drawable.issue;


public class StudentMainActivity extends AppCompatActivity {

    // Declare a DynamoDBMapper object
    DynamoDBMapper dynamoDBMapper;
    private BottomNavigationView bottomNavigationView;
    FrameLayout cart_container;
    FrameLayout settings_container;
    FrameLayout catalogue_container;
    FrameLayout issue_container;
    Button Logout;
    TextView mNoItems;
    RelativeLayout cartContent;
    TextView Issue;
    TextView total;

    TextView noIssues;

    ArrayList<CartDO> cart;
    ArrayList<IssuesDO> issuesList;

    static int count;

    static CartAdapter cartAdapter;
    IssuesAdapter issuesAdapter;

    RecyclerView recyclerView;

    RecyclerView issueRecyclerView;

    private ArrayList<CategoryHolder> mCategories;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        cart = new ArrayList<>();
        issuesList = new ArrayList<>();

        mCategories = new ArrayList<CategoryHolder>();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        cart_container = (FrameLayout) findViewById(R.id.cart_container);
        settings_container = (FrameLayout) findViewById(R.id.settings_container);
        catalogue_container = (FrameLayout) findViewById(R.id.catalogue_container);
        recyclerView = (RecyclerView) findViewById(R.id.categoryitems);
        issueRecyclerView = (RecyclerView) findViewById(R.id.issues);
        Logout = (Button) findViewById(R.id.logout);
        mNoItems = (TextView) findViewById(R.id.no_items);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        total = (TextView) findViewById(R.id.total);
        Issue = (TextView) findViewById(R.id.issue_request);
        noIssues = (TextView) findViewById(R.id.no_issues);
        issue_container = (FrameLayout) findViewById(R.id.issues_container);

        Issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: We need to perform the network operation to send the issue request to the admin
                Toast.makeText(getApplicationContext(),"Your Request has been sent. We will contact you soon",Toast.LENGTH_LONG).show();
                SharedPreferences sharedPreferences = getApplicationContext().
                        getSharedPreferences(getString(R.string.login_status), Context.MODE_PRIVATE);

                StringBuilder Description = new StringBuilder();

                int j = 0;
                while(j<cart.size()) {
                    Description.append(cart.get(j).getItemName());
                    Description.append("-");
                    Description.append(cart.get(j).getQuantity()+"");
                    if(j != cart.size()-1) {
                        Description.append(", ");
                    }
                    j++;
                }

                getCount();
                IssuesDO issue = new IssuesDO();
                issue.setIssueId(count);
                issue.setUserEmail(sharedPreferences.getString("userEmail",null));
                issue.setDescription(Description.toString());
                new Utility().writeToDatabase(StudentMainActivity.this,issue);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // storing the login status in the shared preferences
                SharedPreferences sharedPreferences = getApplicationContext().
                        getSharedPreferences(getString(R.string.login_status), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn",false);
                editor.commit();

                // going to the start activity
                Intent startActivityIntent = new Intent(getApplicationContext(),StartActivity.class);
                if (startActivityIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(startActivityIntent);
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // handle desired action here
                // One possibility of action is to replace the contents above the nav bar
                // return true if you want the item to be displayed as the selected item
                if(item.getItemId() == R.id.navigation_cart) {
                    showCart();
                } else if(item.getItemId() == R.id.navigation_catalogue){
                    showCatalogue();
                } else if(item.getItemId() == R.id.navigation_settings) {
                    showSettings();
                } else if(item.getItemId() == R.id.navigation_issue) {
                    showIssue();
                }

                return true;
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gridview);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent categoryItem = new Intent(getApplicationContext(),CategoryItemsActivity.class);
                categoryItem.putExtra("catId",mCategories.get(position).getCategoryId());
                if (categoryItem.resolveActivity(getPackageManager()) != null) {
                    startActivity(categoryItem);
                }
            }
        });

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        // Instantiate a AmazonDynamoDBMapperClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        categoriesRead();

        gridview.setAdapter(new CategoryAdapter(this,mCategories));

        if(getIntent().getIntExtra("position",-1) == 1) {
            Log.e("StudentMainAct.class", "View Activity Called");
            showCart();
        }
    }

    void showSettings() {
        cart_container.setVisibility(View.GONE);
        catalogue_container.setVisibility(View.GONE);
        issue_container.setVisibility(View.GONE);
        settings_container.setVisibility(View.VISIBLE);
    }

    void showCatalogue() {
        cart_container.setVisibility(View.GONE);
        settings_container.setVisibility(View.GONE);
        issue_container.setVisibility(View.GONE);
        catalogue_container.setVisibility(View.VISIBLE);
    }

    void showIssue() {

        catalogue_container.setVisibility(View.GONE);
        settings_container.setVisibility(View.GONE);
        cart_container.setVisibility(View.GONE);

        // perform the network operation here
        // fetch the issues of the current user from the database
        fillIssues();

        if(issuesList.isEmpty()) {
            Log.e("StudentMainActivity", "Your Issues List is Empty");
            noIssues.setVisibility(View.VISIBLE);
        } else {
            Log.e("StudentMainActivity", "Your Issues List is not Empty");
            issueRecyclerView.setVisibility(View.VISIBLE);
        }

        issue_container.setVisibility(View.VISIBLE);

        issuesAdapter = new IssuesAdapter(StudentMainActivity.this,issuesList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //Set divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(issueRecyclerView.getContext(),DividerItemDecoration.VERTICAL);
        issueRecyclerView.addItemDecoration(dividerItemDecoration);
        issueRecyclerView.setLayoutManager(mLayoutManager);
        //Give animation
        issueRecyclerView.setItemAnimator(new DefaultItemAnimator());
        issueRecyclerView.setAdapter(issuesAdapter);
    }

    void showCart() {
        catalogue_container.setVisibility(View.GONE);
        issue_container.setVisibility(View.GONE);
        settings_container.setVisibility(View.GONE);

        // perform the network operation here
        // fetch the cart items of the current user from the database
        fillCart();

        if(cart.isEmpty()) {
            Log.e("StudentMainActivity", "Your Cart is Empty");
            mNoItems.setVisibility(View.VISIBLE);
        } else {
            Log.e("StudentMainActivity", "Your cart is not Empty");
            total.setText("" + CategoryItemsActivity.total_quantity);
            cartContent.setVisibility(View.VISIBLE);
        }

        cart_container.setVisibility(View.VISIBLE);

        Log.e("StudentMainActivity",cart + "");

        cartAdapter = new CartAdapter(StudentMainActivity.this, cart, total);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //Set divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(mLayoutManager);
        //Give animation
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cartAdapter);

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
    }

    public void categoriesRead() {
       Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                DynamoDBScanExpression queryExpression = new DynamoDBScanExpression();

                PaginatedList<CategoriesDO> result = dynamoDBMapper.scan(
                        com.amazonaws.models.nosql.CategoriesDO.class,queryExpression);

                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();

                String jsonFormOfItem = new String();

                stringBuilder.append("[");

                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    jsonFormOfItem = gson.toJson(result.get(i));
                    stringBuilder.append(jsonFormOfItem);
                    if(i != result.size()-1) {
                        stringBuilder.append(",");
                    }
                }

                stringBuilder.append("]");

                try {
                    JSONArray array = new JSONArray (stringBuilder.toString());
                    for(int i=0 ; i<array.length() ; i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        Log.e("StudentMainActivity", object.getString("_catName").toString());
                        mCategories.add(new CategoryHolder(object.getString("_catName"),object.getInt("_catId")));
                    }

                } catch (Exception e) {
                    Log.e("StudentMainActivity",e.getMessage());
                }

            }
        });

        t.start();
        try {
            t.join();
        } catch (Exception e) {
          Log.e("StudentMainActivity",e.getMessage());
        }
    }

    void fillCart() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences emailSharedPref = StudentMainActivity.this.getSharedPreferences(StudentMainActivity.this.getString(R.string.login_status)
                        , Context.MODE_PRIVATE);

                DynamoDBQueryExpression query = new DynamoDBQueryExpression<CartDO>()
                        .withHashKeyValues(new CartDO(emailSharedPref.getString("userEmail", null), null, null, null,null));

                PaginatedList<CartDO> cart_result = dynamoDBMapper.query(CartDO.class, query);

                if (cart_result.isEmpty()) {
                    return;
                }

                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();

                String jsonFormOfItem = new String();

                stringBuilder.append("[");

                Log.e("CategoryItem", "Entered Function");
                // Loop through query results
                for (int i = 0; i < cart_result.size(); i++) {
                    jsonFormOfItem = gson.toJson(cart_result.get(i));
                    stringBuilder.append(jsonFormOfItem);
                    if (i != cart_result.size() - 1) {
                        stringBuilder.append(",");
                    }
                }

                stringBuilder.append("]");

                Log.e("StudentMainActivity", stringBuilder.toString());

                try {
                    JSONArray array = new JSONArray(stringBuilder.toString());
                    boolean item_check = false;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        Log.e("StudentMainActivity", object.getString("_itemName").toString());
                        int j=0;

                        while(j<cart.size()) {
                            if(cart.get(j).getItemId() == object.getDouble("_itemId")) {
                                cart.get(j).setQuantity(object.getInt("_quantity"));
                                item_check = true;
                                break;
                            }
                            j++;
                        }
                        if(item_check == false) {
                            cart.add(new CartDO(object.getString("_userEmail"), object.getDouble("_itemId"),
                                    object.getDouble("_catId"), object.getString("_itemName"), object.getInt("_quantity")));
                            if (CategoryItemsActivity.check == false) {
                                CategoryItemsActivity.total_quantity += object.getDouble("_quantity");
                            }
                        }
                        item_check = false;
                    }
                    CategoryItemsActivity.check = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("StudentMainActivity", e.getMessage());
        }
    }

    void getCount() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                PaginatedList<CountDO> result = dynamoDBMapper.scan(
                        com.amazonaws.models.nosql.CountDO.class,scanExpression);
                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();

                String jsonFormOfItem = new String();

                Log.e("CategoryItem", "Entered Function");
                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    jsonFormOfItem = gson.toJson(result.get(i));
                    stringBuilder.append(jsonFormOfItem);
                    if (i != result.size() - 1) {
                        stringBuilder.append(",");
                    }
                }

                Log.e("StudentMainActivity", stringBuilder.toString());

                try {
                    JSONObject ob = new JSONObject(stringBuilder.toString());
                    count = ob.getInt("_value");
                    count++;
                    Log.e("StudentMainActivity",count+"");
                } catch (Exception e) {

                }

                CountDO mCount = new CountDO();
                mCount.setCount("count");
                mCount.setValue(count);
                new Utility().writeToDatabase(StudentMainActivity.this,mCount);
            }
        });

        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("StudentMainActivity", e.getMessage());
        }
    }

    void fillIssues() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences emailSharedPref = StudentMainActivity.this.getSharedPreferences(StudentMainActivity.this.getString(R.string.login_status)
                        , Context.MODE_PRIVATE);

                Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();

                map.put(":email", new AttributeValue().withS(emailSharedPref.getString("userEmail", null)));

                DynamoDBScanExpression query = new DynamoDBScanExpression().withFilterExpression("userEmail = :email").withExpressionAttributeValues(map);

                PaginatedList<IssuesDO> result = dynamoDBMapper.scan(IssuesDO.class, query);

                if (result.isEmpty()) {
                    return;
                }

                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();

                String jsonFormOfItem = new String();

                stringBuilder.append("[");

                Log.e("CategoryItem", "Entered Function");
                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    jsonFormOfItem = gson.toJson(result.get(i));
                    stringBuilder.append(jsonFormOfItem);
                    if (i != result.size() - 1) {
                        stringBuilder.append(",");
                    }
                }

                stringBuilder.append("]");

                Log.e("StudentMainActivity", stringBuilder.toString());

                try {
                    JSONArray array = new JSONArray(stringBuilder.toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
//                        Log.e("StudentMainActivity", object.getString("_itemName").toString());
                        int j=0;
                        while(j<issuesList.size()) {
                            if(issuesList.get(j).getIssueId() == object.getInt("_issueId")){
                                break;
                            } else {
                                j++;
                            }
                        }
                        if(j == issuesList.size()) {
                            issuesList.add(new IssuesDO(object.getString("_userEmail"), object.getString("_description"), object.getInt("_issueId")));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("StudentMainActivity", e.getMessage());
        }
    }
}

