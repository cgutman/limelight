package com.limelight.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceConfiguration {
    static final String RES_FPS_PREF_STRING = "list_resolution_fps";
    private static final String DECODER_PREF_STRING = "list_decoders";
    static final String BITRATE_PREF_STRING = "seekbar_bitrate";
    private static final String STRETCH_PREF_STRING = "checkbox_stretch_video";
    private static final String SOPS_PREF_STRING = "checkbox_enable_sops";
    private static final String DISABLE_TOASTS_PREF_STRING = "checkbox_disable_warnings";
    private static final String HOST_AUDIO_PREF_STRING = "checkbox_host_audio";
    private static final String DEADZONE_PREF_STRING = "seekbar_deadzone";

    private static final int BITRATE_DEFAULT_640_480_30 = 1;
    private static final int BITRATE_DEFAULT_800_600_30 = 2;
    private static final int BITRATE_DEFAULT_1024_768_30 = 4;
    private static final int BITRATE_DEFAULT_720_30 = 5;
    private static final int BITRATE_DEFAULT_720_60 = 10;
    private static final int BITRATE_DEFAULT_1080_30 = 10;
    private static final int BITRATE_DEFAULT_1080_60 = 30;

    private static final String DEFAULT_RES_FPS = "720p60";
    private static final String DEFAULT_DECODER = "auto";
    private static final int DEFAULT_BITRATE = BITRATE_DEFAULT_720_60;
    private static final boolean DEFAULT_STRETCH = false;
    private static final boolean DEFAULT_SOPS = true;
    private static final boolean DEFAULT_DISABLE_TOASTS = false;
    private static final boolean DEFAULT_HOST_AUDIO = false;
    private static final int DEFAULT_DEADZONE = 15;

    public static final int FORCE_HARDWARE_DECODER = -1;
    public static final int AUTOSELECT_DECODER = 0;
    public static final int FORCE_SOFTWARE_DECODER = 1;

    public int width, height, fps;
    public int bitrate;
    public int decoder;
    public int deadzonePercentage;
    public boolean stretchVideo, enableSops, playHostAudio, disableWarnings;

    public static int getDefaultBitrate(String resFpsString) {
        if (resFpsString.equals("640_480_30")) {
            return BITRATE_DEFAULT_640_480_30;
        }
        else if (resFpsString.equals("800_600_30")) {
            return BITRATE_DEFAULT_800_600_30;
        }
        else if (resFpsString.equals("1024_768_30")) {
            return BITRATE_DEFAULT_1024_768_30;
        }
        else if (resFpsString.equals("720p30")) {
            return BITRATE_DEFAULT_720_30;
        }
        else if (resFpsString.equals("720p60")) {
            return BITRATE_DEFAULT_720_60;
        }
        else if (resFpsString.equals("1080p30")) {
            return BITRATE_DEFAULT_1080_30;
        }
        else if (resFpsString.equals("1080p60")) {
            return BITRATE_DEFAULT_1080_60;
        }
        else {
            // Should never get here
            return DEFAULT_BITRATE;
        }
    }

    public static int getDefaultBitrate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String str = prefs.getString(RES_FPS_PREF_STRING, DEFAULT_RES_FPS);
        if (str.equals("640_480_30")) {
            return BITRATE_DEFAULT_640_480_30;
        }
        else if (str.equals("800_600_30")) {
            return BITRATE_DEFAULT_800_600_30;
        }
        else if (str.equals("1024_768_30")) {
            return BITRATE_DEFAULT_1024_768_30;
        }
        else if (str.equals("720p30")) {
            return BITRATE_DEFAULT_720_30;
        }
        else if (str.equals("720p60")) {
            return BITRATE_DEFAULT_720_60;
        }
        else if (str.equals("1080p30")) {
            return BITRATE_DEFAULT_1080_30;
        }
        else if (str.equals("1080p60")) {
            return BITRATE_DEFAULT_1080_60;
        }
        else {
            // Should never get here
            return DEFAULT_BITRATE;
        }
    }

    private static int getDecoderValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String str = prefs.getString(DECODER_PREF_STRING, DEFAULT_DECODER);
        if (str.equals("auto")) {
            return AUTOSELECT_DECODER;
        }
        else if (str.equals("software")) {
            return FORCE_SOFTWARE_DECODER;
        }
        else if (str.equals("hardware")) {
            return FORCE_HARDWARE_DECODER;
        }
        else {
            // Should never get here
            return AUTOSELECT_DECODER;
        }
    }

    public static PreferenceConfiguration readPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceConfiguration config = new PreferenceConfiguration();

        config.bitrate = prefs.getInt(BITRATE_PREF_STRING, getDefaultBitrate(context));
        String str = prefs.getString(RES_FPS_PREF_STRING, DEFAULT_RES_FPS);
        if (str.equals("640_480_30")) {
            config.width = 640;
            config.height = 480;
            config.fps = 30;
        }
        else if (str.equals("800_600_30")) {
            config.width = 800;
            config.height = 600;
            config.fps = 30;
        }
        else if (str.equals("1024_768_30")) {
            config.width = 1024;
            config.height = 768;
            config.fps = 30;
        }
        else if (str.equals("720p30")) {
            config.width = 1280;
            config.height = 720;
            config.fps = 30;
        }
        else if (str.equals("720p60")) {
            config.width = 1280;
            config.height = 720;
            config.fps = 60;
        }
        else if (str.equals("1080p30")) {
            config.width = 1920;
            config.height = 1080;
            config.fps = 30;
        }
        else if (str.equals("1080p60")) {
            config.width = 1920;
            config.height = 1080;
            config.fps = 60;
        }
        else {
            // Should never get here
            config.width = 1280;
            config.height = 720;
            config.fps = 60;
        }

        config.decoder = getDecoderValue(context);

        config.deadzonePercentage = prefs.getInt(DEADZONE_PREF_STRING, DEFAULT_DEADZONE);

        // Checkbox preferences
        config.disableWarnings = prefs.getBoolean(DISABLE_TOASTS_PREF_STRING, DEFAULT_DISABLE_TOASTS);
        config.enableSops = prefs.getBoolean(SOPS_PREF_STRING, DEFAULT_SOPS);
        config.stretchVideo = prefs.getBoolean(STRETCH_PREF_STRING, DEFAULT_STRETCH);
        config.playHostAudio = prefs.getBoolean(HOST_AUDIO_PREF_STRING, DEFAULT_HOST_AUDIO);

        return config;
    }
}
