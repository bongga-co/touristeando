package co.bongga.toury.interfaces;

import android.view.View;

/**
 * Created by bongga on 1/17/17.
 */

public interface RecyclerClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
