package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DrawableHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.NotificationHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class UserInfoActivity extends AppCompatActivity {


    //region Declarations

    private String _id;
    private Boolean isLocalUser = false;
    private User userDatas;
    private Activity activity;

    @Nullable @BindView(R.id.btnSendMessage) AppCompatButton btnSendMessage;
    @Nullable @BindView(R.id.btnFollow) AppCompatButton btnFollow;
    @Nullable @BindView(R.id.txtFullname) TextView txtFullname;
    @Nullable @BindView(R.id.txtBiographie) TextView txtBiographie;

    @Nullable @BindView(R.id.avatar) CircleImageView avatar;
    @Nullable @BindView(R.id.userinfo_header) LinearLayout userinfo_header;

    private Unbinder unbinder;

    //endregion Decalarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        Bundle bundle = getIntent().getExtras();
        isLocalUser = bundle.getBoolean("isLocalUser");
        _id = bundle.getString("_id");
        if (_id.equals(SportsFunApplication.getCurrentUser().getId()))
            isLocalUser = true;
        LoadView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    //region Private methods

    private void LoadView() {

        if (unbinder != null)
            unbinder.unbind();

        API.GetUser(_id, new SCallback() {

            @Override
            public void onTaskCompleted(final JsonObject result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.has("success") && result.get("success").getAsBoolean() == false) {
                            setContentView(R.layout.activity_interneterror);
                            unbinder = ButterKnife.bind(activity);
                        } else {
                            setContentView(R.layout.activity_userinfos);
                            unbinder = ButterKnife.bind(activity);
                            Gson gson = new Gson();
                            userDatas = gson.fromJson(result.get("data").getAsJsonObject(), User.class);
                            RefreshLayout();


                            registerForContextMenu(avatar);

                        }
                    }
                });

            }

        });
    }

    private void RefreshLayout() {
        txtFullname.setText(userDatas.getFullName());
        txtBiographie.setText(userDatas.getBio());
        Picasso.with(getApplicationContext())
                .load(userDatas.getProfilPicUrl())
                .resize(150,150)
                .centerCrop()
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(avatar);

        if (SportsFunApplication.getCurrentUser().getLinks().contains(_id)) {
            btnFollow.setText(R.string.btnUnfollow);
        } else {
            btnFollow.setText(R.string.btnFollow);
        }

        if (isLocalUser == true) {
            btnFollow.setVisibility(View.INVISIBLE);
            btnSendMessage.setVisibility(View.INVISIBLE);
        }
        else
        {
            btnFollow.setVisibility(View.VISIBLE);
            btnSendMessage.setVisibility(View.VISIBLE);
        }
    }

    private void LoadDatas()
    {
        String endpoint = "api/user";

        if (isLocalUser == false) {
            endpoint += "/" + _id;
        }
        else
        {
            btnFollow.setVisibility(View.INVISIBLE);
            btnSendMessage.setVisibility(View.INVISIBLE);
        }

        if (SportsFunApplication.getCurrentUser().getLinks().contains(_id)) {
            btnFollow.setText(R.string.btnUnfollow);
        } else {
            btnFollow.setText(R.string.btnFollow);
        }

        NetworkManager.PostRequest(endpoint, null, RequestType.GET, new  okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    System.out.println(response.isSuccessful());

                    Gson gson = new GsonBuilder().create();
                    JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

                    userDatas = gson.fromJson(json.get("data").getAsJsonObject(), User.class);
                    userDatas.setProfilPicUrl(userDatas.getProfilPicUrl());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtFullname.setText(userDatas.getFullName());
                            txtBiographie.setText(userDatas.getBio());
                            Picasso.with(getApplicationContext())
                                    .load(userDatas.getProfilPicUrl())
                                    .resize(150,150)
                                    .centerCrop()
                                    .placeholder(R.drawable.baseline_account_circle_black_36)
                                    .error(R.drawable.baseline_account_circle_black_36)
                                    .into(avatar);

                        }
                    });
                }

            }

        });
    }

    //endregion Private methods


    //region Events

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.avatar) {
            getMenuInflater().inflate(R.menu.menu_context_pic, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.selectImage:
                NotificationHelper.newNotification(getApplicationContext(), "Benjamin Malbrel", "Salut");
                NotificationHelper.newNotification(getApplicationContext(), "Benjamin Malbrel", "Salupazet");
                NotificationHelper.newNotification2(getApplicationContext(), "Sylvain Garant", "Salut");

                return true;

            case R.id.deleteImage:
                System.out.println("deleteImage");

                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    //endregion Events

    //region Buttons

    @OnClick(R.id.btnFollow) @Optional
    public void OnBtnFollowClick()
    {

        API.ToogleFollow(_id, new SCallback() {

            @Override
            public void onTaskCompleted(JsonObject result) {

                API.RefreshLocalUserData(new SCallback() {
                    @Override
                    public void onTaskCompleted(JsonObject result) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (SportsFunApplication.getCurrentUser().getLinks().contains(_id)) {
                                    btnFollow.setText(R.string.btnUnfollow);
                                } else {
                                    btnFollow.setText(R.string.btnFollow);
                                }
                            }
                        });
                    }

                });
            }

        });
    }

    @OnClick(R.id.btnSendMessage) @Optional
    public void OnBtnSendMessageClick()
    {

        Intent newIntent = new Intent(getApplicationContext(), MessageActivity.class);

        newIntent.putExtra("partnerName", userDatas.getFullName());
        newIntent.putExtra("partnerID", userDatas.getId());

        startActivity(newIntent);

    }

    @OnClick(R.id.btnReload) @Optional
    public void btnReload_OnClick()
    {
        LoadView();
    }

    @OnClick(R.id.avatar)
    public void avatar_OnClick() {
        System.out.println("LOL");
    }

    @OnClick(R.id.userinfo_header)
    public void userinfo_OnClick() {
        System.out.println("MDR");
        try {
            userinfo_header.setBackground(DrawableHelper.drawableFromUrl("https://3.bp.blogspot.com/-gNg5dQQkDOA/UHZUx83bu7I/AAAAAAAALT4/TtslrC5Umq4/s1600/Galaxy+Desktop+Wallpapers+2.jpg"));
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    //endregion Buttons
}
