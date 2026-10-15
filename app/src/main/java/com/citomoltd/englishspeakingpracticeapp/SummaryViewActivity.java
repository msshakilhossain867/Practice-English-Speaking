package com.citomoltd.englishspeakingpracticeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class SummaryViewActivity extends AppCompatActivity {

    TextView textSummary, textTittle;

    float xDown = 0, yDown = 0;
    ImageView imageView;
    List<TextView> tabList = new ArrayList<>();
    int itemId;
    private FrameLayout adContainer;

    @SuppressLint({"ClickableViewAccessibility", "SuspiciousIndentation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textSummary = findViewById(R.id.textSummary);
        textTittle = findViewById(R.id.textTittle);
        imageView = findViewById(R.id.imageView);

        tabList.add(findViewById(R.id.tab_listen));
        tabList.add(findViewById(R.id.tab_quiz));
        tabList.add(findViewById(R.id.tab_practice));
        tabList.add(findViewById(R.id.tab_grammar));
        tabList.add(findViewById(R.id.tab_vocab));
        tabList.add(findViewById(R.id.tab_convo));
        adContainer = findViewById(R.id.adContainer);


        itemId = getIntent().getIntExtra("item_id", -1);
        String itemName = getIntent().getStringExtra("item_name");
        String image = getIntent().getStringExtra("image");
        int value = getIntent().getIntExtra("topic", 0);

        DbHelper dbHelper = new DbHelper(this);
        String summary;
        if (value == 1) {
            summary = dbHelper.getQuizSummary(itemId);
        } else if (value == 2) {
            summary = dbHelper.getQuizSummaryRegular(itemId);
        } else {
            summary = "";
        }

        AdHelper.loadBannerAd(this, adContainer);

        // 🔥 Format the summary text
        textSummary.setText(getFormattedDialog(summary));
        textTittle.setText(itemName);

        int images = SummaryViewActivity.this.getResources().getIdentifier(image, "drawable", SummaryViewActivity.this.getPackageName());


        if (images != 0) {
            imageView.setImageResource(images);
        } else {
            imageView.setImageResource(R.drawable.beginner); // fallback image
        }


        for (TextView tab : tabList) {
            tab.setOnClickListener(v -> {
                for (TextView t : tabList) {
                    t.setSelected(false);
                }
                tab.setSelected(true);

                int id = v.getId();
                Intent intent = null;
                if (id == R.id.tab_quiz) {

                    intent = new Intent(SummaryViewActivity.this, QuizActivity.class);
                    intent.putExtra("item_id", itemId);
                    intent.putExtra("tittle", itemName);
                    intent.putExtra("image", images);
                } else if (id == R.id.tab_practice) {
                    intent = new Intent(SummaryViewActivity.this, PracticeActivity.class);
                    intent.putExtra("dialog_summary", summary);
                } else if (id == R.id.tab_grammar) {
                    intent = new Intent(SummaryViewActivity.this, PracticeActivity2.class);
                    intent.putExtra("dialog", summary);
                } else if (id == R.id.tab_vocab) {
                    intent = new Intent(SummaryViewActivity.this, PracticeActivity3.class);
                    intent.putExtra("dialog", summary);
                } else if (id == R.id.tab_convo) {
                    intent = new Intent(SummaryViewActivity.this, PracticeActivity4.class);
                    intent.putExtra("dialog", summary);
                }

                if (intent != null) {
                    startActivity(intent);
                }
            });
        }


        // Default selected
        tabList.get(0).setSelected(true);


        // status drop down========================
        /*btnStartQuiz.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:

                        xDown=event.getX();
                        yDown=event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float movedX,movedY;
                        movedX=event.getX();
                        movedY=event.getY();
                        //calculate how much the user moved
                        float distanceX=movedX-xDown;
                        float distanceY=movedY-yDown;
                        // now move the view to that position
                        btnStartQuiz.setX(btnStartQuiz.getX()+distanceX);
                        btnStartQuiz.setY(btnStartQuiz.getY()+distanceY);
                }
                return true;
            }
        });*/
    }

    private SpannableStringBuilder getFormattedDialog(String summary) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String[] lines = summary.split("\n");

        String currentSpeaker = "";
        StringBuilder currentMessage = new StringBuilder();

        for (String line : lines) {
            int colonIndex = line.indexOf(":");

            if (colonIndex != -1) {
                // If a new speaker is found
                if (currentMessage.length() > 0) {
                    // Append the previous message and speaker
                    int start = builder.length();
                    builder.append(currentSpeaker).append(" ");
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(currentMessage.toString()).append("\n");

                    // Add a line break for the next speaker
                    builder.append("\n");
                }

                // Extract speaker name and message
                currentSpeaker = line.substring(0, colonIndex + 1);
                String message = line.substring(colonIndex + 1).trim();

                // Start a new message for the new speaker
                currentMessage = new StringBuilder(message);
            } else {
                // Continue appending to the current message (if it is a continuation of the previous speaker's message)
                currentMessage.append(" ").append(line.trim());
            }
        }

        // Append the last message
        if (currentMessage.length() > 0) {
            int start = builder.length();
            builder.append(currentSpeaker).append(" ");
            builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(currentMessage.toString()).append("\n");
        }

        return builder;
    }

    @Override
    protected void onResume() {
        super.onResume();

        tabList.get(0).setSelected(true);
        tabList.get(0).performClick();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }


}
