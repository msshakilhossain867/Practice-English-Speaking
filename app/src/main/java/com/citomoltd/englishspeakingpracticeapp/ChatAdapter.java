package com.citomoltd.englishspeakingpracticeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<DialogLine> dialogLines;
    private String currentSpeaker;
    private int selectedLine = -1; // Initially no line is selected

    public ChatAdapter(List<DialogLine> dialogLines, String currentSpeaker, int selectedLine) {
        this.dialogLines = dialogLines;
        this.currentSpeaker = currentSpeaker;
        this.selectedLine = selectedLine;
    }

    public void setSelectedLine(int selectedLine) {
        this.selectedLine = selectedLine;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        DialogLine dialogLine = dialogLines.get(position);

        if (position == selectedLine) {
            holder.txtMessage.setBackgroundResource(R.drawable.selected_message_bg);
        } else {
            holder.txtMessage.setBackgroundResource(R.drawable.default_message_bg);
        }

        // Bold and color based on current speaker
        if (dialogLine.speaker.equals(currentSpeaker)) {
            holder.txtMessage.setText(dialogLine.message);
            holder.txtMessage.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.txtMessage.setTextColor(android.graphics.Color.BLACK); // গাঢ়
        } else {
            holder.txtMessage.setText(dialogLine.speaker + ": " + dialogLine.message);
            holder.txtMessage.setTypeface(null, android.graphics.Typeface.NORMAL);
            holder.txtMessage.setTextColor(android.graphics.Color.GRAY); // হালকা
        }
    }

    @Override
    public int getItemCount() {
        return dialogLines.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }

    public void setCurrentSpeaker(String speaker) {
        this.currentSpeaker = speaker;
    }

}
