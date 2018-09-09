package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class UserInfoActivity extends AppCompatActivity {


    //region Declarations

    private String _id;
    private Boolean isLocalUser = false;
    private User userDatas;

    @BindView(R.id.btnSendMessage) AppCompatButton btnSendMessage;
    @BindView(R.id.btnFollow) AppCompatButton btnFollow;
    @BindView(R.id.txtFullname) TextView txtFullname;
    @BindView(R.id.avatar) CircleImageView avatar;
    @BindView(R.id.txtBiographie) TextView txtBiographie;

    //endregion Decalarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfos);

        Bundle b = getIntent().getExtras();
        isLocalUser = b.getBoolean("isLocalUser");
        _id = b.getString("_id");

        ButterKnife.bind(this);

        LoadDatas();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    //region Private methods

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

        System.out.println(endpoint);

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
                    userDatas.setProfilPicUrl(userDatas.getProfilePic());
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

}
