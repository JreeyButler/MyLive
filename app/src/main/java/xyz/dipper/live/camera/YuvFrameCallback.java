package xyz.dipper.live.camera;

/**
 * @author Dipper
 * @date 2021/5/29 17:22
 */
public interface YuvFrameCallback {
    /**
     * Camera Yuv frame callback
     *
     * @param bytes  yuv data
     * @param length yuv data length
     * @param timeUs yuv timestamp
     */
    void onFrame(byte[] bytes, int length, long timeUs);
}
