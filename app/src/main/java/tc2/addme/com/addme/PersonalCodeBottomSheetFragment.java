package tc2.addme.com.addme;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PersonalCodeBottomSheetFragment extends BottomSheetDialogFragment {

    EditText displayName;
    EditText username;
    private static final String TAG = "PersonalCodeBottomSheet";
    ImageButton shareButton;
    ImageView imageView;
    ImageButton refreshButton;
    JSONObject jsonObject;
    View contentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(getContext(), R.layout.personal_code_modal, null);


        Bundle data = getArguments();
        ArrayList<App> apps = data.getParcelableArrayList("apps");
        HashMap<String, String> map = new HashMap<>();
        if(apps.size() == 0){
            Log.d(TAG, "no apps");
        } else {
            for(App app: apps) {
                Log.d(TAG, app.toString());
                if(app.isAppSwitchIsOn()){
                    Log.d(TAG, "Will add to QR code...");
                    map.put(app.getDisplayName(), app.getUrl());
                }
            }
        }

        jsonObject = new JSONObject(map);
        Log.d(TAG, jsonObject.toString());
        imageView = (ImageView) contentView.findViewById(R.id.qrCode);
        RefreshQRCode(jsonObject);
    }

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
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        refreshButton = (ImageButton) contentView.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshQRCode(jsonObject);
            }
        });

        shareButton = (ImageButton) contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bitmap icon = mBitmap;
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("image/jpeg");
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
//                try {
//                    f.createNewFile();
//                    FileOutputStream fo = new FileOutputStream(f);
//                    fo.write(bytes.toByteArray());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
//                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

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

    void RefreshQRCode(JSONObject jsonObject) {
        try {
            String tempString = jsonObject.toString();
            Log.d(TAG, "QR code as string: \n" + tempString);

            Bitmap bm = TextToImageEncode(tempString);

            if (bm != null) {
                Log.d(TAG, "Setting qr code...");
                imageView.setImageBitmap(bm);
            }
        } catch (WriterException e) { //eek }
        }
    }

    Bitmap TextToImageEncode(String value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    value,
                    BarcodeFormat.QR_CODE,
                    500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public JSONObject createJSON() {
        JSONObject tempObject;

        tempObject = new JSONObject();
        try {
            tempObject.put("Facebook", "http://facebook.com/chris.deck.75");
            tempObject.put("Insta", "http://www.instagram.com/chris_deck");
            tempObject.put("Twitter", "http://www.twitter.com/chrisdeck7");
            tempObject.put("Xbox", "https://account.xbox.com/en-us/Profile?GamerTag=HitTheDeck95");
            tempObject.put("Twitch", "https://m.twitch.tv/deckchris95/profile");
        } catch (JSONException j) {
            Log.e(TAG, "JSON Exception: " + j);
        }


        return tempObject;
    }
}