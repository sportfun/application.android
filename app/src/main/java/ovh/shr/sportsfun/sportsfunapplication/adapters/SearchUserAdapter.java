package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.models.User;


public class SearchUserAdapter  extends BaseAdapter {

    //region Declarations

    private List<User> dataList;
    private LayoutInflater inflater;
    private Activity currentActivity;
    private String ID;

    //endregion Declarations

    //region Constructor

    public SearchUserAdapter(Activity activity, List<User> contents){
        this.dataList = contents;
        this.inflater = LayoutInflater.from(activity.getApplicationContext());
        this.currentActivity = activity;
    }

    //endregion Constructor

    //region Public Methods
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public int getCount() {
        return this.dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();
            view = inflater.inflate(R.layout.layout_searchuser_item, null);
            holder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            holder.nickname = (TextView) view.findViewById(R.id.nickname);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.nickname.setText(dataList.get(i).getFullName());

        Picasso.with(currentActivity.getApplicationContext())
                .load(dataList.get(i).getProfilPicUrl())
                .resize(200,200).
                centerCrop()
                .placeholder(R.drawable.baseline_account_circle_black_36)
                .error(R.drawable.baseline_account_circle_black_36)
                .into(holder.avatar);

        return view;
    }
    //endregion Public Methods

    //region ViewHolder

    public final class ViewHolder {
        public CircleImageView avatar;
        public TextView nickname;
    }

    //endregion ViewHolder

}
