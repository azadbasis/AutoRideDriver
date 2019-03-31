package org.autoride.driver.autorideReference;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import org.autoride.driver.utils.reference.ReferenceItem;

import java.util.List;


public class ReferencePagerAdapter extends FragmentStatePagerAdapter {

    private List<ReferenceItem> referenceItems;

    ReferencePagerAdapter(FragmentManager fm, List<ReferenceItem> referenceItems) {
        super(fm);
        this.referenceItems = referenceItems;
    }

    @Override
    public Fragment getItem(int position) {
        ReferenceItem referenceItem = referenceItems.get(position);
        return ReferenceDetailFragment.newInstance(referenceItem, referenceItem.name);
    }

    @Override
    public int getCount() {
        return referenceItems.size();
    }

}