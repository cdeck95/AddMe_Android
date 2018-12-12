package com.tc2.linkup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MyLinksActivity extends Fragment {

    private static final String TAG = "MAIN_ACTIVITY";
    ImageButton imageButton;
    private RecyclerView scansRecyclerView;
    private ArrayList<Scan> scansArray = new ArrayList<>();
    HttpURLConnection urlConnection = null;
    private FloatingActionButton addAppButton;
    private Integer selected = -1;
    private String cognitoId;
    SwipeRefreshLayout swipeRefreshLayout;
    private ScansAdapter adapter;
    String defaultImageURL = "https://images.pexels.com/photos/708440/pexels-photo-708440.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260";
    SwipeRefreshLayout pullToRefresh;
    private Scan scans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_links_tab, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayoutScans);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "----Refreshed----");
            new GetScans(getContext()).execute();
            swipeRefreshLayout.setRefreshing(false);
            final Snackbar snackBar = Snackbar.make(swipeRefreshLayout, "Refreshed", Snackbar.LENGTH_SHORT);
            snackBar.setAction("Dismiss", v -> snackBar.dismiss());
            snackBar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            snackBar.show();
        });
        scansRecyclerView = rootView.findViewById(R.id.scansRecyclerView);
        //scansRecyclerView.setHasFixedSize(true);
        scansRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new GetScans(getContext()).execute();

        return rootView;
    }

    private void populateScans() {
        adapter = new ScansAdapter(getContext(), getActivity(), scans);
        scansRecyclerView.setAdapter(adapter);
    }

    public class GetScans extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public GetScans(Context c) {
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
            cognitoId = CredentialsManager.getInstance().getCognitoId();

            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/scans/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");


                Log.e(TAG, "Response Code: " + urlConnection.getResponseCode());
                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(TAG, "Error in URL Connection");
                } else {
                    if (urlConnection.getInputStream() != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        StringBuilder builder = new StringBuilder();
                        while ((line = in.readLine()) != null) {
                            builder.append(line);
                        }

                        Log.d(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        Gson gson = new Gson();
                        scans = gson.fromJson(obj.toString(), Scan.class);
                        Log.d(TAG, scans.toString());
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
            // populate list
            populateScans();
            dialog.dismiss();
        }
    }
}
