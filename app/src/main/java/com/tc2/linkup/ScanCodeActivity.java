package com.tc2.linkup;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.droidbyme.dialoglib.DroidDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import static android.app.Activity.RESULT_OK;

//import android.support.v4.app.Fragment;

public class ScanCodeActivity extends Fragment implements AdapterView.OnClickListener {

    private static final String TAG = "ScanCodeActivity";
    private Button scanBtn;
    private TextView tvScanFormat, tvScanContent;
    private LinearLayout llSearch;
    private Fragment fragment;
    private Context context;
    HttpURLConnection urlConnection;
    TextView tvData;
    Button btnQRScan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.scan_code_tab, container, false);

        fragment = this;

        btnQRScan = rootView.findViewById(R.id.btnQRScan);
        btnQRScan.setOnClickListener(this);

        Intent intent=new Intent(rootView.getContext(), QRScanner.class);
//                intent.putExtra(EasyQR.IS_TOOLBAR_SHOW,true);
//                intent.putExtra(EasyQR.TOOLBAR_DRAWABLE_ID,R.drawable.ic_audiotrack_dark);
//                intent.putExtra(EasyQR.TOOLBAR_TEXT,"My QR");
//                intent.putExtra(EasyQR.TOOLBAR_BACKGROUND_COLOR,"#0588EE");
//                intent.putExtra(EasyQR.TOOLBAR_TEXT_COLOR,"#FFFFFF");
//                intent.putExtra(EasyQR.BACKGROUND_COLOR,"#000000");
//                intent.putExtra(EasyQR.CAMERA_MARGIN_LEFT,50);
//                intent.putExtra(EasyQR.CAMERA_MARGIN_TOP,50);
//                intent.putExtra(EasyQR.CAMERA_MARGIN_RIGHT,50);
//                intent.putExtra(EasyQR.CAMERA_MARGIN_BOTTOM,50);
        intent.putExtra(EasyQR.CAMERA_BORDER,100);
        intent.putExtra(EasyQR.CAMERA_BORDER_COLOR,"#C1000000");
        intent.putExtra(EasyQR.IS_SCAN_BAR,true);
        intent.putExtra(EasyQR.IS_BEEP,true);
        intent.putExtra(EasyQR.BEEP_RESOURCE_ID,R.raw.beep);
        startActivityForResult(intent, EasyQR.QR_SCANNER_REQUEST);

        return rootView;
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnQRScan: {
                Intent intent=new Intent(v.getContext(), QRScanner.class);
//                intent.putExtra(EasyQR.IS_TOOLBAR_SHOW,true);
//                intent.putExtra(EasyQR.TOOLBAR_DRAWABLE_ID,R.drawable.ic_audiotrack_dark);
//                intent.putExtra(EasyQR.TOOLBAR_TEXT,"My QR");
//                intent.putExtra(EasyQR.TOOLBAR_BACKGROUND_COLOR,"#0588EE");
//                intent.putExtra(EasyQR.TOOLBAR_TEXT_COLOR,"#FFFFFF");
//                intent.putExtra(EasyQR.BACKGROUND_COLOR,"#000000");
//                intent.putExtra(EasyQR.CAMERA_MARGIN_LEFT,50);
//                intent.putExtra(EasyQR.CAMERA_MARGIN_TOP,50);
//                intent.putExtra(EasyQR.CAMERA_MARGIN_RIGHT,50);
//                intent.putExtra(EasyQR.CAMERA_MARGIN_BOTTOM,50);
                intent.putExtra(EasyQR.CAMERA_BORDER,100);
                intent.putExtra(EasyQR.CAMERA_BORDER_COLOR,"#C1000000");
                intent.putExtra(EasyQR.IS_SCAN_BAR,true);
                intent.putExtra(EasyQR.IS_BEEP,true);
                intent.putExtra(EasyQR.BEEP_RESOURCE_ID,R.raw.beep);
                startActivityForResult(intent, EasyQR.QR_SCANNER_REQUEST);
            } break;
        }
//        IntentIntegrator integrator = new IntentIntegrator(getActivity());
//        Intent i = integrator.createScanIntent();
//        i.setAction(Intents.Scan.ONE_D_MODE);
//        i.putExtra("RESULT_DISPLAY_DURATION_MS", 0L);
//        startActivityForResult(i, IntentIntegrator.REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EasyQR.QR_SCANNER_REQUEST: {
                if (resultCode==RESULT_OK){
                   // tvData.setText(data.getStringExtra(EasyQR.DATA));
                    try {
                        JSONObject jsonObject = new JSONObject(data.getStringExtra(EasyQR.DATA));
                        Integer profileId = Integer.parseInt(jsonObject.getString("profileId"));
                        new ScanCodeActivity.ScanProfile(getContext(), profileId).execute();
                        Bundle bundle = new Bundle();
                        bundle.putInt("profileId", profileId);
                        Intent intent = new Intent(getActivity(), ScannedProfileActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        new DroidDialog.Builder(getContext())
                                .icon(R.drawable.ic_action_close)
                                .title("Hmm")
                                .content("The QR code doesn't seem right. Is it a LinkUp created QR code?")
                                .cancelable(true, true)
                                .neutralButton("DISMISS", droidDialog -> {
                                    droidDialog.dismiss();
                                }).show();
                    }

                }
            } break;
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
