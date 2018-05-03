package tc2.addme.com.addme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

    EditText displayName;
    EditText username;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final View contentView = View.inflate(getContext(), R.layout.dialog_modal, null);
        dialog.setContentView(contentView);
        ImageView facebookTV = (ImageView) contentView.findViewById(R.id.Facebook);
        ImageView twitterTV = (ImageView) contentView.findViewById(R.id.Twitter);
        ImageView instagramTV = (ImageView) contentView.findViewById(R.id.Instagram);

        facebookTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog();
            }
        });

        twitterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog();
            }
        });

        instagramTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAlertDialog();
            }
        });

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
//        facebookTV.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.facebook_icon), null, null, null);
//        twitterTV.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.facebook_icon), null, null, null);

    }

    private void launchAlertDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        // Set Custom Title
        TextView title = new TextView(getContext());
        // Title Properties
        title.setText("Add Me");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set Message
        displayName = new EditText(getApplicationContext());
        // Message Properties
        displayName.setHint("Display Name");
        displayName.setGravity(Gravity.CENTER_HORIZONTAL);
        displayName.setTextColor(Color.BLACK);

        // Set Message
        username = new EditText(getApplicationContext());
        // Message Properties
        username.setHint("Username");
        username.setGravity(Gravity.CENTER_HORIZONTAL);
        username.setTextColor(Color.BLACK);

        layout.addView(displayName);
        layout.addView(username);

        alertDialog.setView(layout);

        // Set Button
        // you can more buttons
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String userName = username.getText().toString();
                String display_name = displayName.getText().toString();
                Log.e("Add App", userName);
                Log.e("Add App", display_name);
            }
        });


        new Dialog(getApplicationContext());
        alertDialog.show();

        // Set Properties for OK Button
        final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.BLUE);
        okBT.setLayoutParams(neutralBtnLP);

        final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
    }
}