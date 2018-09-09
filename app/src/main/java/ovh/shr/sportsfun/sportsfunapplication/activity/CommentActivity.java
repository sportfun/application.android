package ovh.shr.sportsfun.sportsfunapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.adapters.CommentAdapter;
import ovh.shr.sportsfun.sportsfunapplication.adapters.NewsAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.News;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class CommentActivity extends AppCompatActivity {


    //rehion Declarations

    private static final String EXTRA_PARENT_ID = "shr.ovh.activity.parentId";
    private String ID;

    private News.NewsEntity CurrentNews;

    CommentAdapter commentAdapter;

    private ActionBar actionBar;
    private ProgressDialog progressDialog;
    @BindView(R.id.my_toolbar) Toolbar toolbar;
    @BindView(R.id.comments_list) ListView comments_list;
    @BindView(R.id.btnSendMessage) ImageButton btnSendMessage;
    @BindView(R.id.txtMessage) EditText txtMessage;


    //endregion Declarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.ID = this.getIntent().getStringExtra(EXTRA_PARENT_ID);

        CurrentNews = new News.NewsEntity();
        CurrentNews.getPost(this.ID);

        List<News.NewsEntity> dataList = new ArrayList<>();

        commentAdapter = new CommentAdapter(this, dataList);
        this.comments_list.setAdapter(commentAdapter);
        commentAdapter.setID(this.ID);
        commentAdapter.getDatas();

    }

    //region Menu

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    //endregion Menu

    //region Privates methods

    public static void actionStart(Context context, String newsID) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(EXTRA_PARENT_ID, newsID);
        context.startActivity(intent);
    }

    @OnClick(R.id.btnSendMessage)
    protected void sendComment(){

        if (this.txtMessage.getText().toString().length() == 0)
            return;

        progressDialog = new ProgressDialog(CommentActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getApplicationContext().getString(R.string.processSending));
        progressDialog.show();


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", this.txtMessage.getText().toString());
        jsonObject.addProperty("parent", this.ID);

        NetworkManager.PostRequest("api/post", jsonObject, RequestType.POST, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                commentAdapter.getDatas();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtMessage.setText("");
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(txtMessage.getWindowToken(), 0);
                    }
                });
            }
        });

    }


    //endregion Privates methods

}
