package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.GameInfo;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.utilities.Utils;

public class GameHistoryAdapter extends BaseAdapter {

    //region Declarations

    private List<GameInfo> dataList;
    private LayoutInflater inflater;
    private Context context;

    //endregion Declarations

    //region Constructor

    public GameHistoryAdapter(Context context, List<GameInfo> datas) {
        this.dataList = datas;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    //endregion Constructor

    //region Adapter Specific

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

        view = inflater.inflate(R.layout.layout_item_history, null);
        ViewHolder holder = new ViewHolder(view);
        holder.lblGameTitle.setText("Runner");
        Picasso.with(context)
                .load(R.drawable.game_1)
                .into(holder.civGamePicture);

        return view;
    }

    //endregion Adapter Specific

    //region ViewHolder

    public final class ViewHolder {

        //region Declarations

        private CircleImageView civGamePicture;
        private TextView lblGameTitle;


        //endregion Declarations

        //region Getters && Setters

        public CircleImageView getCivGamePicture() {
            return civGamePicture;
        }

        public void setCivGamePicture(CircleImageView civGamePicture) {
            this.civGamePicture = civGamePicture;
        }

        public TextView getLblGameTitle() {
            return lblGameTitle;
        }

        public void setLblGameTitle(TextView lblGameTitle) {
            this.lblGameTitle = lblGameTitle;
        }

        //endregion Getters && Setters

        //region Constructor

        public ViewHolder(View itemView) {

            this.lblGameTitle = itemView.findViewById(R.id.lblGameTitle);
            this.civGamePicture = itemView.findViewById(R.id.civGamePicture);

        }

        //endregion Constructor

    }

    //endregion ViewHolder

}
