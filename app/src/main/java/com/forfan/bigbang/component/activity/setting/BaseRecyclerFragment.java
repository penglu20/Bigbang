package com.forfan.bigbang.component.activity.setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.baseCard.CardListAdapter;
import com.forfan.bigbang.baseCard.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 * Created by penglu on 2016/5/8.
 */
public class BaseRecyclerFragment extends BaseFragment {

    protected RecyclerView cardList;
    protected View view;
    protected List<AbsCard> cardViews=new ArrayList<>();
    protected CardListAdapter newAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.fragment_common, container, false);
            cardList = (RecyclerView) view.findViewById(R.id.card_list);
            cardList.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL_LIST));
            prepareCardView();
            newAdapter = new CardListAdapter(view.getContext(), false);
            newAdapter.setCardViews(cardViews);
            cardList.setItemAnimator(new FadeInAnimator());
            cardList.setAdapter(newAdapter);
        }
        if ((ViewGroup)view.getParent()!=null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    protected void prepareCardView() {
        cardViews.add(new FeedBackAndUpdateCard(getActivity()));
    }

}
