package ovh.shr.sportsfun.sportsfunapplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.activity.PublishActivity;
import ovh.shr.sportsfun.sportsfunapplication.adapters.GameHistoryAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.GameInfo;
import ovh.shr.sportsfun.sportsfunapplication.network.API;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.JsonHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class SessionsFragments extends Fragment {

    //region Declarations

    // BindView
    private Unbinder unbinder;

    @BindView(R.id.lblMinEfforts) TextView lblMinEfforts;
    @BindView(R.id.lblObjectifs) TextView lblObjectifs;
    @BindView(R.id.lblObjectPercent) TextView lblObjectPercent;
    @BindView(R.id.pbObjectif) ProgressBar pbObjectif;

    @BindView(R.id.lvGameHistory) ListView lvGameHistory;
    private GameHistoryAdapter gameHistoryAdapter;
    private List<GameInfo> dataList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    // TMP
    private int timeSpent = 0;

    //endregion Declarations

    //region Constructor

    public SessionsFragments() {
    }

    public static SessionsFragments newInstance() {
        SessionsFragments fragment = new SessionsFragments();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sessions_fragments, container, false);
        unbinder = ButterKnife.bind(this, view);

        gameHistoryAdapter = new GameHistoryAdapter(this.getContext(), this.dataList);
        lvGameHistory.setAdapter(gameHistoryAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SetMinutesEfforts(0);
        SetPercentage(0);
        SetObjectifs(SportsFunApplication.getCurrentUser().getGoal());
        RefreshStats();
        RefreshHistory();

//        Picasso.with(getContext())
//                .load("http://sportsfun.shr.ovh/ressources/jeu1.jpg")
//                .placeholder(R.drawable.baseline_account_circle_black_36)
//                .error(R.drawable.baseline_account_circle_black_36)
//                .into(civGamePicture);
    }

    //endregion Constructor

    //region Deconstructor

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //endregion Deconstructor

    //region Private methods

    private void RefreshStats() {

        NetworkManager.PostRequest("api/activity", null, RequestType.GET, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                JsonObject json = JsonHelper.GetJsonObject(response.body().string());
                JsonArray elements = json.get("data").getAsJsonArray();

                for (int index = 0; index < elements.size(); index++) {
                    JsonObject element = elements.get(index).getAsJsonObject();

                    timeSpent += element.get("timeSpent").getAsInt();
                }

                if (elements.size() > 0) {
                    timeSpent /= 60;
                    SetMinutesEfforts(timeSpent);
                    SetObjectifs(SportsFunApplication.getCurrentUser().getGoal());

                    if (timeSpent > SportsFunApplication.getCurrentUser().getGoal()) {
                        SetPercentage(100);
                    } else
                        SetPercentage(Math.round(timeSpent * 100 / SportsFunApplication.getCurrentUser().getGoal() * 100 / 100));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                SetPercentage(0);
                SetMinutesEfforts(0);
                SetObjectifs(0);

            }

        });

    }

    private void SetPercentage(int percent) {

        this.lblObjectPercent.setText(percent + "%");
        this.pbObjectif.setProgress(percent);

    }

    private void SetMinutesEfforts(int minutes) {
        this.lblMinEfforts.setText("" + minutes);
    }

    private void SetObjectifs(int amounts) {
        this.lblObjectifs.setText("" + amounts);
    }

    //endregion Private methods

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void RefreshHistory() {

        API.GetActivities(new SCallback() {

            @Override
            public void onTaskCompleted(JsonObject result) {

                if (result.has("success") && result.get("success").getAsBoolean() == false) {
                    switch (result.get("message").getAsString()) {

                        case "failed_to_connect":

                            break;


                    }
                } else {

                    for (JsonElement jsonElement : result.get("data").getAsJsonArray()) {

                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        GameInfo newGameInfo = new GameInfo();

                        newGameInfo.setId(jsonObject.get("_id").getAsString());
                        newGameInfo.setGame(jsonObject.get("game").getAsString());
                        newGameInfo.setType(jsonObject.get("type").getAsString());
                        newGameInfo.setTimeSpent(jsonObject.get("timeSpent").getAsInt());
                        newGameInfo.setDate(DateHelper.fromISO8601UTC(jsonObject.get("createdAt").getAsString()));
                        newGameInfo.setScore(jsonObject.get("score").getAsInt());

                        dataList.add(newGameInfo);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            gameHistoryAdapter.notifyDataSetChanged();

                        }
                    });
                }

            }

        });

    }

}
