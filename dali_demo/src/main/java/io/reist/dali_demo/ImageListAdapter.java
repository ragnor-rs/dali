package io.reist.dali_demo;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.reist.dali.Dali;
import io.reist.dali.ScaleMode;

/**
 * Created by Reist on 10.06.16.
 */
public abstract class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private static final ScaleMode[] SCALE_MODES = ScaleMode.values();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        View itemView = holder.itemView;
        itemView.setBackgroundColor(Color.rgb(
                (int)(Math.random() * 255),
                (int)(Math.random() * 255),
                (int)(Math.random() * 255)
        ));

        int i = holder.getAdapterPosition();
        ScaleMode scaleMode = SCALE_MODES[i % SCALE_MODES.length];

        TextView textView = holder.textView;
        textView.setText(scaleMode.name());
        textView.setTextColor(Color.WHITE);

        String url = getUrl(i);

        AdapterImageView imageView = holder.imageView;
        imageView.setPosition(i);
        Dali.with(itemView)
                .load(url)
                .inCircle(true)
                .scaleMode(scaleMode)
                .into(imageView, false);

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
        private final TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (AdapterImageView) itemView.findViewById(R.id.image_view);
            textView = (TextView) itemView.findViewById(R.id.text_view);
        }

    }

}
