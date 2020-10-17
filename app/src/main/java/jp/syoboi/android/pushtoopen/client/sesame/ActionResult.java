package jp.syoboi.android.pushtoopen.client.sesame;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionResult {

    public static final String STATUS_TERMINATED = "terminated";
    public static final String STATUS_PROCESSING = "processing";

    public String  status;
    public boolean successful;
    public String  error;

    public ActionResult(JSONObject o) throws JSONException {
        status = o.getString("status");
        successful = o.optBoolean("successful");
        error = o.optString("error");
    }
}
