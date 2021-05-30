package xyz.dipper.live.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dipper
 * @date 2021/5/29 15:39
 */
class BaseCamera implements Camera.PreviewCallback, Camera.ErrorCallback {
    private static final String TAG = BaseCamera.class.getSimpleName();
    private static final int PREVIEW_WIDTH = 1280;
    private static final int PREVIEW_HEIGHT = 720;
    private static final int FRAME_RATE = 25;

    private Camera camera;
    private CameraChannel channel = CameraChannel.UNKNOWN;
    private List<YuvFrameCallback> callbacks;
    private Handler callbackHandler;
    private HandlerThread callbackThread;

    private boolean openStatus;
    private boolean previewStatus;

    public boolean open(@NonNull CameraChannel channel) {
        camera = Camera.open(channel.getId());
        if (camera == null) {
            return false;
        }
        openStatus = true;
        this.channel = channel;
        return true;
    }

    public void close() {
        if (!isCameraOpened()) {
            return;
        }
        stopPreview();
        camera.release();
        camera = null;
        openStatus = false;
    }

    public boolean isCameraOpened() {
        return camera != null && openStatus;
    }

    public CameraChannel getChannel() {
        return camera != null ? CameraChannel.UNKNOWN : channel;
    }

    private static void initCameraParameters(@NotNull Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFrameRate(FRAME_RATE);
        parameters.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        parameters.setPreviewFormat(ImageFormat.YV12);
        camera.setParameters(parameters);
    }

    public void addYuvFrameCallback(YuvFrameCallback callback) {
        if (callback == null) {
            return;
        }
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(callback);
    }

    public void removeYuvFrameCallback(YuvFrameCallback callback) {
        if (callback == null) {
            return;
        }
        if (callbacks == null) {
            return;
        }
        callbacks.remove(callback);
    }

    public void startPreview(SurfaceHolder surfaceHolder) {
        if (!isCameraOpened()) {
            return;
        }
        initCameraParameters(camera);
        camera.setErrorCallback(this);
        initCameraPreviewBuffer(camera);
        try {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceHolder);

        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        previewStatus = true;
    }

    public void stopPreview() {
        if (!isCameraOpened() || !isCameraPreviewing()) {
            return;
        }
        camera.stopPreview();
        previewStatus = false;
        if (callbacks != null) {
            callbacks.clear();
        }
        if (callbackThread != null) {
            callbackThread.quitSafely();
            callbackThread = null;
        }
    }

    public boolean isCameraPreviewing() {
        return previewStatus;
    }

    private void initCameraPreviewBuffer(Camera camera) {
        final int bufferSize = 5;
        int length = PREVIEW_WIDTH * PREVIEW_HEIGHT * 3 / 2;
        for (int i = 0; i < bufferSize; i++) {
            camera.addCallbackBuffer(new byte[length]);
        }
        camera.setPreviewCallbackWithBuffer(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (data == null || data.length < 1) {
            return;
        }
        final long timeUs = System.nanoTime() / 1000;
        final byte[] bytes = Arrays.copyOf(data, data.length);
        if (callbackHandler == null) {
            callbackThread = new HandlerThread("YuvFrameCallback");
            callbackThread.start();
            callbackHandler = new Handler(callbackThread.getLooper());
        }
        if (callbacks != null && callbacks.size() > 0) {
            callbackHandler.post(() -> {
                int size = callbacks.size();
                for (int i = 0; i < size; i++) {
                    YuvFrameCallback callback = callbacks.get(i);
                    if (callback == null) {
                        continue;
                    }
                    callback.onFrame(bytes, bytes.length, timeUs);
                }
            });
        }
        camera.addCallbackBuffer(data);
    }

    @Override
    public void onError(int error, Camera camera) {
        Log.e(TAG, "onError: " + channel + " camera error(" + error + ").");
    }
}
