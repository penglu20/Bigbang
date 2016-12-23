package com.forfan.bigbang.onestep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forfan.bigbang.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyan-pd on 2016/12/21.
 */

public class AppsAdapter extends SwipeMenuAdapter<AppsAdapter.MyViewHolder> {
    private final Context mContext;
    private List<ResolveInfoWrap> items = new ArrayList<>();
    private OnItemClickListener mOnItemClicklitener;

    public AppsAdapter(Context c) {
        mContext = c;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_intent, null);

        return convertView;
    }

    @Override
    public MyViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        MyViewHolder holder = new MyViewHolder(realContentView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ResolveInfoWrap> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClicklitener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClicked(ResolveInfoWrap item);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }

        public void setItem(ResolveInfoWrap appItem) {
            ((ImageView) itemView.findViewById(R.id.app_icon)).setImageDrawable(appItem.resolveInfo.loadIcon(mContext.getPackageManager()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClicklitener != null) {
                        mOnItemClicklitener.onItemClicked(appItem);
                    }
                }
            });
        }
    }

}
