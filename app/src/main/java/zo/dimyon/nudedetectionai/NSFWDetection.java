package zo.dimyon.nudedetectionai;

import android.content.Context;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.io.File;

public class NSFWDetection {
    private final Interpreter tflite;

    public NSFWDetection(Context context, int rawResId) throws IOException {
        tflite = new Interpreter(loadModelFile(context, rawResId));
    }

    // Load the model from the raw folder
    private MappedByteBuffer loadModelFile(Context context, int rawResId) throws IOException {
        // Open the raw resource as an input stream
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        File tempFile = File.createTempFile("model", ".tflite", context.getCacheDir());
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, read);
        }

        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();

        // Open the file and map it to memory
        FileInputStream fileInputStream = new FileInputStream(tempFile);
        FileChannel fileChannel = fileInputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, tempFile.length());
    }

    public float[] runInference(ByteBuffer inputBuffer) {
        // Assuming the output shape is [1, 4], so we need an array of size 4.
        float[][] output = new float[1][4];  // Adjust to match the expected output shape.

        // Run the inference using the input buffer and store the output in the float array.
        tflite.run(inputBuffer, output);

        // Return the first array containing the four probability values.
        return output[0];
    }

}

