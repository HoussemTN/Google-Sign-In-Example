package com.brains404.google_sign_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    final int RC_SIGN_IN=404;
    TextView name ;
    TextView email ;
    TextView id ;
    ImageView profileImage ;
    GoogleSignInAccount acct;
    SignInButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.sign_in_button);

        name = findViewById(R.id.tv_username);
        email = findViewById(R.id.tv_email);
        id = findViewById(R.id.tv_user_id);
        profileImage = findViewById(R.id.im_profile_image);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (acct == null) {
                    signIn();

                }else {
                    signOut();

                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {

            updateUI(acct);
        }else{
            updateUI(null);
        }

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
           updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("test", "signInResult:failed code=" + e.getStatusCode());
           updateUI(null);
        }
    }

    public void updateUI(GoogleSignInAccount acct){
        if (acct!=null) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.connected_message),Toast.LENGTH_LONG).show();
            setGooglePlusButtonText(loginButton,getResources().getString(R.string.sign_out));
            name.setText(acct.getDisplayName());
            name.setVisibility(View.VISIBLE);

            email.setText(acct.getEmail());
            email.setVisibility(View.VISIBLE);

            id.setText(acct.getId());
            id.setVisibility(View.VISIBLE);
          //  Log.d("img",acct.getPhotoUrl()+"");
            Picasso.with(this).load(acct.getPhotoUrl()).fit().placeholder(R.mipmap.ic_launcher_round).into(profileImage);
            profileImage.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.disconnected_message),Toast.LENGTH_LONG).show();
            setGooglePlusButtonText(loginButton,getResources().getString(R.string.sign_in));
            id.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            profileImage.setVisibility(View.GONE);

        }
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }



}
