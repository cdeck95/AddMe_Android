package com.tc2.linkup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoAccessToken;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.droidbyme.dialoglib.DroidDialog;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class HomeActivity extends Fragment {

    private static final String TAG = "HOME_ACTIVITY";
    ImageButton imageButton;
    PublisherAdView mPublisherAdView;

    private static final int NUM_PAGES = 5;
    private SectionPageAdapter mSectionsPagerAdapter;
    public ViewPager mPager;
    public PagerAdapter mPagerAdapter;

    private ArrayList<Profile> profilesArray = new ArrayList<>();
    HttpURLConnection urlConnection = null;
    private FloatingActionButton addAppButton;
    private Integer selected = -1;
    private String cognitoId;
    private static final int SELECT_PHOTO = 100;
    String defaultImageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
    private ArrayList<App> allAccounts = new ArrayList<>();
    SwipeRefreshLayout pullToRefresh;
    ImageView profileImage;
    // private JSONArray allAccounts = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_home, container, false);

        setCognitoId();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        new GetProfiles(rootView.getContext()).execute();
        new GetAllAccounts(rootView.getContext()).execute();

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(getContext(), R.color.GColor2),
                        ContextCompat.getColor(getContext(), R.color.GColor2)});

        rootView.findViewById(R.id.background).setBackground(gradientDrawable);
        profileImage = rootView.findViewById(R.id.profileImage);

        profileImage.setOnClickListener(v -> {
            Intent photoPic = new Intent(Intent.ACTION_PICK);
            photoPic.setType("image/*");
            startActivityForResult(photoPic, SELECT_PHOTO);
        });

        Permissions();


        addAppButton = rootView.findViewById(R.id.fab);


        //AWSMobileClient.getInstance().initialize(this).execute();

        mSectionsPagerAdapter = new SectionPageAdapter(getFragmentManager());

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            // Create Alert using Builder
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("What would you like to add?");
            builder.setSingleChoiceItems(new String[]{"Profile", "Account"}, 3, (dialogInterface, index) ->
            {
                setSelected(index);
            });
            builder.addButton("DONE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialogInterface, i) ->
            {
                if(selected == 1){
                    openBottomSheet();
                } else {
                    addProfile();
                }
                dialogInterface.dismiss();
            });

            // Show the alert
            builder.show();
            //openBottomSheet();
        });




        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            //textView.setText(personName);
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            //Picasso.with(getApplicationContext()).load(personPhoto).into(imageView);
        }

//        mPublisherAdView = findViewById(R.id.publisherAdView);
//        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB").build();
//        mPublisherAdView.loadAd(adRequest);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = rootView.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setVisibility(View.GONE);

        pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        //setting an setOnRefreshListener on the SwipeDownLayout
        pullToRefresh.setOnRefreshListener(() -> {
            Log.e(TAG, "Refeshing...");
            pullToRefresh.setRefreshing(true);
            new GetProfiles(getContext()).execute();
            mPagerAdapter.notifyDataSetChanged();
            pullToRefresh.setRefreshing(false);
        });

        mPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float v, int i1 ) {
            }

            @Override
            public void onPageSelected( int position ) {
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
                enableDisableSwipeRefresh( state == ViewPager.SCROLL_STATE_IDLE );
            }
        } );


        Button customButton = new Button(getContext());
        customButton.setText("");
        customButton.setEnabled(false);
        customButton.setVisibility(View.GONE);
