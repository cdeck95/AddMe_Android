package com.tc2.linkup;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.droidbyme.dialoglib.DroidDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by cdeck_000 on 4/4/2017.
 */

public class CustomEditAppsAdapter extends ArrayAdapter<App> {

    private static final String TAG = "CustomGroupsAdapter";
    Integer selected;
    Integer appId;
    HttpURLConnection urlConnection = null;
    App singleApp;

    public CustomEditAppsAdapter(Context context, int resource, ArrayList<App> apps) {
        super(context, resource, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_edit_apps_rows, parent, false);

        singleApp = getItem(position);

        TextView txtAppDisplayName = customView.findViewById(R.id.txtAppDisplayName2);
        txtAppDisplayName.setText(singleApp.getDisplayName());

        Button editBtn = customView.findViewById(R.id.editBtn2);
        editBtn.setOnClickListener(v -> {
            singleApp = getItem(position);
            Log.d(TAG, singleApp.getDisplayName());
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("What would you like to do?");
            builder.addButton("Edit Account Display Name", -1, Color.parseColor("#429ef4"), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) -> {
                setSelected(0);
                editAccountName();
                dialog.dismiss();
            });
            builder.addButton("Edit Account Username", -1, Color.parseColor("#429ef4"), CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) -> {
                setSelected(1);
                editAccountUsername();
                dialog.dismiss();
            });
            builder.addButton("Delete Account", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) -> {
                setSelected(2);
                deleteAccount();
                dialog.dismiss();
            });

