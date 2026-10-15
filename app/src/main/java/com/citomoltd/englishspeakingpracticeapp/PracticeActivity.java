package com.citomoltd.englishspeakingpracticeapp;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class PracticeActivity extends AppCompatActivity {
    TextView textViewLine, textViewSpeaker;
    Button btnNext, btnPrev, btnRepeat;
    RadioGroup radioGroupSpeakers;

    ArrayList<DialogLine> dialogList = new ArrayList<>();
    int currentIndex = 0;
    String selectedSpeaker = "";
    TextToSpeech tts;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        textViewLine = findViewById(R.id.textViewLine);
        textViewSpeaker = findViewById(R.id.textViewSpeaker);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnRepeat = findViewById(R.id.btnRepeat);
        radioGroupSpeakers = findViewById(R.id.radioGroupSpeakers);
        adContainer = findViewById(R.id.adContainer);

        // Initialize TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        // Get dialog from intent
        String summary = getIntent().getStringExtra("dialog_summary");
        AdHelper.loadBannerAd(this, adContainer);

        // Parse dialog lines
        for (String line : summary.split("\n")) {
            int idx = line.indexOf(":");
            if (idx != -1) {
                String speaker = line.substring(0, idx).trim();
                String message = line.substring(idx + 1).trim();
                dialogList.add(new DialogLine(speaker, message));
            }
        }

        // Set up speaker selection
        setupSpeakerOptions();

        // NEXt BUTTON CLICK LISTENER
        btnNext.setOnClickListener(v -> {
            if (!selectedSpeaker.isEmpty()) {  // Check if a speaker is selected
                if (currentIndex < dialogList.size() - 1) {
                    currentIndex++;
                    stopTextToSpeech();
                    showLine();
                } else {
                    Toast.makeText(PracticeActivity.this, "You've reached the end of the dialog.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PracticeActivity.this, "Please select a speaker first.", Toast.LENGTH_SHORT).show();
            }
        });

// PREV BUTTON CLICK LISTENER
        btnPrev.setOnClickListener(v -> {
            if (!selectedSpeaker.isEmpty()) {  // Check if a speaker is selected
                if (currentIndex > 0) {
                    currentIndex--;
                    stopTextToSpeech();
                    showLine();
                } else {
                    Toast.makeText(PracticeActivity.this, "You're at the beginning of the dialog.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PracticeActivity.this, "Please select a speaker first.", Toast.LENGTH_SHORT).show();
            }
        });

        btnRepeat.setOnClickListener(v -> {
            currentIndex = 0;  // Reset to the first line
            stopTextToSpeech();
            showLine();
            btnRepeat.setVisibility(View.GONE);  // Hide Repeat button after restarting dialog
        });


        // Button listeners
      /*  btnNext.setOnClickListener(v -> {
            if (currentIndex < dialogList.size() - 1) {
                currentIndex++;
                showLine();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showLine();
            }
        });*/

        // Show first line
        showLine();
    }

    void setupSpeakerOptions() {
        ArrayList<String> speakers = new ArrayList<>();
        for (DialogLine line : dialogList) {
            if (!speakers.contains(line.speaker)) {
                speakers.add(line.speaker);
            }
        }

        for (String speaker : speakers) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(speaker);
            radioGroupSpeakers.addView(radioButton);
        }

        radioGroupSpeakers.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = findViewById(checkedId);
            if (selected != null) {
                selectedSpeaker = selected.getText().toString();
                showLine();
            }
        });
    }


    /*void showLine() {
        if (dialogList.isEmpty()) return;

        DialogLine line = dialogList.get(currentIndex);
        textViewSpeaker.setText(line.speaker + ":");
        textViewLine.setText(line.message);

        // Highlight logic
        if (line.speaker.equals(selectedSpeaker)) {
            textViewLine.setTextColor(Color.BLACK);
            textViewSpeaker.setTextColor(Color.BLUE);
        } else {
            textViewLine.setTextColor(Color.GRAY);
            textViewSpeaker.setTextColor(Color.DKGRAY);
            speak(line.message);  // Call TextToSpeech for non-selected speaker
        }
    }*/

    // Check if it's the first or last line to disable buttons

    private void stopTextToSpeech() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
    }

    void showLine() {
        if (dialogList.isEmpty()) return;

        DialogLine line = dialogList.get(currentIndex);
        textViewSpeaker.setText(line.speaker + ":");
        textViewLine.setText(line.message);


        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (line.speaker.equals(selectedSpeaker)) {

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                // Dark mode - use light color
                textViewLine.setTextColor(Color.WHITE);
                textViewSpeaker.setTextColor(Color.GREEN);

            } else {
                textViewLine.setTextColor(Color.BLACK);
                textViewSpeaker.setTextColor(Color.BLUE);
            }

        } else {
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                // Dark mode - use light color
                textViewLine.setTextColor(Color.GRAY);
                textViewSpeaker.setTextColor(Color.LTGRAY);
            } else {
                // Light mode - normal dark colors
                textViewLine.setTextColor(Color.GRAY);
                textViewSpeaker.setTextColor(Color.DKGRAY);
            }
            speak(line.message);
        }


      /*  // Highlight logic
        if (line.speaker.equals(selectedSpeaker)) {
            textViewLine.setTextColor(Color.BLACK);
            textViewSpeaker.setTextColor(Color.BLUE);
        } else {
            textViewLine.setTextColor(Color.GRAY);
            textViewSpeaker.setTextColor(Color.DKGRAY);
            speak(line.message);
        }
*/
        // Disable Next/Prev buttons when dialog is over
        if (currentIndex == 0) {
            btnPrev.setEnabled(false);  // Disable Prev button at the first line
        } else {
            btnPrev.setEnabled(true);
        }

        if (currentIndex == dialogList.size() - 1) {
            btnNext.setEnabled(false);  // Disable Next button at the last line
        } else {
            btnNext.setEnabled(true);
        }

        if (currentIndex == dialogList.size() - 1) {
            btnNext.setEnabled(false);  // Disable Next button at the last line
            btnRepeat.setVisibility(View.VISIBLE);  // Show Repeat button at the end
        } else {
            btnNext.setEnabled(true);
            btnRepeat.setVisibility(View.GONE);  // Hide Repeat button during normal dialog flow
        }
    }


    private void speak(String text) {
        if (tts != null) {
            // Set the speech rate (1.0 is the normal rate, < 1.0 is slower, > 1.0 is faster)
            tts.setSpeechRate(0.4f); // For slower speech (you can adjust this value)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }

    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    static class DialogLine {
        String speaker;
        String message;

        DialogLine(String speaker, String message) {
            this.speaker = speaker;
            this.message = message;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }
}