//
//        BottomNavigationViewEx bnve = rootView.findViewById(R.id.navigation);
//        bnve.enableAnimation(false);
//        bnve.enableShiftingMode(false);
//        bnve.enableItemShiftingMode(true);
//
//        // set adapter
//        adapter = new VpAdapter(getFragmentManager(), fragments);
//        bind.vp.setAdapter(adapter);
//
//        // binding with ViewPager
//        bind.bnve.setupWithViewPager(bind.vp);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
//doing some uri parsing
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        //getting the image
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    //decoding bitmap
                    Bitmap bMap = BitmapFactory.decodeStream(imageStream);
                    profileImage.setImageBitmap(bMap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    new POSTImage(getContext(), imageString).execute();
                }
        }
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (pullToRefresh != null) {
            pullToRefresh.setEnabled(enable);
        }
    }

    private void addProfile() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_profiles, null);
        dialogBuilder.setView(dialogView);

        final EditText nameIn = dialogView.findViewById(R.id.displayName);
        final EditText descriptionIn = dialogView.findViewById(R.id.username);
        //final EditText message = (EditText) dialogView.findViewById(R.id.customFeedback);

        dialogBuilder.setTitle("Add Profile");
        dialogBuilder.setMessage("Please enter details...");
        dialogBuilder.setPositiveButton("Add", (dialog, whichButton) -> {
            String name = nameIn.getText().toString().trim();
            String description = descriptionIn.getText().toString();
            // String messageStr = message.getText().toString().trim();
            Log.d(TAG, name);
            Log.d(TAG, description);
            new AddProfile(getContext(), getActivity(), name, description).execute();

        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            //pass
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setSelected(Integer selectedIn){
        this.selected = selectedIn;
    }

    private void setCognitoId(){
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getContext(),
                getResources().getString(R.string.pool_id), // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        new SetAccessToken(credentialsProvider).execute();
    }

//    @Override
//    public void onBackPressed() {
//        if (mPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//        }
//    }

    private void openBottomSheet() {

        //Initializing a bottom sheet
        BottomSheetDialogFragment bottomSheetDialogFragment = new CustomBottomSheetDialogFragment();

        //show it
        bottomSheetDialogFragment.show(getFragmentManager(), bottomSheetDialogFragment.getTag());

    }



    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("profileId", profilesArray.get(position).getProfileId());
            args.putString("profileImageUrl", profilesArray.get(position).getImageUrl());
            args.putString("profileName", profilesArray.get(position).getName());
            args.putString("profileDescription", profilesArray.get(position).getDescription());
            args.putParcelableArrayList("accounts", profilesArray.get(position).getAccounts());
            args.putParcelableArrayList("allAccounts", allAccounts);
            screenSlidePageFragment.setArguments(args);
            return screenSlidePageFragment;
        }

        @Override
        public int getCount() {
            //return NUM_PAGES;
            return profilesArray.size();
        }
    }

    public class GetProfiles extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public GetProfiles(Context c) {
            mcontext = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //connect to API
            JSONObject obj = null;
            JSONArray profiles = new JSONArray();
            cognitoId = CredentialsManager.getInstance().getCognitoId();
            if(cognitoId == null){
                setCognitoId();
                cognitoId = CredentialsManager.getInstance().getCognitoId();
            }
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", CredentialsManager.getInstance().getAccessToken());

                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "No Profiles Found.");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.e(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        profiles = obj.getJSONArray("profiles");
                        //prefs.edit().putString("accounts", builder.toString()).apply();

                    } else {
                        Log.d(TAG, "No input stream");
                        return null;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    getActivity().runOnUiThread(() -> {
                            new DroidDialog.Builder(mcontext)
                                    .icon(R.drawable.ic_action_close)
                                    .title("Uh-oh!")
                                    .content("Are you connected to the internet?")
                                    .cancelable(true, true)
                                    .neutralButton("DISMISS", droidDialog -> {
                                        droidDialog.dismiss();
                                    }).show();
                    });

                }
