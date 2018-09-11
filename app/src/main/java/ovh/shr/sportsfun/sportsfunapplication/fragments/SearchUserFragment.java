package ovh.shr.sportsfun.sportsfunapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import okhttp3.Call;
import okhttp3.Response;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.activity.RegisterActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.UserInfoActivity;
import ovh.shr.sportsfun.sportsfunapplication.adapters.SearchUserAdapter;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;
import ovh.shr.sportsfun.sportsfunapplication.network.RequestType;

public class SearchUserFragment extends Fragment implements AdapterView.OnItemClickListener {

    //region Declarations

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<User> userList = new ArrayList<>();

    public SearchUserAdapter searchUserAdapter;
    @BindView(R.id.lvUsers) ListView lvUsers;

    private Unbinder unbinder;


    //endregion Decalrations

    //region Constructor

    public SearchUserFragment() {
        // Required empty public constructor
    }

    public static SearchUserFragment newInstance(String param1) {
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    //endregion Constructor

    //region Android Specific
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_user, container, false);
        unbinder = ButterKnife.bind(this, view);

        searchUserAdapter = new SearchUserAdapter(this.getActivity(), userList);
        this.lvUsers.setAdapter(searchUserAdapter);
        this.lvUsers.setOnItemClickListener(this);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        SearchUser(mParam1);
    }

    //endregion Android Specific

    //region Private Methods

    private void SearchUser(String pattern) {

        if (pattern == null || pattern.length() == 0)
        {
            userList.clear();
            return;
        }

        NetworkManager.PostRequest("api/user/p/" + pattern, null, RequestType.GET, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getStackTrace());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                userList.clear();

                Gson gson = new GsonBuilder().create();
                JsonArray json = gson.fromJson(response.body().string(), JsonObject.class).get("data").getAsJsonArray();
                System.out.println(json.toString());


                for (JsonElement jsonElement : json) {
                    User newUser = gson.fromJson(jsonElement, User.class);

//                    JsonObject object = jsonElement.getAsJsonObject();
//
//                    User newUser = new User();
//                    newUser.setFirstname(object.get("firstName").getAsString());
//                    newUser.setLastname(object.get("lastName").getAsString());
//                    newUser.setProfilePic(object.get("profilePic").getAsString());

                    userList.add(newUser);

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchUserAdapter.notifyDataSetChanged();

                    }
                });

            }


        });

    }

    //endregion Private Methods

    //region Public methods


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        User selectedUser = this.userList.get(position - 1);

        Intent intent = new Intent(getContext(), UserInfoActivity.class);
        intent.putExtra("isLocalUser", false);
        intent.putExtra("_id", selectedUser.getId());
        startActivity(intent);
        System.out.println("lol");
    }

    public void RefreshUserList(String str) {
        SearchUser(str);
    }

    //endregion Public methods
}
