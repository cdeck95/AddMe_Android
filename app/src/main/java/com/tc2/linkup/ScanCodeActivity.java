package com.tc2.linkup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//import android.support.v4.app.Fragment;

public class ScanCodeActivity extends Fragment implements AdapterView.OnClickListener {

    private static final String TAG = "ScanCodeActivity";
    private Button scanBtn;
    private TextView tvScanFormat, tvScanContent;
    private LinearLayout llSearch;
    private Fragment fragment;
    private Context context;
    HttpURLConnection urlConnection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.scan_code_tab, container, false);

        fragment = this;

        scanBtn = rootView.findViewById(R.id.scan_button);
        tvScanFormat = rootView.findViewById(R.id.tvScanFormat);
        tvScanContent = rootView.findViewById(R.id.tvScanContent);
        llSearch = rootView.findViewById(R.id.llSearch);

        scanBtn.setOnClickListener(this);
        return rootView;
    }

    public void onClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        Intent i = integrator.createScanIntent();
        i.setAction(Intents.Scan.ONE_D_MODE);
        i.putExtra("RESULT_DISPLAY_DURATION_MS", 0L);
        startActivityForResult(i, IntentIntegrator.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                llSearch.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                new ScanCodeActivity.ScanProfile(getContext(), 92).execute();
                Bundle bundle = new Bundle();
                bundle.putInt("profileId", 92);
                Intent intent = new Intent(getActivity(), ScannedProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Log.e(TAG, "Finished:" + result.getContents());
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    String profileIdStr = jsonObject.getString("profileId");
                    Integer profileId = Integer.parseInt(profileIdStr);
                    //Log.e(TAG, "Account number: " + accounts.length());
//                    if (accounts.length() > 0) {
//                        for (int i = 0; i < accounts.length(); i++) {
//                            JSONObject tempObject = accounts.getJSONObject(i);
//                            //Log.e(TAG, "tempObject: " + tempObject.get("platform"));
//                            if (tempObject.getString("platform").equals("Facebook")) {
//                                String tempString = tempObject.getString("url");
//                                Log.e(TAG, "Facebook Link: " + Uri.parse("fb://facewebmodal/f?href=" + tempString));
//
//                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + tempString.toLowerCase()));
//                                startActivity(browserIntent);
//                            }
//                        }
//                    }
                    new ScanCodeActivity.ScanProfile(getContext(), profileId).execute();
                    Bundle bundle = new Bundle();
                    bundle.putInt("profileId", profileId);
                    Intent intent = new Intent(getActivity(), ScannedProfileActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } catch (JSONException j) {
                    j.printStackTrace();
                }
                llSearch.setVisibility(View.VISIBLE);
                tvScanContent.setText(result.getContents());
                tvScanFormat.setText(result.getFormatName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class ScanProfile extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        Integer profileId;


        public ScanProfile(Context c, Integer profileId) {
            mcontext = c;
            this.profileId = profileId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new MaterialDialog.Builder(mcontext)
//                    .title("Contacting Server")
//                    .content("Loading...")
//                    .progress(true, 0)
//                    .progressIndeterminateStyle(true)
//                    .show();
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject obj;
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/scans/" + profileId;

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");


                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "No profile Found.");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.d(TAG, "response buffer: " + builder.toString());

//                        obj = new JSONObject(builder.toString());
//                        JSONObject profileObject = obj.getJSONObject("profile");
//                        Gson gson = new Gson();
//                        profile = gson.fromJson(profileObject.toString(), Profile.class);
//                        Log.d(TAG, profile.getAccounts().toString());
                    } else {
                        Log.d(TAG, "No input stream");
                        return null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
