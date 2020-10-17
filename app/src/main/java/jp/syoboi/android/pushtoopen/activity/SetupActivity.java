package jp.syoboi.android.pushtoopen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;

import jp.syoboi.android.pushtoopen.Prefs;
import jp.syoboi.android.pushtoopen.R;
import jp.syoboi.android.pushtoopen.activity.base.BaseSetupActivity;
import jp.syoboi.android.pushtoopen.databinding.ActivitySetupBinding;

import static jp.syoboi.android.pushtoopen.Prefs.SESAME_API_KEY;

/**
 * セットアップ画面
 */
public class SetupActivity extends BaseSetupActivity {

    private final int REQUEST_KEY = 1;

    ActivitySetupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySetupBinding.inflate(getLayoutInflater(),
                (ViewGroup) findViewById(android.R.id.content));

        // 説明テキスト
        mBinding.aboutApiKey.setText(Html.fromHtml(getString(R.string.aboutApiKey),
                Html.FROM_HTML_MODE_LEGACY));
        mBinding.aboutApiKey.setMovementMethod(LinkMovementMethod.getInstance());

        // APIキーのEdit
        mBinding.apiKeyEdit.setText(Prefs.get(SESAME_API_KEY));
        mBinding.apiKeyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateView();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // 貼り付けボタン
        mBinding.paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.apiKeyEdit.getEditableText().clear();
                mBinding.apiKeyEdit.onTextContextMenuItem(android.R.id.paste);
            }
        });


        mBinding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startKeyActivity();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_KEY) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    void updateView() {
        mBinding.next.setEnabled(mBinding.apiKeyEdit.getText().length() > 0);
    }

    void startKeyActivity() {
        String apiKey = mBinding.apiKeyEdit.getText().toString();
        if (!Prefs.get(SESAME_API_KEY).equals(apiKey)) {
            Prefs.set(SESAME_API_KEY, apiKey);
        }

        Intent i = new Intent(this,KeyActivity.class);
        startActivityForResult(i, REQUEST_KEY);
    }
}
