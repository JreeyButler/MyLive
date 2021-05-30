package xyz.dipper.live.camera;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author Dipper
 * @date 2021/1/8 17:59
 */
public class CameraHelper {
    private static final String TAG = CameraHelper.class.getSimpleName();
    private HashMap<CameraChannel, BaseCamera> cameraMap;
    private static CameraHelper helper;

    private CameraHelper() {
    }

    public static CameraHelper getInstance() {
        if (helper == null) {
            helper = new CameraHelper();
        }
        return helper;
    }

    public synchronized boolean openCamera(CameraChannel channel) {
        Log.d(TAG, "openCamera: " + channel);
        if (isCameraOpened(channel)) {
            return true;
        }
        BaseCamera camera = new BaseCamera();
        if (camera.open(channel)) {
            if (cameraMap == null) {
                cameraMap = new HashMap<>(Camera.getNumberOfCameras());
            }
            cameraMap.put(channel, camera);
            return true;
        }
        return false;
    }

    public synchronized void closeCamera(CameraChannel channel) {
        if (!isCameraOpened(channel)) {
            return;
        }
        BaseCamera camera = getOpenedCameraByChannel(channel);
        if (camera == null) {
            return;
        }
        camera.close();
    }

    public synchronized void startPreview(CameraChannel channel, SurfaceHolder holder) {
        if (isCameraOpened(channel)) {
            BaseCamera camera = getOpenedCameraByChannel(channel);
            if (camera == null) {
                return;
            }
            if (!camera.isCameraPreviewing()) {
                camera.startPreview(holder);
            }
        }
    }

    public synchronized void stopPreview(CameraChannel channel) {
        if (isCameraOpened(channel)) {
            BaseCamera camera = getOpenedCameraByChannel(channel);
            if (camera != null) {
                camera.stopPreview();
            }
        }
    }

    public boolean isCameraOpened(CameraChannel channel) {
        if (cameraMap == null) {
            return false;
        }
        BaseCamera camera = cameraMap.get(channel);
        return camera != null && camera.isCameraOpened();
    }

    public BaseCamera getOpenedCameraByChannel(CameraChannel channel) {
        return cameraMap == null ? null : cameraMap.get(channel);
    }

    public void addYuvFrameCallback(CameraChannel channel, @NotNull YuvFrameCallback callback) {
        BaseCamera camera = getOpenedCameraByChannel(channel);
        if (camera != null) {
            camera.addYuvFrameCallback(callback);
        }
    }

    public void removeYuvFrameCallback(CameraChannel channel, @NotNull YuvFrameCallback callback) {
        BaseCamera camera = getOpenedCameraByChannel(channel);
        if (camera != null) {
            camera.removeYuvFrameCallback(callback);
        }
    }
}
