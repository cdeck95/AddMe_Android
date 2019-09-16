package com.tc2.linkup;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoAccessToken;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.droidbyme.dialoglib.DroidDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Random;

public class PersonalCodeBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "PersonalCodeBottomSheet";
    SharedPreferences prefs;
    EditText displayName;
    EditText username;
    ImageButton shareButton;
    ImageView imageView;
    ImageButton refreshButton;
    View contentView;
    String cognitoId;
    Integer profileId;
    // Uri for image path
    private static Uri imageUri = null;
    private Integer selected;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(getContext(), R.layout.personal_code_modal, null);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        Bundle args = getArguments();
        profileId = args.getInt("profileId", -1);
        cognitoId = CredentialsManager.getInstance().getCognitoId();
        imageView =  contentView.findViewById(R.id.qrCode);

        if (imageView != null) {
            new DownloadImageWithURLTask(imageView).execute( "https://api.tc2pro.com/users/" + cognitoId + "/profiles/" + profileId + "/qr");
        }
        Log.e(TAG, IdentityManager.getDefaultIdentityManager().getCachedUserID());
        refreshButton = contentView.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadImageWithURLTask(imageView).execute( "https://api.tc2pro.com/users/" + cognitoId + "/profiles/" + profileId + "/qr");
                Log.e(TAG, "Refreshing Qr code...");
                final Snackbar snackBar = Snackbar.make(contentView.getRootView(), "Refreshed", Snackbar.LENGTH_SHORT);
                snackBar.setAction("Dismiss", v2 -> snackBar.dismiss());
                snackBar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                snackBar.show();
            }
        });

        shareButton = contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("What would you like to add?");
            builder.setSingleChoiceItems(new String[]{"Save Image", "Share Image"}, 3, (dialogInterface, index) ->
            {
                setSelected(index);
                Toast.makeText(getContext(), "Selected:"+index, Toast.LENGTH_SHORT).show();
            });
            builder.addButton("DONE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, (dialogInterface, i) ->
            {
                Toast.makeText(getContext(), "Selected:"+selected, Toast.LENGTH_SHORT).show();
                if(selected == 1){
                    try {
                        Bitmap bitmap = getBitmapFromView(imageView);
                        File cachePath = new File(getContext().getCacheDir(), "images");
                        cachePath.mkdirs(); // don't forget to make the directory
                        FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.close();
                        shareImage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Bitmap bitmap = getBitmapFromView(imageView);
                    saveImageToExternalStorage(bitmap);
                    Toast.makeText(contentView.getContext(), "Saved successfully, Check gallery", Toast.LENGTH_SHORT).show();
                }
                dialogInterface.dismiss();
            });

            // Show the alert
            builder.show();

        });

    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images_1");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(getContext(), new String[]{file.toString()}, null,
                (path, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });

    }

    private void setSelected(Integer selectedIn){
        this.selected = selectedIn;
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void shareImage(){
        File imagePath = new File(getContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getContext(), "com.tc2.linkup.fileprovider", newFile);

        if (contentUri != null) {

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageWithURLTask extends AsyncTask<String,Void,Bitmap> {
        ImageView profileImage;
        public DownloadImageWithURLTask(ImageView profileImageIn){
            this.profileImage = profileImageIn;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (UnknownHostException e){
                Log.e(TAG, e.getMessage());
                if(e.getMessage().equals("Unable to resolve host \"api.tc2pro.com\": No address associated with hostname")){
                    getActivity().runOnUiThread(() -> {
                        new DroidDialog.Builder(getContext())
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
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            profileImage.setImageBitmap(result);
        }
    }
}