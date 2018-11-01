package com.tc2.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ImportCodeActivity extends Fragment {

    Button importCodeButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.import_code_tab, container, false);

        importCodeButton = rootView.findViewById(R.id.importCodeButton);
        importCodeButton.setOnClickListener(v -> {
            //get image and read QR code
            String profileId = "90";
            Bundle bundle = new Bundle();
            bundle.putString("profileId", profileId);
            Intent intent = new Intent(getActivity(), ScannedProfileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        return rootView;
    }
}
