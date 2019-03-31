package org.autoride.driver.autorideReference;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.utils.reference.ReferenceGalleryAdapter;
import org.autoride.driver.utils.reference.ReferenceItem;
import org.autoride.driver.utils.reference.ReferenceItemClickListener;
import org.autoride.driver.utils.reference.Utils;


public class RecyclerViewFragment extends Fragment implements ReferenceItemClickListener {

    public static final String TAG = RecyclerViewFragment.class.getSimpleName();

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ReferenceGalleryAdapter referenceGalleryAdapter = new ReferenceGalleryAdapter(DriverMainActivity.generateReferenceItems(getContext()), this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(referenceGalleryAdapter);
    }

    @Override
    public void onRefereneItemClick(int pos, ReferenceItem referenceItem, ImageView sharedImageView) {
        Fragment referenceViewPagerFragment = ReferenceViewPagerFragment.newInstance(pos, DriverMainActivity.generateReferenceItems(getContext()));
        getFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedImageView, ViewCompat.getTransitionName(sharedImageView))
                .addToBackStack(TAG)
                .replace(R.id.content, referenceViewPagerFragment)
                .commit();
    }
}