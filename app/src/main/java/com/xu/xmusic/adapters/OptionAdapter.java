package com.xu.xmusic.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xu.xmusic.R;
import com.xu.xmusic.bean.MusicListBean;
import com.xu.xmusic.bean.OptionBean;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position);
    }

    private List<OptionBean> list;
    private Context context;
    private MusicListAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(MusicListAdapter.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OptionAdapter(Context context, List<OptionBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_option, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OptionBean optionBean = list.get(position);

        holder.item_icon.setImageResource(optionBean.getIcon());
        holder.item_name.setText(optionBean.getName());
        holder.item_count.setText(optionBean.getCount() + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, holder, position);
                }
            }
        });
    }

    public MusicListBean getMusicListBean(int position) {
        return list.get(position).getMusicListBean();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_icon;
        private TextView item_name, item_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_icon = itemView.findViewById(R.id.item_icon);
            item_name = itemView.findViewById(R.id.item_name);
            item_count = itemView.findViewById(R.id.item_count);
        }
    }
}
