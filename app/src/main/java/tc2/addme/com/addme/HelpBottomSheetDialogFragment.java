package tc2.addme.com.addme;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.cketti.mailto.EmailIntentBuilder;

public class HelpBottomSheetDialogFragment extends BottomSheetDialogFragment implements ProgressGenerator.OnCompleteListener {

    private static final String TAG = "AddAppActivity";


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
        final View contentView = View.inflate(getContext(), R.layout.help_activity, null);
        dialog.setContentView(contentView);
        String version = "";
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView helpTV = (TextView) contentView.findViewById(R.id.issueDesc);
        NiceSpinner niceSpinner = contentView.findViewById(R.id.helpChoicesSpinner);
        List<String> dataset = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.help_choices)));
        niceSpinner.attachDataSource(dataset);

        helpTV.setText("Please describe your issue here... \n\nOS Version: " + android.os.Build.VERSION.SDK_INT + "\nBuild Version: " + version);
        helpTV.setMovementMethod(new ScrollingMovementMethod());


        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton btnSignIn = contentView.findViewById(R.id.submitBtn);

            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);

          //  btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);

        btnSignIn.setOnClickListener(v -> {
            progressGenerator.start(btnSignIn);
            btnSignIn.setEnabled(false);
            helpTV.setEnabled(false);
            niceSpinner.setEnabled(false);

            boolean success = EmailIntentBuilder.from(getActivity())
                    .to("support@tc2pro.com")
                    .subject("" + dataset.get(niceSpinner.getSelectedIndex()))
                    .body(""+ helpTV.getText().toString())
                    .start();
            if(success){
                btnSignIn.setEnabled(true);
                helpTV.setEnabled(true);
                niceSpinner.setEnabled(true);
            }
        });


    }

    @Override
    public void onComplete() {
        Toast.makeText(getContext(), "Loading Complete", Toast.LENGTH_LONG).show();
    }
}
