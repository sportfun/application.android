package ovh.shr.sportsfun.sportsfunapplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.adapters.ConversationsAdapter;
import ovh.shr.sportsfun.sportsfunapplication.network.SocketIOHelper;
import ovh.shr.sportsfun.sportsfunapplication.utilities.ChatApplication;
import ovh.shr.sportsfun.sportsfunapplication.utilities.SCallback;

public class MessagesFragment extends Fragment {

    //region Declarations

    private Unbinder unbinder;
    private OnFragmentInteractionListener mListener;

    ConversationsAdapter conversationsAdapter = new ConversationsAdapter();

    @BindView(R.id.recycler_list) RecyclerView recyclerView;
    @BindView(R.id.refresher) SwipeRefreshLayout refresher;

    private Socket mSocket;

    //endregion Declarations

    //region Constructor

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        return fragment;
    }

    //endregion Constructor

    //region Android Specific

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        unbinder = ButterKnife.bind(this, view);

        refresher.setColorSchemeResources(R.color.colorPrimary);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshDatas();
            }
        });

        conversationsAdapter.setRefresher(refresher);
        conversationsAdapter.setCurrentActivity(getActivity());

        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(conversationsAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        SocketIOHelper.closeChannel("snippets", onSnippetsReceived);
        SocketIOHelper.openChannel("snippets", onSnippetsReceived);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        SocketIOHelper.closeChannel("snippets", onSnippetsReceived);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        SocketIOHelper.closeChannel("snippets", onSnippetsReceived);
    }

    @Override
    public void onStart() {
        super.onStart();

        SocketIOHelper.closeChannel("snippets", onSnippetsReceived);
        SocketIOHelper.openChannel("snippets", onSnippetsReceived);

        RefreshDatas();

    }
    //endregion Android Specific

    //region Private methods

    private void RefreshDatas()
    {
        conversationsAdapter.reset();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", SportsFunApplication.getAuthentificationToken());

        } catch (Exception err) {

        }

        SocketIOHelper.emit("snippets", jsonObject);
    }

    //endregion Private methods

    //region Events

    private Emitter.Listener onSnippetsReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject JSONObject = (JSONObject) args[0];
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(JSONObject.toString());
            conversationsAdapter.addItem(jsonObject);

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (refresher != null)
                            refresher.setRefreshing(false);
                    }
                });
            }
        }
    };


    //endregion Events

}
