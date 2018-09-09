package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class PublishActivity extends AppCompatActivity  {


    //region Declarations

    private ActionBar actionBar;

    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.txtMessage) EditText txtMessage;

    //endregion Declarations


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    //region Public methods

    /// Create a new Ident of PublishActivty

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PublishActivity.class);
        context.startActivity(intent);
    }

    //endregion Public methods

    //endregion


    //region Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.publish_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.publish_share:
                Publish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //endregion Menu

    //region Privates methods

    public void Publish() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", txtMessage.getText().toString());


        NetworkManager.PostRequest("api/post", jsonObject, RequestType.POST, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                finish();
            }

        });
    }

    //endregion Privates methods


}
