package com.forfan.bigbang.component.activity.setting;

import com.forfan.bigbang.R;
import com.forfan.bigbang.component.base.BaseActivity;

/**
 * Created by penglu on 2016/12/10.
 */

public class DisplayFragment extends BaseRecyclerFragment {

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            BaseActivity baseActivity=((BaseActivity)getActivity());
            baseActivity.getSupportActionBar().setTitle(R.string.fragment_display);
        }
    }

    @Override
    protected void prepareCardView() {
        cardViews.add(new BigBangSettingCard(getActivity()));
        cardViews.add(new FloatAndNotifySettingCard(getActivity()));
    }
}