            // Show the alert
            builder.show();
        });

        TextView txtAppID = customView.findViewById(R.id.txtAppID2);
        appId = singleApp.getAccountId();
        txtAppID.setText(appId + "");

        ImageView appImage = customView.findViewById(R.id.imageView2);
        String platform = singleApp.getPlatform();

        switch (platform) {
            case "Facebook":
                appImage.setImageResource(R.mipmap.ic_facebook);
                break;
            case "Twitter":
                appImage.setImageResource(R.mipmap.ic_twitter);
                break;
            case "Instagram":
                appImage.setImageResource(R.mipmap.ic_instagram);
                break;
            case "Snapchat":
                appImage.setImageResource(R.mipmap.ic_snapchat);
                break;
            case "GooglePlus":
                appImage.setImageResource(R.mipmap.ic_google_plus);
                break;
            case "LinkedIn":
                appImage.setImageResource(R.mipmap.ic_linked_in);
                break;
            case "Xbox":
                appImage.setImageResource(R.mipmap.xbox);
                break;
            case "PSN":
                appImage.setImageResource(R.mipmap.ic_psn);
                break;
            case "Twitch":
                appImage.setImageResource(R.mipmap.ic_twitch);
                break;
            default:
                appImage.setImageResource(R.mipmap.ic_custom); //need custom
                break;
        }

        return customView;
    }

    private void deleteAccount() {
        new DroidDialog.Builder(getContext())
                .icon(R.drawable.ic_action_close)
                .title("Woah!")
                .content("You're about to delete all of your accounts. Are you sure you want to do that?")
                .cancelable(true, true)
                .positiveButton("CANCEL", droidDialog -> {
                    droidDialog.dismiss();
                })
                .negativeButton("DELETE", droidDialog -> {
                    droidDialog.dismiss();
                    new DeleteAccount(getContext()).execute();
                }).show();
    }

    private void editAccountName() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View dialogView = inflater.inflate(R.layout.custom_dialog_edit_account_name, null);
        dialogBuilder.setView(dialogView);

        final EditText nameIn = dialogView.findViewById(R.id.displayName);

        dialogBuilder.setTitle("Edit Profile");
        dialogBuilder.setMessage("Please update your account display name.");
        dialogBuilder.setPositiveButton("Update", (dialog, whichButton) -> {
            String name = nameIn.getText().toString().trim();
            Log.d(TAG, name);
            new EditAccount(getContext(), selected, name).execute();
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            //pass
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void editAccountUsername() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View dialogView = inflater.inflate(R.layout.custom_dialog_edit_account_username, null);
        dialogBuilder.setView(dialogView);

        final EditText usernameIn = dialogView.findViewById(R.id.username);

        dialogBuilder.setTitle("Edit Profile");
        dialogBuilder.setMessage("Please update your account username.");
        dialogBuilder.setPositiveButton("Update", (dialog, whichButton) -> {
            String username = usernameIn.getText().toString().trim();
            Log.d(TAG, username);
            new EditAccount(getContext(), selected, username).execute();
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

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    private class EditAccount extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        String newUsername;
        String newDisplayName;
        Integer selected;
        JSONObject tempObject = new JSONObject();


        public EditAccount(Context c, int selectedIn, String infoToBeUpdated) {
            mcontext = c;
            selected = selectedIn;
            if(selected == 1){
                newUsername = infoToBeUpdated;
            } else {
                newDisplayName = infoToBeUpdated;
            }
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
        protected Void doInBackground(String... params) {
            //connect to API
            JSONObject obj;
            JSONArray accounts = new JSONArray();
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/" + singleApp.getAccountId();

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                try {
                    if(selected == 1){
                        tempObject.put("username", newUsername);
                        switch (singleApp.getPlatform()) {
                            case "Facebook":
                                tempObject.put("url", "https://www.facebook.com/" + (newUsername));
                                break;
                            case "Twitter":
                                tempObject.put("url", "https://www.twitter.com/" + (newUsername));
                                break;
                            case "Instagram":
                                tempObject.put("url", "https://www.instagram.com/" + (newUsername));
                                break;
                            case "Snapchat":
                                tempObject.put("url", "https://www.snapchat.com/add/" + (newUsername));
                                break;
                            case "LinkedIn":
                                tempObject.put("url", "https://www.linkedin.com/in/" + (newUsername));
                                break;
                            case "GooglePlus":
                                tempObject.put("url", "https://www.plus.google.com/" + (newUsername));
                                break;
                            case "Xbox":
                                String usernameURL = URLEncoder.encode(newUsername, "utf-8");
                                tempObject.put("url", "https://account.xbox.com/en-us/Profile?GamerTag=" + (usernameURL));
                                break;
                            case "PSN":
                                usernameURL = URLEncoder.encode(newUsername, "utf-8");
                                tempObject.put("url", "https://my.playstation.com/profile/" + (usernameURL));
                                break;
                            case "Twitch":
                                tempObject.put("url", "https://m.twitch.tv/" + newUsername + "/profile");
                                break;
                            case "Custom":
                                tempObject.put("url", newUsername);
                                break;
                            default:
                                Log.d(TAG, "unknown app found: " + singleApp.getPlatform());
                        }
                        tempObject.put("displayName", singleApp.getDisplayName());

                    } else {
                        tempObject.put("username", singleApp.getUsername());
                        tempObject.put("displayName", newDisplayName);
                        tempObject.put("url", singleApp.getUrl());
                    }

                    tempObject.put("platform", singleApp.getPlatform());
                    Log.d(TAG, tempObject.toString());
                } catch (JSONException j) {
                    j.printStackTrace();
                }

                //Write
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(tempObject.toString());
                writer.close();
                os.close();

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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            notifyDataSetChanged();
            new DroidDialog.Builder(mcontext)
                    .icon(R.drawable.ic_action_tick)
                    .title("Success!")
                    .content("Your account has been updated.")
                    .cancelable(true, true)
                    .neutralButton("DISMISS", droidDialog -> {
                        droidDialog.dismiss();
                    }).show();
        }
    }

    private class DeleteAccount extends AsyncTask<String, Void, Void> {
        String title;
        Context mcontext;
        MaterialDialog dialog;
        JSONObject tempObject = new JSONObject();


        public DeleteAccount(Context c) {
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
        protected Void doInBackground(String... params) {
            //connect to API
            JSONObject obj;
            JSONArray accounts = new JSONArray();
            String cognitoId = CredentialsManager.getInstance().getCognitoId();
            String urlIn = "https://api.tc2pro.com/users/" + cognitoId + "/accounts/" + singleApp.getAccountId();

            Log.d(TAG, "Cognito ID: " + cognitoId);

            URL url = null;
            try {
                url = new URL(urlIn);
                Log.d(TAG, "URL: " + urlIn);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            notifyDataSetChanged();
            new DroidDialog.Builder(mcontext)
                    .icon(R.drawable.ic_action_tick)
                    .title("Success!")
                    .content("Your account has been deleted from the database.")
                    .cancelable(true, true)
                    .neutralButton("DISMISS", droidDialog -> {
                        droidDialog.dismiss();
                    }).show();
        }
    }
}
