package com.harun.rma_1_ime_prezime.drawing;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder> {
        private List<String> drawings;

        public DrawingAdapter(List<String> drawings) {
            this.drawings = drawings;
        }

        @NonNull
        @Override
        public DrawingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View textView = new TextView(parent.getContext());
            textView.setPadding(16, 16, 16, 16);
            return new DrawingViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull DrawingViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(drawings.get(position));
        }

        @Override
        public int getItemCount() {
            return drawings.size();
        }

        static class DrawingViewHolder extends RecyclerView.ViewHolder {
            public DrawingViewHolder(@NonNull View itemView) {
                super(itemView);
            }
    }
}
