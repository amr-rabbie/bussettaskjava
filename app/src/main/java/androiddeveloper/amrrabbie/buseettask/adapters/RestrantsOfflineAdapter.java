package androiddeveloper.amrrabbie.buseettask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import androiddeveloper.amrrabbie.buseettask.R;
import androiddeveloper.amrrabbie.buseettask.db.Restrant;
import androiddeveloper.amrrabbie.buseettask.model.ResultsItem;
import androiddeveloper.amrrabbie.buseettask.ui.OnRestrantslistClick;

public class RestrantsOfflineAdapter extends RecyclerView.Adapter<RestrantsOfflineAdapter.RestrantsViewHolder> implements View.OnClickListener {

    List<Restrant> restrantslist;
    Context mContext;
    OnRestrantslistClick onRestrantslistClick;

    public Restrant getItemAt(int position){
        return restrantslist.get(position);
    }

    public RestrantsOfflineAdapter(Context mContext, OnRestrantslistClick onRestrantslistClick) {
        this.mContext = mContext;
        this.onRestrantslistClick = onRestrantslistClick;
    }

    public void setList(List<Restrant> restrantslist){
        this.restrantslist=restrantslist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RestrantsOfflineAdapter.RestrantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.restrant_item,parent,false);
        v.setOnClickListener(this);
        return new RestrantsOfflineAdapter.RestrantsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestrantsOfflineAdapter.RestrantsViewHolder holder, int position) {
        holder.name.setText(restrantslist.get(position).getName());


        holder.type.setText(restrantslist.get(position).getType());


        Glide.with(mContext)
                .load(restrantslist.get(position).getImg())
                .into(holder.img);

        holder.cview.setTag(position);
    }

    @Override
    public int getItemCount() {
        return restrantslist.size();
    }

    @Override
    public void onClick(View v) {
        int pos=(int)v.getTag();
        Restrant restrant=restrantslist.get(pos);
        onRestrantslistClick.onListOfflineClick(restrant);
    }

    public class RestrantsViewHolder extends RecyclerView.ViewHolder {
        CardView cview;
        TextView name,type;
        ImageView img;
        public RestrantsViewHolder(@NonNull View v) {
            super(v);
            cview=v.findViewById(R.id.cview);
            name=v.findViewById(R.id.name);
            type=v.findViewById(R.id.type);
            img=v.findViewById(R.id.img);
        }
    }
}
