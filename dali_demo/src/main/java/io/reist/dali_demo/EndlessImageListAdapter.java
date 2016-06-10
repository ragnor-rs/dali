package io.reist.dali_demo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.reist.dali.Dali;

/**
 * Created by Reist on 10.06.16.
 */
public class EndlessImageListAdapter extends RecyclerView.Adapter<EndlessImageListAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new ImageView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Dali.load(DaliUtils.toDaliUrl(position)).into(holder.itemView, false);
    }

    @Override
    public int getItemCount() {
        return 1000;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
