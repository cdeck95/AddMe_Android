package tc2.addme.com.addme;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.droidbyme.dialoglib.DroidDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {


    private static final String TAG = "AddAppActivity";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.dialog_modal, null);
        dialog.setContentView(contentView);
        ImageView facebook = (ImageView) contentView.findViewById(R.id.Facebook);
        ImageView twitter = (ImageView) contentView.findViewById(R.id.Twitter);
        ImageView instagram = (ImageView) contentView.findViewById(R.id.Instagram);
        ImageView snapchat = (ImageView) contentView.findViewById(R.id.Snapchat);
        ImageView xbox = (ImageView) contentView.findViewById(R.id.xbox);
        ImageView psn = (ImageView) contentView.findViewById(R.id.psn);
        ImageView twitch = (ImageView) contentView.findViewById(R.id.twitch);
        ImageView custom = (ImageView) contentView.findViewById(R.id.custom);
        ImageView googlePlus = (ImageView) contentView.findViewById(R.id.googlePlus);
        ImageView linkedIn = (ImageView) contentView.findViewById(R.id.LinkedIn);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Facebook");
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Twitter");
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Instagram");
            }
        });

        snapchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Snapchat");
            }
        });

        googlePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("GooglePlus");
            }
        });

        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("LinkedIn");
            }
        });
        xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Xbox");
            }
        });

        psn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("PSN");
            }
        });

        twitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Twitch");
            }
        });

        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Custom");
            }
        });

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
//        facebookTV.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.facebook_icon), null, null, null);
//        twitterTV.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.facebook_icon), null, null, null);

    }

    private void launchAlertDialog(String platformIn) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText name = (EditText) dialogView.findViewById(R.id.displayName);
        final EditText email = (EditText) dialogView.findViewById(R.id.username);
        //final EditText message = (EditText) dialogView.findViewById(R.id.customFeedback);

        dialogBuilder.setTitle("Add Account");
        dialogBuilder.setMessage("Please enter details...");
        dialogBuilder.setPositiveButton("Add", (dialog, whichButton) -> {
            String displayName = name.getText().toString().trim();
            String username = email.getText().toString();
           // String messageStr = message.getText().toString().trim();
            Log.d(TAG, displayName);
            Log.d(TAG, username);
            new Networking(getActivity(), username, displayName, platformIn).execute();

        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    }

 class Networking extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        String userName, displayName, platform;
        App app;
        ProgressDialog mProgressDialog;
        private static final String TAG = "Networking_AddApp";
        HttpURLConnection urlConnection = null;


    public Networking(Context c, String userName, String display_name, String platform){
        mcontext=c;
        this.userName = userName;
        this.displayName = display_name;
        this.platform = platform;
        app = new App();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mcontext);
        mProgressDialog.setTitle("Contacting Server");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

     @Override
     protected Void doInBackground(Void... params) {
         HttpURLConnection httpcon;
         JSONObject tempObject = new JSONObject();

         String cognitoId = CredentialsManager.getInstance().getCognitoId();
         String url = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

         Log.e(TAG, "Retrieve Data URL: " + url);
         try {
             //Connect
             httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
             httpcon.setRequestProperty("Content-Type", "application/json");
             httpcon.setRequestProperty("Accept", "application/json");
             httpcon.setRequestMethod("POST");
             httpcon.connect();

             //Make JSON
//             {
//                  "displayName": "string",
//                  "platform": "string",
//                  "url": "string"
//             }
             try {
                 tempObject.put("username", userName);
                 tempObject.put("displayName", displayName);
                 tempObject.put("platform", platform);
                 switch(platform) {
                     case "Facebook":
                        tempObject.put("url",  "https://www.facebook.com/" + (userName));
                        break;
                     case "Twitter":
                         tempObject.put("url",  "https://www.twitter.com/" + (userName));
                         break;
                     case "Instagram":
                         tempObject.put("url",  "https://www.instagram.com/" + (userName));
                         break;
                     case "Snapchat":
                         tempObject.put("url",  "https://www.snapchat.com/add/" + (userName));
                         break;
                     case "LinkedIn":
                         tempObject.put("url",  "https://www.linkedin.com/in/" + (userName));
                         break;
                     case "GooglePlus":
                         tempObject.put("url",  "https://www.plus.google.com/" + (userName));
                         break;
                     case "Xbox":
                         String usernameURL = URLEncoder.encode(userName, "utf-8");
                         tempObject.put("url",  "https://account.xbox.com/en-us/Profile?GamerTag=" + (usernameURL));
                         break;
                     case "PSN":
                         usernameURL = URLEncoder.encode(userName, "utf-8");
                         tempObject.put("url",  "https://my.playstation.com/profile/" + (usernameURL));
                         break;
                     case "Twitch":
                         tempObject.put("url",  "https://m.twitch.tv/" + userName + "/profile");
                         break;
                     case "Custom":
                         tempObject.put("url", userName);
                         break;
                     default:
                         Log.d(TAG, "unknown app found: " + platform);
                 }

                 tempObject.put("isSwitchOn", true);
                 Log.d(TAG, tempObject.toString());
             } catch (JSONException j) {
                 j.printStackTrace();
             }


             //Write
             OutputStream os = httpcon.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
             writer.write(tempObject.toString());
             writer.close();
             os.close();

             Log.e(TAG, "Response Number: " + httpcon.getResponseCode());
             Log.e(TAG, "Response Body: " + httpcon.getResponseMessage());


             if (httpcon.getResponseCode() != 404) {
                 Log.e(TAG, "doInBackground: " + httpcon.getInputStream());
             } else {
             }


         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return null;
     }

    @Override
    protected void onPostExecute(Void result) {
        // populate list
        mProgressDialog.dismiss();
        Log.d(TAG, "App added: " + app.toString());
        new DroidDialog.Builder(mcontext)
                .icon(R.drawable.ic_action_tick)
                .title("Success!")
                .content("Your account has been added to the database.")
                .cancelable(true, true)
                .neutralButton("DISMISS", droidDialog -> {
                    droidDialog.dismiss();
                    Toast.makeText(mcontext, "Skip", Toast.LENGTH_SHORT).show();
                })
                .show();

    }


}