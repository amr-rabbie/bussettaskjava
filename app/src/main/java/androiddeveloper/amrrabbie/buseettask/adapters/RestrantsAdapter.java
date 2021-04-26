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
import androiddeveloper.amrrabbie.buseettask.model.ResultsItem;
import androiddeveloper.amrrabbie.buseettask.ui.OnRestrantslistClick;

public class RestrantsAdapter extends RecyclerView.Adapter<RestrantsAdapter.RestrantsViewHolder> implements View.OnClickListener {

    List<ResultsItem> restrantslist;
    Context mContext;
    OnRestrantslistClick onRestrantslistClick;

    public RestrantsAdapter(Context mContext, OnRestrantslistClick onRestrantslistClick) {
        this.mContext = mContext;
        this.onRestrantslistClick = onRestrantslistClick;
    }

    public void setList(List<ResultsItem> restrantslist){
        this.restrantslist=restrantslist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RestrantsAdapter.RestrantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.restrant_item,parent,false);
        v.setOnClickListener(this);
        return new RestrantsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestrantsAdapter.RestrantsViewHolder holder, int position) {
        holder.name.setText(restrantslist.get(position).getName());

        List<String> types=restrantslist.get(position).getTypes();

        String resttypes="";

        for(int i=0;i<types.size();i++){
            if(i < types.size() -1){
                resttypes=resttypes + types.get(i) + " - ";
            }else{
                resttypes=resttypes + types.get(i);
            }
        }

        holder.type.setText(resttypes);


        Glide.with(mContext)
                .load(restrantslist.get(position).getIcon())
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
        ResultsItem restrant=restrantslist.get(pos);
        onRestrantslistClick.onListClick(restrant);
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