//                Log.e(TAG, e.getLocalizedMessage());
//                Log.e(TAG, e.getMessage());
//                Log.e(TAG, e.getCause()+"");
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            if(profilesArray != null){
                profilesArray.clear();
            }

            for (int n = 0; n < profiles.length(); n++) {
                try {
                    String profileString = profiles.getString(n);
                    Log.d(TAG, profileString);
                    Profile profile = new Gson().fromJson(profileString, Profile.class);
                    Log.d(TAG, profile.toString());
                    profilesArray.add(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // populate list
            //TODO:Create populateProfiles
            // populateProfiles(1, getView());
            //mProgressDialog.dismiss();
            getActivity().runOnUiThread(() -> {
                mPager.setAdapter(mPagerAdapter);
                mPager.setVisibility(View.VISIBLE);
                mSectionsPagerAdapter.notifyDataSetChanged();
                mPagerAdapter.notifyDataSetChanged();
            });
            dialog.dismiss();
        }
    }

    public class SetAccessToken extends AsyncTask<Void, Void, Void> {
        String title;
        CognitoCachingCredentialsProvider credentialsProvider;
        MaterialDialog dialog;


        public SetAccessToken(CognitoCachingCredentialsProvider credentialsProviderIn) {
            credentialsProvider = credentialsProviderIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
//            CognitoUserSession session = new CognitoUserSession()
//            String accessToken = session.getAccessToken().getJWT();
//            String idToken = session.getIdToken().getJWTToken();
            Log.d(TAG, credentialsProvider.getIdentityId() + "");
            cognitoId = credentialsProvider.getIdentityId();
            CredentialsManager.getInstance().setCognitoId(cognitoId);
            CognitoUserPool userPool = new CognitoUserPool(getContext(), getResources().getString(R.string.pool_id), "3oq4oaic2j4ar9dajsajsrcqeh", "1g4nhom8d3k4jm6fm020ing6keff61gqk20jksg1940uec3o2rb4");
            CognitoUser cognitoUser = userPool.getCurrentUser();
            cognitoUser.getSessionInBackground(new AuthenticationHandler() {
                @Override
                public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                        String idToken = userSession.getIdToken().getJWTToken();
                        Log.e(TAG, idToken);
                        CredentialsManager.getInstance().setAccessToken(idToken);
                        Log.e(TAG, CredentialsManager.getInstance().getAccessToken());
//                        Map<String, String> logins = new HashMap<String, String>();
//                        logins.put("cognito-idp.<region>.amazonaws.com/<YOUR_USER_POOL_ID>", session.getIdToken().getJWTToken());
//                        credentialsProvider.setLogins(logins);
                }

                @Override
                public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                    Log.e(TAG, "getAuthenticationDetails");
                }

                @Override
                public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                    Log.e(TAG, "getMFACode");
                }

                @Override
                public void authenticationChallenge(ChallengeContinuation continuation) {
                    Log.e(TAG, "authenticationChallenge");
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.e(TAG, "onFailure");
                }
            });
