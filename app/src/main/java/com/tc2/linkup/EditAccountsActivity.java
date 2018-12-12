package com.tc2.linkup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.droidbyme.dialoglib.DroidDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EditAccountsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "EditAccountsActivity";
    ListView appList;
    SwipeRefreshLayout swipeRefreshLayout;
    HttpURLConnection urlConnection = null;
    private Button editBtn, deleteAllBtn;
    private ArrayList<App> apps;
    private Integer selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_accounts);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2);
        appList = findViewById(R.id.appsListView2);
        apps = new ArrayList<>();


        deleteAllBtn = findViewById(R.id.deleteAllBtn2);
        deleteAllBtn.setOnClickListener(v -> {
            new DroidDialog.Builder(this)
                    .icon(R.drawable.ic_action_close)
                    .title("Woah!")
                    .content("You're about to delete all of your accounts. Are you sure you want to do that?")
                    .cancelable(true, true)
                    .positiveButton("CANCEL", droidDialog -> {
                        droidDialog.dismiss();
                    })
                    .negativeButton("DELETE", droidDialog -> {
                        droidDialog.dismiss();
                        new EditAccountsActivity.Networking(this).execute("DELETE");
                    }).show();
        });


        appList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (appList == null || appList.getChildCount() == 0) ?
                                0 : appList.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "----Refreshed----");
            new Networking(this).execute("GET");
            //populateApps(1, rootView);
            swipeRefreshLayout.setRefreshing(false);
            final Snackbar snackBar = Snackbar.make(swipeRefreshLayout, "Refreshed", Snackbar.LENGTH_SHORT);
            snackBar.setAction("Dismiss", v -> snackBar.dismiss());
            snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            snackBar.show();
        });

        new EditAccountsActivity.Networking(this).execute("GET");

        return;
    }


    private void setSelected(Integer selectedIn){
        this.selected = selectedIn;
    }

    private void populateApps(int i, View v) {
        if (apps.size() == 0) {
            Log.d(TAG, "Apps list is empty");
            appList.setVisibility(View.INVISIBLE);
        } else {
            appList.setVisibility(View.VISIBLE);
            ListAdapter adapter = new CustomEditAppsAdapter(this, 0, apps);
            appList.setAdapter(adapter);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "On Item Click method params: " + position + " " + id);
    }

    private class Networking extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        String request;


        public Networking(Context c) {
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
        protected Void doInBackground(String... params) {
            //connect to API
            request = params[0];
            JSONObject obj = null;
            JSONArray accounts = new JSONArray();
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(request);


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

                        Log.d(TAG, "response buffer: " + builder.toString());

                        obj = new JSONObject(builder.toString());
                        accounts = obj.getJSONArray("accounts");
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

            apps.clear();

            for (int n = 0; n < accounts.length(); n++) {
                try {
                    JSONObject object = accounts.getJSONObject(n);
                    Integer id = Integer.parseInt(object.getString("accountId"));
                    String displayName = object.getString("displayName");
                    String appUrl = object.getString("url");
                    String platform = object.getString("platform");
                    String username = object.getString("username");
                    App app = new App(id, displayName, platform, appUrl, username);
                    apps.add(app);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // populate list
            populateApps(1, swipeRefreshLayout);
            //mProgressDialog.dismiss();
            dialog.dismiss();
            if(request == "DELETE"){
                new DroidDialog.Builder(mcontext)
                        .icon(R.drawable.ic_action_tick)
                        .title("Success!")
                        .content("All of your accounts have been deleted from the database.")
                        .cancelable(true, true)
                        .neutralButton("DISMISS", droidDialog -> {
                            droidDialog.dismiss();
                        }).show();
            }

        }
    }
}
