package jp.syoboi.android.pushtoopen;

import android.content.Context;

import androidx.annotation.NonNull;

public class ErrorMessage {
    public static String getErrorMessage(@NonNull Context context, @NonNull Throwable e) {
        return e.getMessage();
    }
}
