package tc2.addme.com.addme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.SignInStateChangeListener;
import com.amazonaws.mobile.auth.facebook.FacebookButton;
import com.amazonaws.mobile.auth.google.GoogleButton;
import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;


public class AuthenticatorActivity extends Activity {

    private static final String TAG = "AUTHENTICATOR_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        // Add a call to initialize AWSMobileClient
//        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
//            @Override
//            public void onComplete(AWSStartupResult awsStartupResult) {
//                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
//                signin.login(AuthenticatorActivity.this, MainActivity.class).execute();
//            }
//        }).execute();

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
//                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
//                signin.login(AuthenticatorActivity.this, MainActivity.class).execute();
                AuthUIConfiguration config =
                    new AuthUIConfiguration.Builder()
                            .userPools(true)  // true? show the Email and Password UI
                            .signInButton(FacebookButton.class) // Show Facebook button
                            .signInButton(GoogleButton.class) // Show Google button
    //                        .logoResId(R.drawable.mylogo) // Change the logo
                            .backgroundColor(Color.GRAY) // Change the backgroundColor
//                            .isBackgroundColorFullScreen(true) // Full screen backgroundColor the backgroundColor full screenff
                            .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                            .canCancel(true)
                            .build();
                SignInUI signinUI = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
                signinUI.login(AuthenticatorActivity.this, MainActivity.class).authUIConfiguration(config).execute();
            }
        }).execute();

        // Sign-in listener
        IdentityManager.getDefaultIdentityManager().addSignInStateChangeListener(new SignInStateChangeListener() {
            @Override
            public void onUserSignedIn() {
                Log.d(TAG, "User Signed In");
            }

            // Sign-out listener
            @Override
            public void onUserSignedOut() {

                Log.d(TAG, "User Signed Out");
                showSignIn();
            }
        });

        showSignIn();

    }

    /*
     * Display the AWS SDK sign-in/sign-up UI
     */
    private void showSignIn() {
        SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
        signin.login(AuthenticatorActivity.this, MainActivity.class).execute();
    }
}