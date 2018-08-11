package tc2.addme.com.addme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by cdeck_000 on 4/4/2017.
 */

public class CustomEditAppsAdapter extends ArrayAdapter<App>  {

    private static final String TAG = "CustomGroupsAdapter";

    public CustomEditAppsAdapter(Context context, int resource, ArrayList<App> apps) {
        super(context, resource, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_edit_apps_rows, parent, false);

        final App singleApp = getItem(position);

        TextView txtAppDisplayName = customView.findViewById(R.id.txtAppDisplayName2);
        txtAppDisplayName.setText(singleApp.getDisplayName());

        Button editApp = customView.findViewById(R.id.editBtn2);
        editApp.setOnClickListener(v -> {
            Log.d(TAG, "Edit Button pressed");
        });

        TextView txtAppID = customView.findViewById(R.id.txtAppID2);
        String appID = singleApp.getAppID() + "";
        txtAppID.setText(appID);

        ImageView appImage = customView.findViewById(R.id.imageView2);
        String platform = singleApp.getPlatform();

        switch (platform){
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

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
