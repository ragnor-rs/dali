package io.reist.dali_demo;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reist.dali.Dali;

/**
 * Created by Reist on 10.06.16.
 */
public abstract class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private final int itemLayoutRes;
    private final Dali dali;

    public ImageListAdapter(Dali dali, int itemLayoutRes) {
        this.itemLayoutRes = itemLayoutRes;
        this.dali = dali;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(itemLayoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        AdapterImageView imageView = holder.imageView;

        int i = holder.getAdapterPosition();
        String url = getUrl(i);

        imageView.setPosition(i);

        dali.load(url)
                .inCircle(true)
                .into(imageView, false);

        imageView.setBackgroundColor(Color.rgb(
                (int)(Math.random() * 255),
                (int)(Math.random() * 255),
                (int)(Math.random() * 255))
        );

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.imageView.setImageBitmap(null);
    }

    protected abstract String getUrl(int i);

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
