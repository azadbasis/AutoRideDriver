package org.autoride.driver.autorideReference;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.autoride.driver.R;
import org.autoride.driver.utils.reference.ReferenceItem;

import java.util.ArrayList;


public class ReferenceViewPagerFragment extends Fragment {

    private static final String EXTRA_INITIAL_ITEM_POS = "initial_item_pos";
    private static final String EXTRA_REFERENCE_ITEMS = "reference_items";

    public ReferenceViewPagerFragment() {
        // Required empty public constructor
    }

    public static ReferenceViewPagerFragment newInstance(int currentItem, ArrayList<ReferenceItem> referenceItems) {
        ReferenceViewPagerFragment referenceViewPagerFragment = new ReferenceViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_INITIAL_ITEM_POS, currentItem);
        bundle.putParcelableArrayList(EXTRA_REFERENCE_ITEMS, referenceItems);
        referenceViewPagerFragment.setArguments(bundle);
        return referenceViewPagerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        setSharedElementReturnTransition(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reference_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int currentItem = getArguments().getInt(EXTRA_INITIAL_ITEM_POS);
        ArrayList<ReferenceItem> referenceItems = getArguments().getParcelableArrayList(EXTRA_REFERENCE_ITEMS);

        ReferencePagerAdapter referencePagerAdapter = new ReferencePagerAdapter(getChildFragmentManager(), referenceItems);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.reference_view_pager);
        viewPager.setAdapter(referencePagerAdapter);
        viewPager.setCurrentItem(currentItem);
    }
}