package com.xu.xmusic.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xu.xmusic.R;
import com.xu.xmusic.bean.MusicBean;
import com.xu.xmusic.utils.PixelUtils;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position);
    }

    private List<MusicBean> list;
    private Context context;
    private int layoutId;
    private int select = -1;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public MusicAdapter(Context context, List<MusicBean> list) {
        this.context = context;
        this.list = list;
        layoutId = R.layout.item_music;
    }

    public MusicAdapter(Context context, List<MusicBean> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
    }

    public MusicBean getItemObject(int postion) {
        return list.get(postion);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MusicBean musicBean = list.get(position);

        String num = position + 1 + "";
        if (layoutId == R.layout.item_music) {
            holder.item_num.setText(num);

            Drawable drawable = null;
            String text = musicBean.getSongname();
            if (musicBean.getPayplay() == 1) {
                drawable = context.getResources().getDrawable(R.drawable.ic_vip);
                drawable.setBounds(0, 0, PixelUtils.dp2px(context, 20), PixelUtils.dp2px(context, 20));
                text += "  ";
            }
            holder.item_songname.setCompoundDrawables(null, null, drawable, null);
            holder.item_songname.setText(text);

            holder.item_singer_albumname.setText(musicBean.getSinger() + "·" + musicBean.getAlbumname());

            if (select == position) {
                holder.item_num.setTextColor(context.getResources().getColor(R.color.colorBlue));
                holder.item_songname.setTextColor(context.getResources().getColor(R.color.colorBlue));
                holder.item_singer_albumname.setTextColor(context.getResources().getColor(R.color.colorBlue));
            } else {
                holder.item_num.setTextColor(context.getResources().getColor(R.color.colorGray40));
                holder.item_songname.setTextColor(context.getResources().getColor(R.color.colorGray40));
                holder.item_singer_albumname.setTextColor(context.getResources().getColor(R.color.colorGray80));
            }
        } else if (layoutId == R.layout.item_dialog_music) {
            Drawable drawable = null;
            String text = num + ". " + musicBean.getSongname() + " - " + musicBean.getSinger();
            if (musicBean.getPayplay() == 1) {
                drawable = context.getResources().getDrawable(R.drawable.ic_vip);
                drawable.setBounds(0, 0, PixelUtils.dp2px(context, 20), PixelUtils.dp2px(context, 20));
                text += "  ";
            }
            holder.item_songname.setCompoundDrawables(null, null, drawable, null);
            holder.item_songname.setText(text);

            if (select == position) {
                holder.item_songname.setTextColor(context.getResources().getColor(R.color.colorBlue));
            } else {
                holder.item_songname.setTextColor(context.getResources().getColor(R.color.colorGraye0));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, holder, position);
                }
            }
        });
    }

    public int getPositionBySongmid(String songmid) {
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(songmid, list.get(i).getSongmid())) {
                return i;
            }
        }
        return -1;
    }

    public int getPayPlay(int position) {
        return list.get(position).getPayplay();
    }

    public void setSelect(int position) {
        if (select != position) {
            notifyItemChanged(select);
            notifyItemChanged(position);
            select = position;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView item_num, item_songname, item_singer_albumname;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_num = itemView.findViewById(R.id.item_num);
            item_songname = itemView.findViewById(R.id.item_songname);
            item_singer_albumname = itemView.findViewById(R.id.item_singer_albumname);
        }
    }
}
