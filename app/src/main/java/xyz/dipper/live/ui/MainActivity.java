package xyz.dipper.live.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import xyz.dipper.live.R;
import xyz.dipper.live.camera.CameraChannel;
import xyz.dipper.live.camera.CameraHelper;
import xyz.dipper.live.camera.YuvFrameCallback;
import xyz.dipper.live.view.MySurfaceView;

/**
 * @author Dipper
 * @date 2021/1/8 16:54
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private MySurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        CameraHelper.getInstance().openCamera(CameraChannel.CH2);
        CameraHelper.getInstance().addYuvFrameCallback(CameraChannel.CH2, callback);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.camera_preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermission(PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 1);
        } else {
            surfaceView.setChannel(CameraChannel.CH2);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private boolean checkPermission(String[] permissions) {
        for (String permission : permissions) {
            int result = checkSelfPermission(permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                showNoPermissionDialog();
            }
        }
    }

    private void showNoPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.no_permission_title)
                .setMessage(R.string.no_permission_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> finish())
                .show();
    }

    private final YuvFrameCallback callback = new YuvFrameCallback() {
        @Override
        public void onFrame(byte[] bytes, int length, long timeUs) {
            Log.d(TAG, "onFrame: " + bytes.length + ", timeUs = " + timeUs);

        }
    };
}
