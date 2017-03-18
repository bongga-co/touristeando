package co.bongga.touristeando.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import co.bongga.touristeando.R;
import co.bongga.touristeando.adapters.GalleryAdapter;
import co.bongga.touristeando.fragments.GalleryFragment;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.interfaces.RecyclerClickListener;
import co.bongga.touristeando.utils.DataManager;
import co.bongga.touristeando.utils.RecyclerItemClickListener;
import co.bongga.touristeando.utils.UtilityManager;

public class Gallery extends AppCompatActivity {

    private ArrayList<co.bongga.touristeando.models.Gallery> imageList;
    private ProgressDialog dialog;
    private GalleryAdapter galleryAdapter;
    private RecyclerView galleryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryRecycler = (RecyclerView) findViewById(R.id.gallery_grid);

        dialog = UtilityManager.showLoader(this, getString(R.string.loader_message));
        imageList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(getApplicationContext(), imageList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        galleryRecycler.setLayoutManager(mLayoutManager);
        galleryRecycler.setItemAnimator(new DefaultItemAnimator());
        galleryRecycler.setAdapter(galleryAdapter);

        galleryRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, galleryRecycler, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();;
                bundle.putSerializable("images", imageList);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                GalleryFragment newFragment = GalleryFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchGalleryImages();
    }

    private void fetchGalleryImages(){
        dialog.show();

        String id = getIntent().getStringExtra("placeId");
        DataManager.willGetPlaceGallery(id, new DataCallback() {
            @Override
            public void didReceiveData(List<Object> response) {
                dialog.dismiss();

                if(response != null){
                    List<co.bongga.touristeando.models.Gallery> data = UtilityManager.objectFilter(response, co.bongga.touristeando.models.Gallery.class);
                    if(data.size() > 0){
                        for(co.bongga.touristeando.models.Gallery galleryItem : data){
                            imageList.add(galleryItem);
                        }
                        galleryAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
