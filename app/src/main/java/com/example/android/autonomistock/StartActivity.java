package com.example.android.autonomistock;

import android.content.Intent;
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
