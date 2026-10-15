package com.citomoltd.englishspeakingpracticeapp;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PracticeActivity2 extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnSelectSpeaker;
    TextView txtSpeaker;
    String dialogSummary;
    List<DialogLine> dialogLines;
    DialogAdapter dialogAdapter;

    // Selected speaker
    String selectedSpeaker = "";
    private FrameLayout adContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practice2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerView);
        btnSelectSpeaker = findViewById(R.id.btnSelectSpeaker);
        txtSpeaker = findViewById(R.id.txtSpeaker);
        adContainer = findViewById(R.id.adContainer);

        dialogSummary = getIntent().getStringExtra("dialog");
        dialogLines = parseDialog(dialogSummary);
        AdHelper.loadBannerAd(this, adContainer);

        dialogAdapter = new DialogAdapter(this, dialogLines, selectedSpeaker);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dialogAdapter);

        // Button click to select speaker
        btnSelectSpeaker.setOnClickListener(v -> {
            // Toggle between speakers by selecting their names
            if (selectedSpeaker.equals(dialogLines.get(0).speaker)) {
                selectedSpeaker = dialogLines.get(1).speaker; // toggle between speakers dynamically
            } else {
                selectedSpeaker = dialogLines.get(0).speaker;
            }

            // Update the selected speaker's name display
            txtSpeaker.setText("Selected Speaker: " + selectedSpeaker);

            // Notify adapter to refresh the RecyclerView
            dialogAdapter.updateSelectedSpeaker(selectedSpeaker);
        });
    }

    // Parse the dialog text into individual dialog lines
    private List<DialogLine> parseDialog(String summary) {
        List<DialogLine> lines = new ArrayList<>();
        String[] splitLines = summary.split("\n");

        String currentSpeaker = "";
        StringBuilder currentMessage = new StringBuilder();

        for (String line : splitLines) {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                if (currentMessage.length() > 0) {
                    lines.add(new DialogLine(currentSpeaker, currentMessage.toString()));
                }
                currentSpeaker = line.substring(0, colonIndex).trim(); // Get the speaker's name directly
                currentMessage = new StringBuilder(line.substring(colonIndex + 1).trim());
            } else {
                currentMessage.append(" ").append(line.trim());
            }
        }

        if (currentMessage.length() > 0) {
            lines.add(new DialogLine(currentSpeaker, currentMessage.toString()));
        }

        return lines;
    }

    // Class to hold dialog lines
    private static class DialogLine {
        String speaker;
        String message;

        DialogLine(String speaker, String message) {
            this.speaker = speaker;
            this.message = message;
        }
    }

    // Adapter for RecyclerView
    public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogViewHolder> {

        private List<DialogLine> dialogLines;
        private String selectedSpeaker;
        private Context context;

        DialogAdapter(Context context, List<DialogLine> dialogLines, String selectedSpeaker) {
            this.context = context;
            this.dialogLines = dialogLines;
            this.selectedSpeaker = selectedSpeaker;
        }

        @Override
        public DialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the layout for the dialog item
            return new DialogViewHolder(LayoutInflater.from(context).inflate(R.layout.dialog_item, parent, false));
        }

        @Override
        public void onBindViewHolder(DialogViewHolder holder, int position) {
            DialogLine line = dialogLines.get(position);

            // If the current line's speaker matches the selected speaker
            if (line.speaker.equals(selectedSpeaker)) {
                // Highlight the speaker's name and message in bold
                holder.speaker.setText(line.speaker);
                holder.speaker.setTextColor(ContextCompat.getColor(context, R.color.highlight)); // Bold name color
                holder.speaker.setTypeface(null, Typeface.BOLD); // Make the speaker's name bold

                holder.message.setText(line.message);
                holder.message.setTextColor(ContextCompat.getColor(context, R.color.highlight)); // Highlight message color
                holder.message.setTypeface(null, Typeface.BOLD); // Make the message bold
            } else {
                // Dim the non-selected speaker's message and name
                holder.speaker.setText(line.speaker);
                holder.speaker.setTextColor(ContextCompat.getColor(context, R.color.dimmedText)); // Dimmed name color
                holder.speaker.setTypeface(null, Typeface.NORMAL); // Make the speaker's name normal

                holder.message.setText(line.message);
                holder.message.setTextColor(ContextCompat.getColor(context, R.color.dimmedText)); // Dimmed message color
                holder.message.setTypeface(null, Typeface.NORMAL); // Make the message normal
            }
        }

        @Override
        public int getItemCount() {
            return dialogLines.size();
        }

        void updateSelectedSpeaker(String selectedSpeaker) {
            this.selectedSpeaker = selectedSpeaker;
            notifyDataSetChanged(); // Refresh the RecyclerView
        }

        // ViewHolder for the dialog lines
        class DialogViewHolder extends RecyclerView.ViewHolder {
            TextView message;
            TextView speaker;

            DialogViewHolder(View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.message);
                speaker = itemView.findViewById(R.id.speaker); // Initialize the speaker TextView
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }


}