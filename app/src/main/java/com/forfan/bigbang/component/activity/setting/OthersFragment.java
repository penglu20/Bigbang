package com.forfan.bigbang.component.activity.setting;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;

/**
 * Created by penglu on 2016/12/10.
 */

public class OthersFragment extends BaseRecyclerFragment {
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BaseActivity baseActivity=((BaseActivity)getActivity());
            baseActivity.getSupportActionBar().setTitle(R.string.fragment_other);
        }
    }

    @Override
    protected void prepareCardView() {
        cardViews.add(new SLSettingCard(getActivity()));
        cardViews.add(new FeedBackAndUpdateCard(getActivity()));
        cardViews.add(new AboutCard(getActivity()));
    }

}
