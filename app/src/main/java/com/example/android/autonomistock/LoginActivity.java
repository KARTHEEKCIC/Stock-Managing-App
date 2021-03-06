package com.example.android.autonomistock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // for aws web services
    private CognitoUserPool userPool;
    private CognitoUserAttributes userAttributes;
    private String clientId = "1ki0dem2n9h71fjj3mj047nkdp";
    private String clientSecret = "n4jm2ipcnffc1ds7camgc3ck12pj107e83v0il6i9puvgsfllrg";
    private String poolId = "ap-south-1_ZYrq6thxG";

    // Cognito User object representing a user in the user pool
    CognitoUser mCognitoUser;

    // Confirmation code sent to the email
    String confirmationCode;

    // to avoid any alias already existing in the api from being confirmed
    boolean forcedAliasCreation = false;

    //MFA Verification code for aws
    String mfaVerificationCode = "00000";

    // User's Login Credentials
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Client configuration for aws services
        ClientConfiguration clientConfiguration = new ClientConfiguration();

        AmazonCognitoIdentityProviderClient identityProviderClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), new ClientConfiguration());
        identityProviderClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

        // object for the user pool access with id given above
        userPool = new CognitoUserPool(getApplicationContext(), poolId, clientId, clientSecret, identityProviderClient);

        mCognitoUser = userPool.getUser();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // set the parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if(!checkPasswordConstraints(password)) {
            mPasswordView.setError("Password should contain an lower case character, uppercase character" +
                    ", number and special character");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // Sign in the user
            mCognitoUser.getSessionInBackground(authenticationHandler);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    // check whether the password is according the constraints
    private boolean checkPasswordConstraints(String password) {
        if(password.matches("^[a-zA-Z0-9!@#$&()\\\\-`.+,/\\\"]*$")) {
            return true;
        }
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    // Callback handler for confirmSignUp API
    GenericHandler confirmationCallback = new GenericHandler() {

        @Override
        public void onSuccess() {
            // User was successfully confirmed
            // Now the save the status that the user has logged in
            SharedPreferences sharedPreferences = getApplicationContext().
                    getSharedPreferences(getString(R.string.login_status), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userEmail", email);
            editor.putBoolean("loggedIn",true);
            editor.commit();

            // Now go the Student Main Page
            Intent StudentMainActivity = new Intent(getApplicationContext(),
                    com.example.android.autonomistock.StudentMainActivity.class);
            if (StudentMainActivity.resolveActivity(getPackageManager()) != null) {
                startActivity(StudentMainActivity);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // User confirmation failed. Check exception for the cause.
            Log.e("RegisterActivity.class","Failed to confirm with exception - " + exception);
            if(exception.getClass() == CodeMismatchException.class) {
                // Inform the user that the code entered is incorrect
                Toast.makeText(getApplicationContext(),"Incorrect Code. Please enter the correct code",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"Some Network Error. Please Try after some time",Toast.LENGTH_LONG).show();
            }
        }
    };


    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {

        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            Log.e("LoginActivity.class", "Login Successfull");

            // Now the save the status that the user has logged in
            Log.e("LoginActivity","Setting the login status");
            SharedPreferences sharedPreferences = getApplicationContext().
                    getSharedPreferences(getString(R.string.login_status), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("loggedIn",true);
            editor.putString("userEmail",email);
            editor.commit();

            Intent StudentMainActivity = new Intent(getApplicationContext(), com.example.android.autonomistock.StudentMainActivity.class);
            if (StudentMainActivity.resolveActivity(getPackageManager()) != null) {
                startActivity(StudentMainActivity);
            }
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(email, password, null);

            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);

            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            Log.e("LoginActivity.class","GetMFACode called");
            // Multi-factor authentication is required; get the verification code from user
            multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            multiFactorAuthenticationContinuation.continueTask();
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
            Log.e("LoginActivity.class","Login Failed - " + exception);

            // check whether the user is confirmed or not
            if(exception.getClass() == UserNotConfirmedException.class) {

                VerificationHandler mVerificationHandler = new VerificationHandler() {
                    @Override
                    public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                        Log.e("LoginActivity.class","Verification Code Sent Successfully");
                        setContentView(R.layout.activity_verification);

                        // PIN Edit Text
                        PinEntryEditText verificationPin;

                        verificationPin = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
                        // Set listener to get the verification code from user
                        verificationPin.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                            @Override
                            public void onPinEntered(CharSequence str) {
                                confirmationCode = str.toString();
                                // Call API to confirm this user
                                userPool.getUser(email).confirmSignUpInBackground(confirmationCode,forcedAliasCreation,confirmationCallback);
                            }
                        });


                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("LoginActivity.class","Some Network Error - " + exception);
                    }
                };

                userPool.getUser(email).resendConfirmationCodeInBackground(mVerificationHandler);
            } else if (exception.getClass() == UserNotFoundException.class) {
                showProgress(false);
                Toast.makeText(getApplicationContext(),"User does not exist",Toast.LENGTH_SHORT).show();
            } else if (exception.getClass() == NotAuthorizedException.class) {
                showProgress(false);
                Toast.makeText(getApplicationContext(),"Incorrect Password",Toast.LENGTH_SHORT).show();
            }

        }
    };

}

