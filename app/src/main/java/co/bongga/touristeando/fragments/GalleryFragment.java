package co.bongga.touristeando.fragments;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.GalleryItem;
import co.bongga.touristeando.utils.UtilityManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends DialogFragment {
    private ArrayList<GalleryItem> imageList;
    private ViewPager viewPager;
    private TextView lblCount;
    private GalleryPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;
    private ImageButton btnClose;

    public GalleryFragment() {

    }

    public static GalleryFragment newInstance() {
        GalleryFragment f = new GalleryFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);

        btnClose = (ImageButton) v.findViewById(R.id.btn_close_image_fullscreen);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityManager.showMessage(btnClose, "Here...");
                getDialog().dismiss();
            }
        });

        imageList = (ArrayList<GalleryItem>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

        myViewPagerAdapter = new GalleryPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " de " + imageList.size());
        GalleryItem image = imageList.get(position);
    }

    public class GalleryPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public GalleryPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.gallery_image_fullscreen, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            GalleryItem image = imageList.get(position);

            if(image.getUrlLarge() != null){
                Glide.with(getActivity()).load(image.getUrlLarge())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);
            }
            else if(image.getBitmap() != null){
                imageViewPreview.setImageBitmap(image.getBitmap());
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
