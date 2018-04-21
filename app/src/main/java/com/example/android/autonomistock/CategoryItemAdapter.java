package com.example.android.autonomistock;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.CartDO;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kartheek on 15/4/18.
 */

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.MyViewHolder> {
    private List<CategoryItem> list;
    private Context mContext;
    private LinearLayout viewCart;
    private TextView cart_quantity;
    private double mQuantity;
    private int catId;
    public CategoryItemAdapter(List<CategoryItem> newsList, Context mContext, LinearLayout viewCart,
                               TextView cart_quantity, int catId) {
        this.list = newsList;
        this.mContext = mContext;
        this.viewCart = viewCart;
        this.cart_quantity = cart_quantity;
        this.catId = catId;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(list.get(position));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemAvailability, plus, minus, Quantity;
        public View mainView;
        public MyViewHolder(View view) {
            super(view);
            mainView = view;
            itemName = (TextView) view.findViewById(R.id.itemName);
            itemAvailability = (TextView) view.findViewById(R.id.itemAvailability);
            Quantity = (TextView) view.findViewById(R.id.quantity);
            plus = (TextView) view.findViewById(R.id.plus);
            minus = (TextView) view.findViewById(R.id.minus);
        }

        double parseJson(PaginatedList<CartDO> result) {

            double quantity = 0;
            Log.e("CategoryItemAdapter", "Parsing the json");
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

            Log.e("CategoryItemAdapter",stringBuilder.toString());

            try {
                JSONArray array = new JSONArray (stringBuilder.toString());
                for(int i=0 ; i<array.length() ; i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    Log.e("StudentMainActivity", object.getString("_catId").toString());
                    quantity+=object.getDouble("_quantity");
                }

            } catch (Exception e) {
                Log.e("StudentMainActivity",e.getMessage());
            }
            return quantity;
        }

        public void bind(final CategoryItem item) {
            itemName.setText(item.getmItemName());
            itemAvailability.setText("Availability-"+item.getmAvailability());
            Quantity.setText( (int) item.getmQuantity() + "");
            plus.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    TextView quantityView = (TextView) mainView.findViewById(R.id.quantity);

                    int quantity = Integer.parseInt(quantityView.getText().toString());
                    quantity++;

                    if(item.getmAvailability() >= quantity) {

                        // Saving the item added to the cart database
                        final SharedPreferences emailSharedPref = mContext.getSharedPreferences(mContext.getString(R.string.login_status)
                                , Context.MODE_PRIVATE);

                        CartDO cart = new CartDO();
                        cart.setUserEmail(emailSharedPref.getString("userEmail", null));
                        cart.setItemId((double) item.getmItemId());
                        cart.setQuantity(quantity);
                        cart.setItemName(itemName.getText().toString());
                        cart.setCatId((double) catId);

                        new Utility().writeToDatabase(mContext, cart);

                        quantityView.setText(quantity + "");

                        CategoryItemsActivity.total_quantity += 1;

                        cart_quantity.setText("" + (int) CategoryItemsActivity.total_quantity);
                        viewCart.setVisibility(View.VISIBLE);
                    } else {
                        // as availability is not as much as requirement
                        // so donot add
                        Toast.makeText(mContext,"Required quantity is higher than availability",Toast.LENGTH_LONG).show();
                    }
                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView quantityView = (TextView) mainView.findViewById(R.id.quantity);
                    int quantity = Integer.parseInt(quantityView.getText().toString());
                    quantity--;
                    if(quantity>=0) {

                        // Saving the item deleted to the cart database
                        SharedPreferences emailSharedPref = mContext.getSharedPreferences(mContext.getString(R.string.login_status)
                                , Context.MODE_PRIVATE);

                        CartDO cart = new CartDO();
                        cart.setUserEmail(emailSharedPref.getString("userEmail", null));
                        cart.setItemId((double) item.getmItemId());
                        cart.setQuantity(quantity);
                        cart.setItemName(itemName.getText().toString());
                        cart.setCatId((double) catId);

                        if(quantity == 0) {
                            // delete the item from the cart database
                            new Utility().deleteFromDatabase(mContext,cart);
                        } else {

                            new Utility().writeToDatabase(mContext, cart);
                        }

                        quantityView.setText(quantity + "");

                        CategoryItemsActivity.total_quantity -= 1;

                        if(CategoryItemsActivity.total_quantity !=0) {
                            cart_quantity.setText("" + (int) CategoryItemsActivity.total_quantity);
                            viewCart.setVisibility(View.VISIBLE);
                        } else {
                            // remove the view cart option
                            viewCart.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

}
