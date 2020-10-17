package jp.syoboi.android.pushtoopen.client.sesame;


import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.syoboi.android.pushtoopen.BuildConfig;

public class Sesame {

    private static final String TAG = Sesame.class.getSimpleName();

    private static final String API_BASE_URL = "https://api.candyhouse.co/public/";

    private static final String API_DEVICE_LIST_URL = API_BASE_URL + "sesames";
    private static final String API_DEVICE_INFO_URL = API_BASE_URL + "sesame";
    private static final String API_ACTION_RESULT_URL = API_BASE_URL + "action-result";
    @NonNull
    private final String mApiKey;

    public Sesame(@NonNull String apiKey) {
        mApiKey = apiKey;
    }

    private String getRequest(String uri) throws IOException, SesameApiException {
        HttpURLConnection con = openUrlConnection(uri);
        return getContent(con);
    }

    private String postRequest(String uri, JSONObject data) throws IOException, SesameApiException {
        HttpURLConnection con = openUrlConnection(uri);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.connect();
        OutputStream os = con.getOutputStream();
        try {
            os.write(data.toString().getBytes());
        } finally {
            os.close();
        }
        return getContent(con);
    }

    private HttpURLConnection openUrlConnection(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("Authorization", mApiKey);
        return con;
    }

    private String getContent(HttpURLConnection con) throws IOException, SesameApiException {
        InputStream is = con.getInputStream();
        try {
            ByteArrayOutputStream o   = new ByteArrayOutputStream();
            byte[]                buf = new byte[8192];
            int                   size;

            while ((size = is.read(buf)) != -1) {
                o.write(buf, 0, size);
            }
            String text = o.toString();
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "response body: " + text);
            }
            checkErrorResponse(text);
            return text;
        } finally {
            is.close();
            con.disconnect();
        }
    }

    private void checkErrorResponse(String text) throws SesameApiException {
        try {
            JSONObject jo = new JSONObject(text);
            if (jo.has("error")) {
                String error = jo.getString("error");
                throw new SesameApiException("SESAME のサーバからエラーが返されました: " + error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getRequestAsJsonArray(String uri) throws IOException, JSONException, SesameApiException {
        return new JSONArray(getRequest(uri));
    }

    public JSONObject getRequestAsJsonObject(String uri) throws IOException, JSONException, SesameApiException {
        return new JSONObject(getRequest(uri));
    }

    public JSONObject postRequestAsJsonObject(String url, JSONObject data) throws IOException, JSONException, SesameApiException {
        return new JSONObject(postRequest(url, data));
    }

    public List<Device> getDevices() throws IOException, JSONException, SesameApiException {
        ArrayList<Device> devices = new ArrayList<>();
        JSONArray         ar      = getRequestAsJsonArray(API_DEVICE_LIST_URL);
        for (int j = 0; j < ar.length(); j++) {
            JSONObject obj = ar.getJSONObject(j);
            devices.add(new Device(obj));
        }
        return devices;
    }

    public DeviceInfo getDeviceInfo(@NonNull String device_id) throws IOException, JSONException, SesameApiException {
        return new DeviceInfo(getRequestAsJsonObject(API_DEVICE_INFO_URL + "/" + device_id));
    }

    public String setLock(@NonNull String device_id, boolean lock) throws JSONException, IOException, SesameApiException {
        JSONObject data = new JSONObject();
        data.put("command", lock ? "lock" : "unlock");

        JSONObject r = postRequestAsJsonObject(API_DEVICE_INFO_URL + "/" + device_id, data);
        return r.getString("task_id");
    }

    public ActionResult getActionResult(@NonNull String task_id) throws IOException, JSONException, SesameApiException {
        JSONObject r = getRequestAsJsonObject(API_ACTION_RESULT_URL + "?task_id=" + task_id);
        return new ActionResult(r);
    }

}
