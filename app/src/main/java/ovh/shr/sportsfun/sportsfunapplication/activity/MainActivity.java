package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.gson.JsonObject;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import butterknife.BindView;
import butterknife.ButterKnife;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.fragments.MessagesFragment;
import ovh.shr.sportsfun.sportsfunapplication.fragments.NewsFragments;
import ovh.shr.sportsfun.sportsfunapplication.fragments.SearchUserFragment;
import ovh.shr.sportsfun.sportsfunapplication.fragments.SessionsFragments;
import ovh.shr.sportsfun.sportsfunapplication.fragments.SettingsFragments;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.SocketIOHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

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

    Fragment currentFragment;

    private static final int REQUEST_CODE_QR_SCAN = 101;
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 10000;
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

        SocketIOHelper.Connect();

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
                    ChangeView(getText(R.string.activityMainMessages), MessagesFragment.newInstance());
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

            case R.id.action_search:
                return true;

            case R.id.action_scan:
                RequestAccessCamera();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.titlebar_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ChangeView(getText(R.string.activityMainNews), NewsFragments.newInstance());
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

                currentFragment = searchUserFragment;


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
                //searchUserFragment.RefreshUserList(s);
                SearchUser(s);
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

        currentFragment = fragment;
    }


    private void SearchUser(String str) {

        if (currentFragment != searchUserFragment) {
            ChangeView("", searchUserFragment);
        }
        searchUserFragment.RefreshUserList(str);
    }

    //endregion Fragments Implementations

    //region QrCode

    private void RequestAccessCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                Toast toast = Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG);
                toast.show();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA_CODE);
            }
        } else {
            OpenCamera();
        }
    }

    private void OpenCamera() {

        Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
        startActivityForResult(i, REQUEST_CODE_QR_SCAN);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                DisplayAlert("QR Code", "Une erreur est survenu lors du scan du QR Code");
            }
            return;

        }

        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;

            final String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            if (result.startsWith("sportsfun:")) {

                System.out.println(result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("QR Code");
                        alertDialog.setMessage("Connexion est en cours...");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                        API.SendQRCode(result, new SCallback() {
                            @Override
                            public void onTaskCompleted(JsonObject result) {
                                System.out.println("*********************");
                                System.out.println(result.toString());
                                if (result.has("success") && result.get("success").getAsBoolean())
                                {
                                    alertDialog.dismiss();
                                    DisplayAlert("QR Code", "Connexion réussie");

                                } else {
                                    alertDialog.dismiss();
                                    DisplayAlert("QR Code", "Le QR Code n'est pas reconnu");
                                }
                            }
                        });

                    }
                });

            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("QR Code");
                alertDialog.setMessage("Le QR Code n'est pas un QR Code Sportsfun valide.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Réessayer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RequestAccessCamera();
                            }
                        });
                alertDialog.show();

                }
        }
    }

    private void DisplayAlert(final String title, final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Fermer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });


    }

    //endregion QrCode


    //region Permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenCamera();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Permission denied2", Toast.LENGTH_LONG);
                    toast.show();
                }
                return;
            }
        }
    }

    //endregion Permissions

}
