package io.reist.dali_demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.reist.dali.Dali;

/**
 * Created by Reist on 10.06.16.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Dali.load(DaliUtils.toDaliUrl(position)).into(holder.imageView, false);
    }

    @Override
    public int getItemCount() {
        return 1000;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }

    }

}
