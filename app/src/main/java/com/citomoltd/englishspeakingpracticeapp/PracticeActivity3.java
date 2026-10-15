package com.citomoltd.englishspeakingpracticeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PracticeActivity3 extends AppCompatActivity {
    TextView txtDialog;
    EditText edtUserResponse;
    Button btnSpeak, btnNext, btnSubmitResponse, btnRepeat, btnSkip;
    Spinner spinnerSpeaker;
    List<DialogLine> dialogLines;
    int currentLine = 0;
    int correctResponses = 0;
    TextToSpeech textToSpeech;
    String selectedSpeaker = "Speaker1";
    boolean isSubmitted = false;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practice3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        txtDialog = findViewById(R.id.txtDialog);
        edtUserResponse = findViewById(R.id.edtUserResponse);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnNext = findViewById(R.id.btnNext);
        btnSubmitResponse = findViewById(R.id.btnSubmitResponse);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnSkip = findViewById(R.id.btnSkip);
        spinnerSpeaker = findViewById(R.id.spinnerSpeaker);
        adContainer = findViewById(R.id.adContainer);

        String dialogSummary = getIntent().getStringExtra("dialog");
        dialogLines = parseDialog(dialogSummary);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1.0f);
            }
        });
        AdHelper.loadBannerAd(this, adContainer);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getAllSpeakers(dialogLines));
        spinnerSpeaker.setAdapter(adapter);
        spinnerSpeaker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpeaker = parent.getItemAtPosition(position).toString();
                updateDialog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        updateDialog();


        Animation entryAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        edtUserResponse.startAnimation(entryAnim);


        btnSpeak.setOnClickListener(v -> {
            if (currentLine < dialogLines.size()) {
                String message = dialogLines.get(currentLine).message;
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });


        btnSubmitResponse.setOnClickListener(v -> {
            if (currentLine < dialogLines.size()) {
                String userResponse = edtUserResponse.getText().toString().trim();
                if (userResponse.isEmpty()) {
                    Toast.makeText(this, "Please enter a response!", Toast.LENGTH_SHORT).show();
                } else {
                    isSubmitted = true;
                    String correct = dialogLines.get(currentLine).message;
                    if (userResponse.equalsIgnoreCase(correct)) {
                        correctResponses++;
                        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Shake animation
                        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                        edtUserResponse.startAnimation(shake);

                        // Red flash background temporarily

                        edtUserResponse.setBackgroundColor(Color.parseColor("#D32F2F")); // light red
                        edtUserResponse.postDelayed(() -> {
                            edtUserResponse.setBackgroundResource(R.drawable.edittext_background);
                        }, 600); // after shake

                        Toast.makeText(this, "Expected: " + correct, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        btnNext.setOnClickListener(v -> {
            if (!isSubmitted) {
                Toast.makeText(this, "Please submit your response first!", Toast.LENGTH_SHORT).show();
                return;
            }

            currentLine++;
            edtUserResponse.setText("");
            isSubmitted = false;

            if (currentLine < dialogLines.size()) {
                updateDialog();
            } else {
                txtDialog.setText("End of dialog.\nCorrect: " + correctResponses + "/" + dialogLines.size());
            }
        });

        btnRepeat.setOnClickListener(v -> {
            currentLine = 0;
            correctResponses = 0;
            isSubmitted = false;
            edtUserResponse.setText("");
            updateDialog();
        });

        btnSkip.setOnClickListener(v -> {
            currentLine++;
            if (currentLine < dialogLines.size()) {
                isSubmitted = false;
                updateDialog();
            } else {
                txtDialog.setText("End of dialog.\nCorrect: " + correctResponses + "/" + dialogLines.size());
            }
        });
    }

    private void updateDialog() {
        if (currentLine < dialogLines.size()) {
            DialogLine line = dialogLines.get(currentLine);
            txtDialog.setText(line.speaker + ": " + line.message);
            txtDialog.setTextColor(line.speaker.equalsIgnoreCase(selectedSpeaker) ? Color.BLUE : Color.GRAY);

            if (!line.speaker.equalsIgnoreCase(selectedSpeaker)) {
                textToSpeech.speak(line.message, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

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
                currentSpeaker = line.substring(0, colonIndex).trim();
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

    private List<String> getAllSpeakers(List<DialogLine> lines) {
        Set<String> speakers = new LinkedHashSet<>();
        for (DialogLine line : lines) {
            speakers.add(line.speaker);
        }
        return new ArrayList<>(speakers);
    }

    private static class DialogLine {
        String speaker, message;

        DialogLine(String speaker, String message) {
            this.speaker = speaker;
            this.message = message;
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }
}
