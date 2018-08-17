package tc2.addme.com.addme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.support.v4.app.Fragment;

public class ScanCodeActivity extends Fragment implements AdapterView.OnClickListener {

    private static final String TAG = "ScanCodeActivity";
    private Button scanBtn;
    private TextView tvScanFormat, tvScanContent;
    private LinearLayout llSearch;
    private Fragment fragment;
    private Context context;

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
            } else {
                Log.e(TAG, "Finished:" + result.getContents());
                try {
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    JSONArray accounts = jsonObject.getJSONArray("accounts");
                    //Log.e(TAG, "Account number: " + accounts.length());
                    if (accounts.length() > 0) {
                        for (int i = 0; i < accounts.length(); i++) {
                            JSONObject tempObject = accounts.getJSONObject(i);
                            //Log.e(TAG, "tempObject: " + tempObject.get("platform"));
                            if (tempObject.getString("platform").equals("Facebook")) {
                                Log.e(TAG, "Facebook Link: " + Uri.parse(tempObject.getString("url").toLowerCase()));

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tempObject.getString("url").toLowerCase()));
                                startActivity(browserIntent);
                            }
                        }
                    }
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
}
