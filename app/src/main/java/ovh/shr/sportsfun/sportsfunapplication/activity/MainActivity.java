package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.fragments.MessagesFragment;
import ovh.shr.sportsfun.sportsfunapplication.fragments.NewsFragments;
import ovh.shr.sportsfun.sportsfunapplication.fragments.SearchUserFragment;
import ovh.shr.sportsfun.sportsfunapplication.fragments.SessionsFragments;
import ovh.shr.sportsfun.sportsfunapplication.fragments.SettingsFragments;

public class MainActivity extends AppCompatActivity implements
        NewsFragments.OnFragmentInteractionListener,
        SettingsFragments.OnFragmentInteractionListener,
        SessionsFragments.OnFragmentInteractionListener,
        MessagesFragment.OnFragmentInteractionListener,
        SearchUserFragment.OnFragmentInteractionListener {

    //region Declarations

    @BindView(R.id.my_toolbar) Toolbar androidToolbar;
    @BindView(R.id.frameLayout) FrameLayout frameLayout;
    @BindView(R.id.navigation) BottomNavigationViewEx navigation;
    FragmentManager manager;
    FragmentTransaction transaction;

    SearchUserFragment searchUserFragment;


    //endregion Declarations

    //region Constructor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(androidToolbar);
        getSupportActionBar().setTitle(getText(R.string.activityMainNews));
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.setTextVisibility(false);
        frameLayout.removeAllViews();
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.frameLayout, NewsFragments.newInstance()); // newInstance() is a static factory method.
        transaction.commit();

    }

    //endregion Constructor

    //region Menu

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    ChangeView(getText(R.string.activityMainNews), NewsFragments.newInstance());
                    return true;

                case R.id.navigation_sessions:
                    ChangeView(getText(R.string.activityMainSessions), SessionsFragments.newInstance());
                    return true;

                case R.id.navigation_messages:
                    ChangeView(getText(R.string.activityMainSessions), MessagesFragment.newInstance());
                    return true;

                case R.id.navigation_settingsmenu:
                    ChangeView(getText(R.string.activityMainSettings), SettingsFragments.newInstance());
                    return true;

            }
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
            case R.id.action_search:

                System.out.println("lol");

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.titlebar_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                System.out.println("lolaze");
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchUserFragment = new SearchUserFragment();

                frameLayout.removeAllViews();
                manager = getFragmentManager();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.frameLayout, searchUserFragment);
                transaction.commit();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {



            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //ChangeView(getText(R.string.activityMainSessions), SearchUserFragment.newInstance(s));
                //return true;
                searchUserFragment.RefreshUserList(s);
                return true;
            }
        });

        return true;
    }

    //endregion Menu

    //region Fragments Implementations

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void ChangeView(CharSequence title, Fragment fragment)
    {
        getSupportActionBar().setTitle(title);

        frameLayout.removeAllViews();
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();


    }


    //endregion Fragments Implementations


}
