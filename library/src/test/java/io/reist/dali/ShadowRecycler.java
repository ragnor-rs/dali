package io.reist.dali;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Emulates {@link android.support.v7.widget.RecyclerView}. Call {@link #render()} to initiate view
 * creation and data binding.
 *
 * Created by Reist on 15.06.16.
 */
class ShadowRecycler<V extends View> {

    private final int windowHeight;
    private final Adapter<V> adapter;

    private int position;

    private final List<ViewHolder<V>> viewHolders = new ArrayList<>();

    ShadowRecycler(int windowHeight, Adapter<V> adapter) {
        this.windowHeight = windowHeight;
        this.adapter = adapter;
    }

    void render() {

        int dataLength = adapter.getCount();

        int windowStart = this.position;
        int windowEnd = Math.min(this.position + windowHeight, dataLength);

        // recycle invisible views
        for (int i = 0; i < windowStart; i++) {
            for (ViewHolder<V> holder : viewHolders) {
                if (holder.i == i) {
                    holder.i = -1;
                }
            }
        }

        // bind data to visible views
        for (int i = windowStart; i < windowEnd; i++) {
            ViewHolder<V> viewHolder = createOrGet(i);
            adapter.bindView(viewHolder.v, i);
        }

        // recycle invisible views
        for (int i = windowEnd; i < dataLength; i++) {
            for (ViewHolder<V> holder : viewHolders) {
                if (holder.i == i) {
                    holder.i = -1;
                }
            }
        }

    }

    private ViewHolder<V> createOrGet(int i) {
        for (ViewHolder<V> holder : viewHolders) {
            if (holder.i == -1) {
                holder.i = i;
                return holder;
            }
        }
        ViewHolder<V> holder = new ViewHolder<>(adapter.createView(i), i);
        viewHolders.add(holder);
        return holder;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public Adapter<V> getAdapter() {
        return adapter;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    private static class ViewHolder<V> {

        private final V v;
        private int i;

        public ViewHolder(V v, int i) {
            this.v = v;
            this.i = i;
        }

    }

    interface Adapter<V extends View> {

        int getCount();

        V createView(int i);

        void bindView(V v, int i);

    }

}
