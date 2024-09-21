package zo.dimyon.nudedetectionai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView selectedImage;
    private TextView resultText;
    private NSFWDetection nsfwDetection;
    private ExecutorService executorService; // Thread executor for background tasks
    private static final String TAG = "MADARA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedImage = findViewById(R.id.selectedImage);
        resultText = findViewById(R.id.resultText);
        Button imageChooserButton = findViewById(R.id.imageChooserButton);

        // Initialize the NSFWDetection class
        try {
            nsfwDetection = new NSFWDetection(this, R.raw.model); // Load from raw folder
        } catch (IOException e) {
            Log.e(TAG, "onCreate: ", e);
        }

        // Initialize ExecutorService with a single background thread
        executorService = Executors.newSingleThreadExecutor();

        // Set button click listener to choose image from gallery
        imageChooserButton.setOnClickListener(v -> openGallery());
    }

    // Open gallery to select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                selectedImage.setImageBitmap(bitmap); // Display selected image

                // Run preprocessing and detection on a background thread
                executorService.execute(() -> processImageInBackground(bitmap));

            } catch (IOException e) {
                Log.e(TAG, "onActivityResult: ", e);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void processImageInBackground(Bitmap bitmap) {
        // Preprocess the image for TensorFlow Lite model
        ByteBuffer inputBuffer = ImageUtils.preprocessImage(bitmap, 224); // Assuming model input size is 224x224

        // Run TensorFlow Lite model to detect NSFW content
        float[] detectionResults = nsfwDetection.runInference(inputBuffer);

        // Update UI on the main thread with the result
        runOnUiThread(() -> {
            // Get the label with the highest confidence
            String[] labels = {"drawings", "neutral", "porn", "sexy"};

            // Find the index of the highest confidence
            int maxIndex = 0;
            float maxConfidence = detectionResults[0];

            for (int i = 1; i < detectionResults.length; i++) {
                if (detectionResults[i] > maxConfidence) {
                    maxConfidence = detectionResults[i];
                    maxIndex = i;
                }
            }

            // Display the label with the highest confidence
            resultText.setText("Detected: " + labels[maxIndex] + " (Confidence: " + maxConfidence + ")");
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

}
