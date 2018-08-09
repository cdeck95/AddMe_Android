package tc2.addme.com.addme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    EditText displayName;
    EditText username;



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
        ImageView facebookTV = (ImageView) contentView.findViewById(R.id.Facebook);
        ImageView twitterTV = (ImageView) contentView.findViewById(R.id.Twitter);
        ImageView instagramTV = (ImageView) contentView.findViewById(R.id.Instagram);

        facebookTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Facebook");
            }
        });

        twitterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Twitter");
            }
        });

        instagramTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog("Instagram");
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
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        final String platform = platformIn;
        // Set Custom Title
        TextView title = new TextView(getContext());
        // Title Properties
        title.setText("Add Me");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set Message
        displayName = new EditText(getApplicationContext());
        // Message Properties
        displayName.setHint("Display Name");
        displayName.setGravity(Gravity.CENTER_HORIZONTAL);
        displayName.setTextColor(Color.BLACK);

        // Set Message
        username = new EditText(getApplicationContext());
        // Message Properties
        username.setHint("Username");
        username.setGravity(Gravity.CENTER_HORIZONTAL);
        username.setTextColor(Color.BLACK);

        layout.addView(displayName);
        layout.addView(username);

        alertDialog.setView(layout);

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String userName = username.getText().toString();
                String display_name = displayName.getText().toString();
                Log.d("Add App", userName);
                Log.d("Add App", display_name);
                new Networking(getActivity(), userName, display_name, platform).execute();
            }
        });



        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.BLUE);
        okBT.setLayoutParams(neutralBtnLP);

        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
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

//    @Override
//    protected Void doInBackground(Void... params) {
//        //connect to API
//        JSONObject obj = null;
//        app.setDisplayName(displayName);
//        app.setPlatform(platform);
//        app.setUrl("http://facebook.com/" + userName);
//
//        String urlIn = "https://api.tc2pro.com/users";
//        //  ArrayList<String> accounts = new ArrayList<>();
//        JSONArray accounts = new JSONArray();
//        String cognitoId = CredentialsManager.getInstance().getCognitoId();
//        Log.d(TAG,  cognitoId);
//
//        String postData = "{\"user\": {\"cognitoId\": \"" + cognitoId + "\", \"displayName\": \"" + app.getDisplayName() + "\", \"platform\": \""
//                + platform + "\", \"url\": \"" + app.getUrl() + "\"}}";
//        Log.d(TAG, postData);
//        Log.d(TAG, "----added get apps by user url---");
//        Log.d(TAG, "URL: " + urlIn);
//
//        URL url = null;     //path for connection
//        try {
//            url = new URL(urlIn);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();       //open the connection
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "----Opened Connection---");
//        try {
//            urlConnection.setRequestMethod("POST");
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        }
//
//        urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//        urlConnection.setRequestProperty("Content-Length", ""+Integer.toString(postData.getBytes().length));
//        urlConnection.setRequestProperty("Content-Language", "en-US");
//        urlConnection.setUseCaches(false);
//        urlConnection.setDoInput(true);
//        urlConnection.setDoOutput(true);
//
//        byte[] outputInBytes = new byte[0];
//        try {
//            outputInBytes = postData.getBytes("UTF-8");
//            Log.d(TAG, outputInBytes+"");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        OutputStream os = null;
//        try {
//            os = urlConnection.getOutputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            os.write( outputInBytes );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            urlConnection.connect();        //finish the connection
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "----Connection Successful----");
//        InputStream inputStream = null;
//        int status = 0;
//        try {
//            status = urlConnection.getResponseCode();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            if(status >= HttpURLConnection.HTTP_BAD_REQUEST)
//                inputStream = urlConnection.getErrorStream();
//            else
//                inputStream = urlConnection.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "----reader----");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        Log.d(TAG, "----Buffer----");
//        StringBuffer buffer = new StringBuffer();
//        Log.d(TAG, "----after Buffer----");
//        String line = "";
//        do {
//            try {
//                line = reader.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            buffer.append(line);
//        } while(line != null);
//
//        Log.d(TAG, "buffer: " + buffer.toString());
//        try {
//            obj = new JSONObject(buffer.toString());
//            //accounts = obj.getJSONArray("accounts");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

     @Override
     protected Void doInBackground(Void... params) {
         HttpURLConnection httpcon;
         String data = null;
         String tempString = null;
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
                 tempObject.put("url", userName);
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
    }
}