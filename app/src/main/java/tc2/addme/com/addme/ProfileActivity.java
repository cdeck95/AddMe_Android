package tc2.addme.com.addme;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Level;

public class ProfileActivity extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "ProfileActivity";
    ListView appList;
    SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgProfile;
    private TextView namneTV;
    private ArrayList<App> apps;
    private App app1, app2, app3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.profile_tab, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        appList = (ListView) rootView.findViewById(R.id.appsListView);
        app1 = new App(1, "Personal Facebook", "Facebook", "http://www.facebook.com");
        app2 = new App(2, "Personal Twitter", "Twitter", "http://www.facebook.com");
        app3 = new App(3, "Personal Instagram", "Instagram", "http://www.facebook.com");
        apps = new ArrayList<App>();
        apps.add(app1);
        apps.add(app2);
        apps.add(app3);

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
                populateApps(1, rootView);
                swipeRefreshLayout.setRefreshing(false);
            }
        });




        return rootView;
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


}
