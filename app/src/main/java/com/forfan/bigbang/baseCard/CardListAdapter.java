package com.forfan.bigbang.baseCard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.forfan.bigbang.component.activity.setting.MonitorSettingCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by penglu on 2015/11/23.
 */
public class CardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_STEP=1000000;

    private List<AbsCard> cardViews;
    private Context mContext;
    public CardListAdapter(Context context, boolean shouldShowShare){
        mContext=context;
        cardViews=new ArrayList<>();
    }

    public void setCardViews(List<AbsCard> cardViews){
        this.cardViews = cardViews;
    }

    @Override
    public int getItemViewType(int position) {
        return cardViews.get(position).hashCode()%TYPE_STEP;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (View view:cardViews){
            if(view.hashCode()%TYPE_STEP==viewType){
                AbsCard cardView= (AbsCard) view;
                return new ViewHolder(cardView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.setView(cardViews.get(position));
        return ;
    }



    @Override
    public int getItemCount() {
        return cardViews.size();
    }

    public boolean containsView(AbsCard v) {
        for (int i = 0; i< cardViews.size(); i++){
            if (v == cardViews.get(i)){
                return true;
            }
        }
        return false;
    }

    public static class ViewHolder<T extends View> extends RecyclerView.ViewHolder {
         public T cardView;
         public ViewHolder(T v) {
             super(v);
             cardView = v;
         }
        public void setView(T v){
            cardView=v;
        }
    }

    public void deleteView(View v){
        for (int i = 0; i< cardViews.size(); i++){
            if (v == cardViews.get(i)){
                cardViews.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void deleteView(Class<? extends AbsCard> v){
        for (int i = 0; i< cardViews.size(); i++){
            if ( v.isInstance(cardViews.get(i))){
                cardViews.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void addView(AbsCard v){
        addView(v,false);
    }


    public void addView(AbsCard v,boolean unique){
        boolean needAdd=true;
        for (int i = 0; i< cardViews.size(); i++) {
            if (unique&&v.getClass().equals(cardViews.get(i).getClass())){
                needAdd=false;
            }
        }
        if (needAdd) {
            cardViews.add(v);
            notifyItemInserted(cardViews.size() - 1);
        }
    }

    public void addView(AbsCard v, int postion){
        if (cardViews.size()<postion){
            postion=cardViews.size();
        }
        cardViews.add(postion,v);
        notifyItemInserted(postion);
    }

}
