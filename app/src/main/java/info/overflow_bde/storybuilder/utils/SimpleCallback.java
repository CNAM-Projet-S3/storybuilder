package info.overflow_bde.storybuilder.utils;

import org.json.JSONException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface SimpleCallback {

    void callback(String response) throws JSONException;

}
