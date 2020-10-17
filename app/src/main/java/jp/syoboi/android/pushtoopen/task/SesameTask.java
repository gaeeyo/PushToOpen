package jp.syoboi.android.pushtoopen.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import jp.syoboi.android.pushtoopen.Prefs;
import jp.syoboi.android.pushtoopen.client.sesame.Sesame;

public abstract class SesameTask<T3> extends AsyncTask<Void, Void, Object> {


    @NonNull
    final Callback<T3> mCallback;

    public SesameTask(@NonNull Callback<T3> callback) {
        mCallback = callback;
    }

    @Override
    protected Object doInBackground(Void... params) {
        try {
            String apiKey = Prefs.get(Prefs.SESAME_API_KEY);
            if (TextUtils.isEmpty(apiKey)) {
                return new SesameTaskException("APIキーが設定されていません");
            }
            Sesame sesame = new Sesame(apiKey);
            return exec(sesame);
        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }

    protected abstract T3 exec(@NonNull Sesame sesame) throws Exception;

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (!isCancelled()) {
            if (o instanceof Throwable) {
                mCallback.onError((Throwable) o);
            } else {
                mCallback.onSuccess((T3) o);
            }
            mCallback.onFinish();
        }
    }

    public abstract static class Callback<T> {
        public abstract void onSuccess(T result);

        public abstract void onError(Throwable e);

        public abstract void onFinish();
    }
}
