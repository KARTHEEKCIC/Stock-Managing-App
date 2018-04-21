package com.example.android.autonomistock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.CartDO;
import com.amazonaws.models.nosql.ItemsDO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kartheek on 21/4/18.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyCartViewHolder> {

    private Context mContext;
    private ArrayList<CartDO> cartList;
    private TextView total;
    private double availability;

    public CartAdapter(Context mContext, ArrayList<CartDO> cartList, TextView total) {
        this.mContext = mContext;
        this.cartList = cartList;
        this.total = total;
    }

    @Override
    public MyCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_row, parent, false);
        return new MyCartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyCartViewHolder holder, int position) {
        holder.bind(cartList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyCartViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;
        public TextView quantity;
        public TextView plus;
        public TextView minus;

        public MyCartViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
            quantity = (TextView) view.findViewById(R.id.quantity);
            plus = (TextView) view.findViewById(R.id.plus);
            minus = (TextView) view.findViewById(R.id.minus);
        }

        public void bind(final CartDO item) {
            itemName.setText(item.getItemName());
            quantity.setText((item.getQuantity() + ""));
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cart_quantity = item.getQuantity()+1;
                    Log.e("CartAdapter", "" + cart_quantity);

                    Thread t = new Thread((new Runnable() {
                        @Override
                        public void run() {
                            DynamoDBMapper dynamoDBMapper = Utility.connectToDatabase(mContext);
                            DynamoDBQueryExpression query = new DynamoDBQueryExpression<ItemsDO>()
                                    .withHashKeyValues(new ItemsDO(item.getItemId()));

                            PaginatedList<CartDO> cart_result = dynamoDBMapper.query(ItemsDO.class, query);

                            Gson gson = new Gson();
                            StringBuilder stringBuilder = new StringBuilder();

                            String jsonFormOfItem = new String();

                            Log.e("CategoryItem", "Entered Function");
                            // Loop through query results
                            for (int i = 0; i < cart_result.size(); i++) {
                                jsonFormOfItem = gson.toJson(cart_result.get(i));
                                stringBuilder.append(jsonFormOfItem);
                                if (i != cart_result.size() - 1) {
                                    stringBuilder.append(",");
                                }
                            }

                            Log.e("StudentMainActivity", stringBuilder.toString());

                            try {
                                JSONObject ob = new JSONObject(stringBuilder.toString());
                                setAvailability(ob.getDouble("_availability"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }));

                    t.start();
                    try {
                        t.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(cart_quantity <= getAvailability()) {
                        quantity.setText(cart_quantity + "");
                        item.setQuantity(cart_quantity);
                        CategoryItemsActivity.total_quantity += 1;
                        total.setText(CategoryItemsActivity.total_quantity + "");
                        new Utility().writeToDatabase(mContext, item);
                    } else {
                        Toast.makeText(mContext,"Availability is short", Toast.LENGTH_LONG).show();
                    }

                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cart_quantity = item.getQuantity()-1;
                    CategoryItemsActivity.total_quantity -= 1;
                    total.setText(CategoryItemsActivity.total_quantity + "");
                    if(cart_quantity>0) {
                        quantity.setText(cart_quantity + "");
                        item.setQuantity(cart_quantity);
                        new Utility().writeToDatabase(mContext, item);
                    } else if(cart_quantity == 0) {
                        cartList.remove(item);
                        // delete the item from the cart database
                        new Utility().deleteFromDatabase(mContext, item);
                        StudentMainActivity.cartAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    double getAvailability () {
        return availability;
    }

    void setAvailability(double availability) {
        this.availability = availability;
    }
}
