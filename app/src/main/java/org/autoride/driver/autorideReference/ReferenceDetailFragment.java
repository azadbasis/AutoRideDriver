package org.autoride.driver.autorideReference;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.autoride.driver.R;
import org.autoride.driver.utils.reference.ReferenceItem;


public class ReferenceDetailFragment extends Fragment {

    private static final String EXTRA_REFERENCE_ITEM = "reference_item";
    private static final String EXTRA_TRANSITION_NAME = "transition_name";

    public ReferenceDetailFragment() {
        // Required empty public constructor
    }

    public static ReferenceDetailFragment newInstance(ReferenceItem referenceItem, String transitionName) {
        ReferenceDetailFragment referenceDetailFragment = new ReferenceDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_REFERENCE_ITEM, referenceItem);
        bundle.putString(EXTRA_TRANSITION_NAME, transitionName);
        referenceDetailFragment.setArguments(bundle);
        return referenceDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reference_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ReferenceItem referenceItem = getArguments().getParcelable(EXTRA_REFERENCE_ITEM);
        String transitionName = getArguments().getString(EXTRA_TRANSITION_NAME);

        TextView nameTextView = (TextView) view.findViewById(R.id.reference_name_text);
        nameTextView.setText(referenceItem.name);

        TextView statusTextView = (TextView) view.findViewById(R.id.reference_status_text);
        statusTextView.setText(referenceItem.status);
        TextView registrationLocationTextView = (TextView) view.findViewById(R.id.reference_registration_location_text);
        registrationLocationTextView.setText(referenceItem.registeredLocation);
        TextView referenceAccountTextView = (TextView) view.findViewById(R.id.reference_account_text);
        referenceAccountTextView.setText("A/C- "+referenceItem.accountNumber);
        TextView homeTextView = (TextView) view.findViewById(R.id.reference_home_text);
        homeTextView.setText(referenceItem.address);
        TextView registrationDateTextView = (TextView) view.findViewById(R.id.reference_reg_date_text);
        registrationDateTextView.setText(referenceItem.createdDate);


        ImageView imageView = (ImageView) view.findViewById(R.id.reference_detail_image_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(transitionName);
        }

        Picasso.with(getContext())
                .load(referenceItem.imageUrl)
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        startPostponedEnterTransition();
                    }
                });
    }
}