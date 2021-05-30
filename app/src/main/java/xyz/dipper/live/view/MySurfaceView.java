package xyz.dipper.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import xyz.dipper.live.camera.CameraChannel;
import xyz.dipper.live.camera.CameraHelper;

/**
 * @author Dipper
 * @date 2021/1/8 16:59
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MySurfaceView.class.getSimpleName();
    private CameraChannel channel;

    public MySurfaceView(Context context) {
        super(context);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);
    }

    public void setChannel(CameraChannel channel) {
        this.channel = channel;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated: ");
        if (channel != null && CameraHelper.getInstance().isCameraOpened(channel)) {
            Log.d(TAG, "surfaceCreated: start preview");
            CameraHelper.getInstance().startPreview(channel, surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed: ");
        CameraHelper.getInstance().closeCamera(channel);
    }
}