//            CredentialsManager.getInstance().setAccessToken(IdentityManager.getDefaultIdentityManager().getCurrentIdentityProvider().getToken());
//            Log.e(TAG, credentialsProvider.getToken());
//            Log.e(TAG, credentialsProvider.getCredentials().getSessionToken());
//            Log.e(TAG, credentialsProvider.getToken());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    public class POSTImage extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        String imageString;
        JSONObject postParams;
        String presignedURL;

        public POSTImage(Context c, String imageStringIn) {
            mcontext = c;
            imageString = imageStringIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection httpcon;
            JSONObject tempObject = new JSONObject();

            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String url = "https://api.tc2pro.com/users/" + cognitoId + "/images/";

            Log.e(TAG, "Retrieve Data URL: " + url);
            try {
                //Connect
                httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestProperty("Authorization", CredentialsManager.getInstance().getAccessToken());
                httpcon.setRequestMethod("POST");
                httpcon.connect();

//                try {
//                    tempObject.put("username", bitmap);
//                    Log.d(TAG, tempObject.toString());
//                } catch (JSONException j) {
//                    j.printStackTrace();
//                }


                //Write
                OutputStream os = httpcon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(tempObject.toString());
                writer.close();
                os.close();

                Log.e(TAG, "Response Number: " + httpcon.getResponseCode());
                Log.e(TAG, "Response Body: " + httpcon.getResponseMessage());


                if (httpcon.getResponseCode() == 404) {
                    Log.e(TAG, "doInBackground: " + httpcon.getInputStream().toString());
                } else {
                    if (httpcon.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }
                        Log.e(TAG, "response buffer: " + builder.toString());
                        JSONObject jsonObject = new JSONObject(builder.toString());
                        postParams = jsonObject.getJSONObject("fields");
                        postParams.put("file", imageString);
                        Log.d(TAG, "base 64 encoded bitmap: "  + imageString);
                        presignedURL = jsonObject.getString("url");
                    }
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (UnknownHostException e){
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    getActivity().runOnUiThread(() -> {
                        new DroidDialog.Builder(mcontext)
                                .icon(R.drawable.ic_action_close)
                                .title("Uh-oh!")
                                .content("Are you connected to the internet?")
                                .cancelable(true, true)
                                .neutralButton("DISMISS", droidDialog -> {
                                    droidDialog.dismiss();
                                }).show();
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            // populate list
            //TODO:Create populateProfiles
            // populateProfiles(1, getView());
            //mProgressDialog.dismiss();
            getActivity().runOnUiThread(() -> {
                mPager.setAdapter(mPagerAdapter);
                mPager.setVisibility(View.VISIBLE);
            });
            dialog.dismiss();
            new POSTImageToAWS(getContext(), presignedURL, postParams).execute();
        }
    }

    public class POSTImageToAWS extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        JSONObject postParams;
        String presignedURL;

        public POSTImageToAWS(Context c, String presignedURLIn, JSONObject postParamsIn) {
            mcontext = c;
            postParams = postParamsIn;
            presignedURL = presignedURLIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection httpcon;
            JSONObject tempObject = new JSONObject();

            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String url = presignedURL;

            Log.e(TAG, "Presigned URL: " + url);
            try {
                //Connect
                httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestMethod("POST");
                httpcon.setRequestProperty("http.keepAlive", "true");
                httpcon.setDoOutput(true);

                //Write
                OutputStream os = httpcon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postParams.toString());
                Log.d(TAG, postParams.toString());
                Log.d(TAG, postParams.getString("file"));
                writer.flush();
                writer.close();
                os.close();
                httpcon.connect();

                Log.e(TAG, "Response Number: " + httpcon.getResponseCode());
                Log.e(TAG, "Response Body: " + httpcon.getResponseMessage());
                Log.e(TAG, "Other: " + httpcon.toString());


                if (httpcon.getResponseCode() == 404) {
                    Log.e(TAG, "doInBackground: " + httpcon.getInputStream().toString());
                } else {
                    if (httpcon.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }
                        Log.e(TAG, "response buffer: " + builder.toString());
                    }
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (UnknownHostException e){
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    getActivity().runOnUiThread(() -> {
                        new DroidDialog.Builder(mcontext)
                                .icon(R.drawable.ic_action_close)
                                .title("Uh-oh!")
                                .content("Are you connected to the internet?")
                                .cancelable(true, true)
                                .neutralButton("DISMISS", droidDialog -> {
                                    droidDialog.dismiss();
                                }).show();
                    });

                }
            } catch(AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            // populate list
            //TODO:Create populateProfiles
            // populateProfiles(1, getView());
            //mProgressDialog.dismiss();
            getActivity().runOnUiThread(() -> {
                mPager.setAdapter(mPagerAdapter);
                mPager.setVisibility(View.VISIBLE);
            });
            dialog.dismiss();
        }
    }

    private class AddProfile extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        String profileName;
        String profileDescription;

        public AddProfile(Context c, Activity a, String nameIn, String descriptionIn) {
            mcontext = c;
            profileName = nameIn;
            profileDescription = descriptionIn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();

//              mProgressDialog = new ProgressDialog(mcontext);
//              mProgressDialog.setTitle("Contacting Server");
//              mProgressDialog.setMessage("Loading...");
//              mProgressDialog.setIndeterminate(false);
//              mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //connect to API
            JSONObject obj = new JSONObject();
            JSONObject tempObject = new JSONObject();
            JSONArray profiles = new JSONArray();
            cognitoId = CredentialsManager.getInstance().getCognitoId();
            if(cognitoId == null){
                setCognitoId();
                cognitoId = CredentialsManager.getInstance().getCognitoId();
            }
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/profiles/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", CredentialsManager.getInstance().getAccessToken());
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();


                try {
                    tempObject.put("name", profileName);
                    tempObject.put("description", profileDescription);
                    tempObject.put("imageUrl", defaultImageURL);
                    Log.d(TAG, allAccounts.toString());
                    JSONArray arrayOfIDs = new JSONArray();
                    for(int i = 0; i< allAccounts.size(); i++){
                        arrayOfIDs.put(allAccounts.get(i).getAccountId());
                    }
                    tempObject.putOpt("accounts", arrayOfIDs);
                    Log.d(TAG, tempObject.toString());
                } catch (JSONException j) {
                    j.printStackTrace();
                }

                try {
                    //Write
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(tempObject.toString());
                    writer.close();
                    os.close();

                    Log.e(TAG, "Response Number: " + urlConnection.getResponseCode());
                    Log.e(TAG, "Response Body: " + urlConnection.getResponseMessage());


                    if (urlConnection.getResponseCode() != 404) {
                        Log.e(TAG, "doInBackground: " + urlConnection.getInputStream());
                    } else {
                        if (urlConnection.getInputStream() != null) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String line;
                            StringBuilder builder = new StringBuilder();
                            while ((line = in.readLine()) != null) {
                                builder.append(line);
                            }

                            Log.e(TAG, "response buffer: " + builder.toString());

                            obj = new JSONObject(builder.toString());
                            profiles = obj.getJSONArray("profiles");
                           // mPagerAdapter.notifyDataSetChanged();
                            //prefs.edit().putString("accounts", builder.toString()).apply();

                        } else {
                            Log.d(TAG, "No input stream");
                            return null;
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e){
                    Log.e(TAG, e.getMessage());
                    if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                        getActivity().runOnUiThread(() -> {
                            new DroidDialog.Builder(mcontext)
                                    .icon(R.drawable.ic_action_close)
                                    .title("Uh-oh!")
                                    .content("Are you connected to the internet?")
                                    .cancelable(true, true)
                                    .neutralButton("DISMISS", droidDialog -> {
                                        droidDialog.dismiss();
                                    }).show();
                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            if(profilesArray != null){
                profilesArray.clear();
            }

            for (int n = 0; n < profiles.length(); n++) {
                try {
                    String profileString = profiles.getString(n);
                    Log.d(TAG, profileString);
                    Profile profile = new Gson().fromJson(profileString, Profile.class);
                    Log.d(TAG, profile.toString());
                    profilesArray.add(profile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            mPagerAdapter.notifyDataSetChanged();
            mSectionsPagerAdapter.notifyDataSetChanged();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // populate list
            //TODO:Create populateProfiles
            // populateProfiles(1, getView());
            //mProgressDialog.dismiss();
            getActivity().runOnUiThread(() -> {
                mPager.setAdapter(mPagerAdapter);
                mPager.setVisibility(View.VISIBLE);
                mPagerAdapter.notifyDataSetChanged();
                mSectionsPagerAdapter.notifyDataSetChanged();
            });
            new GetProfiles(mcontext).execute();
            dialog.dismiss();
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        Log.d(TAG, result.toString());
        return result.toString();
    }

    private void Permissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }
        }
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        0);
            }
        }
    }

    private class GetAllAccounts extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public GetAllAccounts(Context c) {
            mcontext = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new MaterialDialog.Builder(mcontext)
                    .title("Contacting Server")
                    .content("Loading...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .show();

//              mProgressDialog = new ProgressDialog(mcontext);
//              mProgressDialog.setTitle("Contacting Server");
//              mProgressDialog.setMessage("Loading...");
//              mProgressDialog.setIndeterminate(false);
//              mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //connect to API
            JSONObject obj = null;
            JSONArray accounts = new JSONArray();
            cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", CredentialsManager.getInstance().getAccessToken());

                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "No accounts Found.");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.e(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        accounts = obj.getJSONArray("accounts");


                    } else {
                        Log.d(TAG, "No input stream");
                        return null;
                    }
                }
            } catch (UnknownHostException e){
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    getActivity().runOnUiThread(() -> {
                        new DroidDialog.Builder(mcontext)
                                .icon(R.drawable.ic_action_close)
                                .title("Uh-oh!")
                                .content("Are you connected to the internet?")
                                .cancelable(true, true)
                                .neutralButton("DISMISS", droidDialog -> {
                                    droidDialog.dismiss();
                                }).show();
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            allAccounts.clear();

            for (int n = 0; n < accounts.length(); n++) {
                try {
                    JSONObject object = accounts.getJSONObject(n);
                    Integer id = Integer.parseInt(object.getString("accountId"));
                    String displayName = object.getString("displayName");
                    String appUrl = object.getString("url");
                    String platform = object.getString("platform");
                    String username = object.getString("username");
                    App app = new App(id, displayName, platform, appUrl, username);
                    allAccounts.add(app);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }
}
