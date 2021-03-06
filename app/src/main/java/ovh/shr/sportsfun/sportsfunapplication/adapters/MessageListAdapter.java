package ovh.shr.sportsfun.sportsfunapplication.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.SportsFunApplication;
import ovh.shr.sportsfun.sportsfunapplication.models.Message;

public class MessageListAdapter extends BaseAdapter {

    //region Declarations

    private static final int TYPE_IN     = 0;
    private static final int TYPE_OUT    = 1;
    private List<Message> dataList;
    private LayoutInflater inflater;
    private Context context;

    //endregion Declarations

    //region Constructor

    public MessageListAdapter(Context context, List<Message> dataList) {
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    //endregion Constructor

    //region Adapter Specific

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public int getItemViewType(int i)
    {
        Message msg = dataList.get(i);

        if (msg.getAuthor().equals(SportsFunApplication.getCurrentUser().getId()))
            return TYPE_OUT;
        else
            return TYPE_IN;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        Message message = dataList.get(i);

        holder = new ViewHolder();

        if (!message.getAuthor().equals(SportsFunApplication.getCurrentUser().getId())) {
            view = inflater.inflate(R.layout.msg_item_in, null);
            holder.text = (TextView) view.findViewById(R.id.text_in);
        } else {
            view = inflater.inflate(R.layout.msg_item_out, null);
            holder.text = (TextView) view.findViewById(R.id.text_out);
        }

        holder.text.setText(dataList.get(i).getMessage());
        return view;
    }

    //endregion Adapter Specific

    public final class ViewHolder {
        public CircleImageView avatar;
        public TextView text;
    }

}
