package org.mozilla.vrbrowser.ui.widgets.prompts;

import android.content.Context;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.mozilla.geckoview.GeckoSession;
import org.mozilla.vrbrowser.R;
import org.mozilla.vrbrowser.audio.AudioEngine;
import org.mozilla.vrbrowser.ui.views.settings.SettingsEditText;

public class AuthPromptWidget extends PromptWidget {

    private AudioEngine mAudio;
    private SettingsEditText mUsernameText;
    private SettingsEditText mPasswordText;
    private TextView mUsernameTextLabel;
    private TextView mPasswordTextLabel;
    private Button mOkButton;
    private Button mCancelButton;
    private GeckoSession.PromptDelegate.AuthCallback mCallback;

    public AuthPromptWidget(Context aContext) {
        super(aContext);
        initialize(aContext);
    }

    public AuthPromptWidget(Context aContext, AttributeSet aAttrs) {
        super(aContext, aAttrs);
        initialize(aContext);
    }

    public AuthPromptWidget(Context aContext, AttributeSet aAttrs, int aDefStyle) {
        super(aContext, aAttrs, aDefStyle);
        initialize(aContext);
    }

    @Override
    protected void initialize(Context aContext) {
        super.initialize(aContext);

        inflate(aContext, R.layout.prompt_auth, this);

        mAudio = AudioEngine.fromContext(aContext);

        mLayout = findViewById(R.id.layout);

        mTitle = findViewById(R.id.textTitle);
        mMessage = findViewById(R.id.textMessage);
        mMessage.setMovementMethod(new ScrollingMovementMethod());
        mUsernameText = findViewById(R.id.authUsername);
        mUsernameText.setShowSoftInputOnFocus(false);
        mUsernameTextLabel = findViewById(R.id.authUsernameLabel);
        mPasswordText = findViewById(R.id.authPassword);
        mPasswordText.setShowSoftInputOnFocus(false);
        mPasswordTextLabel = findViewById(R.id.authPasswordLabel);

        mOkButton = findViewById(R.id.positiveButton);
        mOkButton.setSoundEffectsEnabled(false);
        mOkButton.setOnClickListener(view -> {
            if (mAudio != null) {
                mAudio.playSound(AudioEngine.Sound.CLICK);
            }

            if (mCallback != null) {
                mCallback.confirm(mUsernameText.getText().toString(), mPasswordText.getText().toString());
            }

            hide(REMOVE_WIDGET);
        });

        mCancelButton = findViewById(R.id.negativeButton);
        mCancelButton.setSoundEffectsEnabled(false);
        mCancelButton.setOnClickListener(view -> {
            if (mAudio != null) {
                mAudio.playSound(AudioEngine.Sound.CLICK);
            }

            onDismiss();
        });

        Switch showPasswordSwitch = findViewById(R.id.showPasswordSwitch);
        showPasswordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPasswordText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                mPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }

    @Override
    protected void onDismiss() {
        hide(REMOVE_WIDGET);

        if (mCallback != null) {
            mCallback.dismiss();
        }
    }

    public void setAuthOptions(GeckoSession.PromptDelegate.AuthOptions aOptions, GeckoSession.PromptDelegate.AuthCallback aCallback) {
        if (aOptions.username != null) {
            mUsernameText.setText(aOptions.username);
        }
        if (aOptions.password != null) {
            mPasswordText.setText(aOptions.password);
        }
        if ((aOptions.flags & GeckoSession.PromptDelegate.AuthOptions.AUTH_FLAG_ONLY_PASSWORD) != 0) {
            mUsernameText.setVisibility(View.GONE);
            mUsernameTextLabel.setVisibility(View.GONE);
        }
        mCallback = aCallback;
    }

    @Override
    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            mTitle.setText(getContext().getString(R.string.authentication_required));

        } else {
            mTitle.setText(title);
        }
    }


}
