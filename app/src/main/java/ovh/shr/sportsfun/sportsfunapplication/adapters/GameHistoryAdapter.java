package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
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
import ovh.shr.sportsfun.sportsfunapplication.utilities.DateHelper;
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
        GameInfo gameInfo = dataList.get(i);
        holder.lblGameTitle.setText(gameInfo.getGame());
        holder.lblDate.setText("le " + DateHelper.toCustomString(gameInfo.getDate(), "dd/MM/yyyy"));
        holder.lblScore.setText("" + gameInfo.getScore());
        int minutes = gameInfo.getTimeSpent() / 60;
        int seconds = gameInfo.getTimeSpent();

        if (seconds < 60)
            holder.lblTimeSpent.setText("pendant " + seconds + " secondes");
        else
            holder.lblTimeSpent.setText("pendant " + minutes + " minutes");

        //holder.lblScore.setText(gameInfo.getScore());

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
        private TextView lblType;
        private TextView lblDate;
        private TextView lblTimeSpent;
        private TextView lblScore;

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

        public TextView getLblType() {
            return lblType;
        }

        public void setLblType(TextView lblType) {
            this.lblType = lblType;
        }

        public TextView getLblDate() {
            return lblDate;
        }

        public void setLblDate(TextView lblDate) {
            this.lblDate = lblDate;
        }

        public TextView getLblTimeSpent() {
            return lblTimeSpent;
        }

        public void setLblTimeSpent(TextView lblTimeSpent) {
            this.lblTimeSpent = lblTimeSpent;
        }

        public TextView getLblScore() {
            return lblScore;
        }

        public void setLblScore(TextView lblScore) {
            this.lblScore = lblScore;
        }

        //endregion Getters && Setters

        //region Constructor

        public ViewHolder(View itemView) {

            this.lblGameTitle = itemView.findViewById(R.id.lblGameTitle);
            this.civGamePicture = itemView.findViewById(R.id.civGamePicture);
            this.lblType = itemView.findViewById(R.id.lblType);
            this.lblDate = itemView.findViewById(R.id.lblDate);
            this.lblTimeSpent = itemView.findViewById(R.id.lblTimeSpent);
            this.lblScore = itemView.findViewById(R.id.lblScore);

        }

        //endregion Constructor

    }

    //endregion ViewHolder

}
