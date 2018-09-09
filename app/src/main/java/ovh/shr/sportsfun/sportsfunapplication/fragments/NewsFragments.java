package ovh.shr.sportsfun.sportsfunapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.activity.LoginActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.PublishActivity;
import ovh.shr.sportsfun.sportsfunapplication.adapters.NewsAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.News;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragments.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragments extends Fragment {

    //region Declarations

    private Unbinder unbinder;

    NewsAdapter newsAdapter = new NewsAdapter();

    //region Binds

    @BindView(R.id.tweets_recycler_list) RecyclerView rvNews;
    @BindView(R.id.refresher) SwipeRefreshLayout refresher;
    @BindView(R.id.add_tweets_fab) FloatingActionButton fabNewMessage;

    //endregion Binds

    //endregion Declarations

    private OnFragmentInteractionListener mListener;

    public NewsFragments() {
        // Required empty public constructor
    }

    public static NewsFragments newInstance() {
        NewsFragments fragment = new NewsFragments();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        RefreshNewsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_fragments, container, false);
        unbinder = ButterKnife.bind(this, view);

        refresher.setColorSchemeResources(R.color.colorPrimary);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshNewsData();
            }
        });

        newsAdapter.setRefresher(refresher);
        newsAdapter.setCurrentActivity(getActivity());

        fabNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishActivity.actionStart(getActivity());
            }
        });

        this.rvNews.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.rvNews.setLayoutManager(layoutManager);
        this.rvNews.setItemAnimator(new DefaultItemAnimator());
        this.rvNews.setAdapter(newsAdapter);

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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    //region Public methods

    //endregion Public methods

    //region Private methods

    private void RefreshNewsData()
    {
        newsAdapter.refreshDatas();
    }

    //endregion Private methods





}
