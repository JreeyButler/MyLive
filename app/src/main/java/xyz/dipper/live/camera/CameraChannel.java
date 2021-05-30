package xyz.dipper.live.camera;

import java.util.HashMap;

/**
 * @author Dipper
 * @date 2021/1/8 18:01
 */
public enum CameraChannel {
    CH1(0, 1),
    CH2(1, 2),
    UNKNOWN(-1, -1);

    private final int id;
    private final int channel;
    private static final int MAX_CHANNEL = 2;
    private static HashMap<Integer, CameraChannel> channelMap;
    private static HashMap<Integer, CameraChannel> idMap;

    static {
        initIdMap();
        initChannelMap();
    }

    private static void initChannelMap() {
        channelMap = new HashMap<>(MAX_CHANNEL);
        for (CameraChannel channel : CameraChannel.values()) {
            channelMap.put(channel.channel, channel);
        }
    }

    private static void initIdMap() {
        idMap = new HashMap<>(MAX_CHANNEL);
        for (CameraChannel channel : CameraChannel.values()) {
            idMap.put(channel.id, channel);
        }
    }

    CameraChannel(int id, int channel) {
        this.id = id;
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public int getChannel() {
        return channel;
    }

    public static CameraChannel getCameraChannelById(int id) {
        return channelMap.get(id);
    }

    public static CameraChannel getCameraChannelByChannel(int channel) {
        return idMap.get(channel);
    }
}
