package zo.dimyon.nudedetectionai;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageUtils {
    public static ByteBuffer preprocessImage(Bitmap bitmap, int inputSize) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3); // RGB input
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[inputSize * inputSize];
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        for (int pixelValue : intValues) {
            byteBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f); // Red
            byteBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);  // Green
            byteBuffer.putFloat((pixelValue & 0xFF) / 255.0f);         // Blue
        }

        return byteBuffer;
    }
}

