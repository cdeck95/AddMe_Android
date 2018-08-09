package tc2.addme.com.addme;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ProfileActivity extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ProfileActivity";
    ListView appList;
    SwipeRefreshLayout swipeRefreshLayout;
    private Switch appSwitch;
    private ArrayList<App> apps;
    private App app1, app2, app3, app4;
    private ImageButton imageButton;
    ProgressDialog mProgressDialog;
    HttpURLConnection urlConnection = null;
    BufferedReader reader;
    StringBuffer buffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.profile_tab, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        appList = (ListView) rootView.findViewById(R.id.appsListView);
        apps = new ArrayList<App>();
        appSwitch = (Switch) rootView.findViewById(R.id.appSwitch);
        populateApps(1, rootView);

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
            }
        });


        imageButton = (ImageButton) rootView.findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initializing a bottom sheet
                BottomSheetDialogFragment bottomSheetDialogFragment = new PersonalCodeBottomSheetFragment();

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
        ListAdapter adapter = new CustomAppsAdapter(getContext(), 0, apps);
        appList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        Log.d(TAG, "----------in group list view on click listener---------------");
        Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG).show();
    }

    private class Networking extends AsyncTask<Void, Void, Void> {
        String title;
        Context mcontext;

        public Networking(Context c){
              mcontext=c;
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

//        @Override
//        protected Void doInBackground(Void... params) {
//            //connect to API
//            JSONObject obj = null;
//          //  ArrayList<String> accounts = new ArrayList<>();
//            JSONArray accounts = new JSONArray();
//            String cognitoId = CredentialsManager.getInstance().getCognitoId();
//            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";
//
//            Log.d(TAG,  cognitoId);
//            try {
//                JSONObject jsonObject = new JSONObject("{}");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
////            parameters.put("cognitoId", cognitoId + "");
//            String postData = "{\"user\": {\"cognitoId\": \"" + cognitoId + "\"}}";
//            Log.d(TAG, postData);
//            Log.d(TAG, "----added get apps by user url---");
//            Log.d(TAG, "URL: " + urlIn);
//
//            URL url = null;     //path for connection
//            try {
//                url = new URL(urlIn);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            try {
//                urlConnection = (HttpURLConnection) url.openConnection();       //open the connection
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "----Opened Connection---");
//            try {
//                urlConnection.setRequestMethod("GET");
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            }
//            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//
//            //urlConnection.setRequestProperty("Content-Length", ""+Integer.toString(postData.getBytes().length));
//
//            urlConnection.setRequestProperty("Content-Language", "en-US");
//
//            urlConnection.setUseCaches(false);
//
//            urlConnection.setDoInput(false);
//            urlConnection.setDoOutput(true);
//
//            byte[] outputInBytes = new byte[0];
//            try {
//                outputInBytes = postData.getBytes("UTF-8");
//                Log.d(TAG, outputInBytes+"");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
////            OutputStream os = null;
////            try {
////                os = urlConnection.getOutputStream();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            try {
////                os.write( outputInBytes );
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            try {
////                os.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
////            try {
////                urlConnection.connect();        //finish the connection
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//            Log.d(TAG, "----Connection Successful----");
//            InputStream inputStream = null;
//            int status = 0;
//            try {
////                status = urlConnection.getResponseCode();
//                Log.e(TAG, "Response Code: " + status);
//                Log.e(TAG, "Response Message: " + urlConnection.getResponseMessage());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                if(status != HttpURLConnection.HTTP_BAD_REQUEST)
//                    inputStream = urlConnection.getErrorStream();
//                else
//                    inputStream = urlConnection.getInputStream();
//            } catch (FileNotFoundException f){
//                f.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "----reader----");
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            Log.d(TAG, "----Buffer----");
//            buffer = new StringBuffer();
//            Log.d(TAG, "----after Buffer----");
//            String line = "";
//           do {
//               try {
//                   line = reader.readLine();
//               } catch (IOException e) {
//                   e.printStackTrace();
//               }
//               buffer.append(line);
//           } while(line != null);
//
//            Log.d(TAG, "buffer: " + buffer.toString());
//            try {
//                obj = new JSONObject(buffer.toString());
//                if(obj.has("accounts")) {
//                    accounts = obj.getJSONArray("accounts");
//                }
//                if(obj.has("error")){
//                    Log.e(TAG, "Error:  " + obj.get("Error"));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            apps.clear();
//
//            for(int n = 0; n < accounts.length(); n++)
//            {
//                try {
//                    JSONObject object = accounts.getJSONObject(n);
//                    Integer id = -1;
//                    //Integer id = object.getInt("cognitoId");
//                    String displayName = object.getString("displayName");
//                    String appUrl = object.getString("url");
//                    String platform = object.getString("platform");
//                    App app = new App(id, displayName, platform, appUrl, Boolean.TRUE);
//                    apps.add(app);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            return null;
//        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection httpcon;
            String data = null;
            String result = null;
            String tempString = null;

            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String url = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/";

            Log.e(TAG, "Retrieve Data URL: " + url);
            try {
                //Connect
                httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "application/json");
                httpcon.setRequestMethod("GET");
                httpcon.connect();

                Log.e(TAG, "Response Number: " + httpcon.getResponseCode());
                Log.e(TAG, "Response Message: " + httpcon.getResponseMessage());

                if (httpcon.getResponseCode() != 404) {
                    Log.e(TAG, "doInBackground: " + httpcon.getInputStream());
                } else {
                    Log.e(TAG, "No Accounts found.");
                }

//                if (!empty) {
//                    //Read
//                    BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));
//
//                    String line = null;
//                    StringBuilder sb = new StringBuilder();
//
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line);
//                    }
//
//                    br.close();
//                    result = sb.toString();
//
//                    Log.e(TAG, "Results: " + result);
//                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //return url;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // populate list
            populateApps(1, getView());
            mProgressDialog.dismiss();
        }
    }


}
