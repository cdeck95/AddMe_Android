package tc2.addme.com.addme;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ProfileActivity extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ProfileActivity";
    ListView appList;
    SwipeRefreshLayout swipeRefreshLayout;
    private Switch appSwitch;
    private ArrayList<App> apps;
    private ImageButton imageButton;
    //ProgressDialog mProgressDialog;
    HttpURLConnection urlConnection = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.profile_tab, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        appList = (ListView) rootView.findViewById(R.id.appsListView);
        apps = new ArrayList<App>();
        appSwitch = (Switch) rootView.findViewById(R.id.appSwitch);


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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "----Refreshed----");
                new Networking(getContext()).execute();
                //populateApps(1, rootView);
                swipeRefreshLayout.setRefreshing(false);
                final Snackbar snackBar = Snackbar.make(getView(), "Refreshed", Snackbar.LENGTH_SHORT);
                snackBar.setAction("Dismiss", v -> snackBar.dismiss());
                snackBar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                snackBar.show();
            }
        });


        imageButton = (ImageButton) rootView.findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initializing a bottom sheet
                BottomSheetDialogFragment bottomSheetDialogFragment = new PersonalCodeBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("apps", apps);
                bottomSheetDialogFragment.setArguments(bundle);
                //show it
                FragmentManager fm = getFragmentManager();
                bottomSheetDialogFragment.show(fm, bottomSheetDialogFragment.getTag());
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Networking(getContext()).execute();
    }

    private void populateApps(int i, View v) {
        if(apps.size() == 0){
            Log.d(TAG, "Apps list is empty");
        } else {
            ListAdapter adapter = new CustomAppsAdapter(getContext(), 0, apps);
            appList.setAdapter(adapter);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "On Item Click method params: " +  position + " " + id);
    }

    private class Networking extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;


        public Networking(Context c){
              mcontext=c;
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
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

            Log.d(TAG,  "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                apps.clear();

                for(int n = 0; n < accounts.length(); n++)
                {
                    try {
                        JSONObject object = accounts.getJSONObject(n);
                        Integer id = Integer.parseInt(object.getString("accountId"));
                        String displayName = object.getString("displayName");
                        String appUrl = object.getString("url");
                        String platform = object.getString("platform");
                        String username = object.getString("username");
                        App app = new App(id, displayName, platform, appUrl, username, Boolean.TRUE);
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
            populateApps(1, getView());
            //mProgressDialog.dismiss();
            dialog.dismiss();

        }
    }


}


