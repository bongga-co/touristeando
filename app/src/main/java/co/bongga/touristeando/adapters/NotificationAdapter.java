package co.bongga.touristeando.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Locale;
import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Notification;
import io.realm.RealmList;

/**
 * Created by bongga on 2/13/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RealmList<Notification> notificationList = new RealmList<>();
    private Context context;

    public  NotificationAdapter(){

    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = notificationList.get(position);
        int type;

        if(notification.getType() == Notification.GNAL_TYPE){
            type = Notification.GNAL_TYPE;
        }
        else{
            type = Notification.OFFER_TYPE;
        }

        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        context = parent.getContext();

        if(viewType == Notification.GNAL_TYPE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_item_with_image, parent, false);
            holder = new NotificationWithImageHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notification_item, parent, false);
            holder = new NotificationHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder parentHolder, int position) {
        Notification notification = notificationList.get(position);

        if(parentHolder instanceof NotificationWithImageHolder){
            NotificationWithImageHolder holder = (NotificationWithImageHolder) parentHolder;

            holder.title.setText(notification.getTitle());
            holder.description.setText(notification.getDescription());

            if(notification.getImage() != null){
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(notification.getImage())
                        .placeholder(R.drawable.placeholder_img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.image);
            }
            else{
                holder.image.setVisibility(View.GONE);
            }
        }
        else{
            NotificationHolder holder = (NotificationHolder) parentHolder;

            holder.title.setText(notification.getTitle());
            holder.description.setText(notification.getDescription());
            holder.discount.setText(String.format(Locale.getDefault(), "%d%%", (int) (notification.getDiscount() * 100)));
            holder.date.setText(String.format(Locale.getDefault(), "Válido hasta el %s", notification.getExpityDate()));
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void replaceData(RealmList<Notification> items) {
        setList(items);
        notifyDataSetChanged();
    }

    public void setList(RealmList<Notification> list) {
        this.notificationList = list;
    }

    public void addItem(Notification pushMessage) {
        notificationList.add(0, pushMessage);
        notifyItemInserted(0);
    }

    public void removeItem(int position) {
        notificationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notificationList.size());
    }

    public Notification fetchItem(int position){
        return notificationList.get(position);
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView discount;
        public TextView date;

        public NotificationHolder(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.nt_title);
            description = (TextView) view.findViewById(R.id.nt_description);
            discount = (TextView) view.findViewById(R.id.nt_discount);
            date = (TextView) view.findViewById(R.id.nt_expiry_date);
        }
    }

    public static class NotificationWithImageHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView image;

        public NotificationWithImageHolder(View view){
            super(view);

            title = (TextView) view.findViewById(R.id.nt_img_title);
            description = (TextView) view.findViewById(R.id.nt_img_description);
            image = (ImageView) view.findViewById(R.id.nt_image);
        }
    }
}
