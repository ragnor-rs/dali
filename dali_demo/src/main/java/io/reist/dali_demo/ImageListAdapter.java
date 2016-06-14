package io.reist.dali_demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reist.dali.Dali;

/**
 * Created by Reist on 10.06.16.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AdapterImageView imageView = holder.imageView;
        int adapterPosition = holder.getAdapterPosition();
        String url = ImageService.positionToUrl(adapterPosition);
        imageView.setPosition(adapterPosition);
        Dali.load(url).placeholder(android.R.color.holo_green_dark).into(imageView, false);
    }

    @Override
    public int getItemCount() {
        return 1000;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AdapterImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (AdapterImageView) itemView.findViewById(R.id.image_view);
        }

    }

}
