package co.bongga.touristeando.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Service;

/**
 * Created by bongga on 2/8/17.
 */

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesHolder> {
    private Context context;
    private List<Service> serviceList;

    public ServicesAdapter(Context context, List<Service> serviceList){
        this.context = context;
        this.serviceList = serviceList;
    }

    @Override
    public ServicesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_services, parent, false);

        return new ServicesAdapter.ServicesHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicesHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.name.setText(service.getName());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServicesHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ServicesHolder(View view){
            super(view);

            name = (TextView) view.findViewById(R.id.name);
        }
    }
}
