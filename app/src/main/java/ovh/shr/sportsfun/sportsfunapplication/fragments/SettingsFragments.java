package ovh.shr.sportsfun.sportsfunapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.activity.LoginActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.MainActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.User.EditBioActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.User.EditPasswordActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.User.EditUserDataActivty;
import ovh.shr.sportsfun.sportsfunapplication.activity.UserInfoActivity;
import ovh.shr.sportsfun.sportsfunapplication.models.User;
import ovh.shr.sportsfun.sportsfunapplication.network.NetworkManager;


public class SettingsFragments extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


   // @BindView(R.id.btnLogout) Button btnLogout;
  //  @BindView(R.id.btnShowProfil) Button btnShowProfil;

    @BindView(R.id.btnPassword) FrameLayout btnPassword;
    @BindView(R.id.btnLogout) FrameLayout btnLogout;
    @BindView(R.id.btnViewProfil) FrameLayout btnViewProfil;
    @BindView(R.id.lblFullname) TextView lblFullname;
    @BindView(R.id.avatar) CircleImageView avatar;
    @BindView(R.id.btnBio) FrameLayout btnBio;


    private Unbinder unbinder;

    public SettingsFragments() {
        // Required empty public constructor
    }

    public static SettingsFragments newInstance() {
        SettingsFragments fragment = new SettingsFragments();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings_fragments, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
        // Inflate the layout for this fragment
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
        void onFragmentInteraction(Uri uri);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick(R.id.btnLogout)
    public void OnBtnLogoutClicked() {

        SportsFunApplication.deleteUserLogins();
        Intent i = new Intent(getContext(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }

    @OnClick(R.id.btnViewProfil)
    public void OnBtnViewProfilClicked() {

        Intent i = new Intent(getContext(), UserInfoActivity.class);
        i.putExtra("isLocalUser", true);
        i.putExtra("_id", SportsFunApplication.getCurrentUser().getId());
        startActivity(i);

    }

    @OnClick(R.id.btnPassword)
    public void OnBtnPasswordClicked() {
        EditPasswordActivity.actionStart(this.getContext());
    }

    @OnClick(R.id.btnIdentity)
    public void OnBtnIdentityClicked() {
        EditUserDataActivty.actionStart(this.getContext());
    }

    @OnClick(R.id.btnBio)
    public void OnBtnBioClicked() {
        EditBioActivity.actionStart(this.getContext());
    }


    @Override
    public void onStart() {
        super.onStart();

        Picasso.with(getContext())
                .load(NetworkManager.BASE_CDN + "" + SportsFunApplication.getCurrentUser().getProfilePic())
                .resize(200,200).
                centerCrop()
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(avatar);

        lblFullname.setText(SportsFunApplication.getCurrentUser().getFullName());

    }
}
