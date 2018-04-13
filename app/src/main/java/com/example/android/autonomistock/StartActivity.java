package com.example.android.autonomistock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button signIn;
    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Shared Preferences file for storing the status of the users login
        // that is it will store a value indicating whether the user has logged in or not
        SharedPreferences sharedPreferences = getApplicationContext().
                getSharedPreferences(getString(R.string.login_status), Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("loggedIn",false)) {
            Intent MainActivityIntent = new Intent(getApplicationContext(), StudentMainActivity.class);
            if (MainActivityIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(MainActivityIntent);
            }
        }

        // find the layout elements
        signIn = (Button) findViewById(R.id.sign_in);
        createAccount = (Button) findViewById(R.id.create_an_account);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(getApplicationContext(), LoginActivity.class);
                if (signInIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(signInIntent);
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccount = new Intent(getApplicationContext(), RegisterActivity.class);
                if (createAccount.resolveActivity(getPackageManager()) != null) {
                    startActivity(createAccount);
                }
            }
        });

    }

}
