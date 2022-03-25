package com.xiaoma.assistant.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.view.ContactView;
import com.xiaoma.assistant.view.MultiPageView;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.tts.OnTtsListener;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：Assistant 主要接口
 */
public interface IAssistantManager {

    void init(Context context);

    void show(boolean isOneshot);

    void speakContent(String text);

    void speakContent(String text, OnTtsListener listener);

    void speakThenListening(String word);

    void speakThenClose(String word);

    void startListeningForChoose(String stkCmd);

    void speakUnderstand();

    void startListening(boolean first);

    void stopListening();

    void onResume();

    void onHomePressed();

    void showProgressDialog(String loadingText);

    void dismissProgressDialog();

    void addFeedBackConversation(String content);

    void addItemToConversationList(ConversationItem item);

    void addItemAndMoveToBottom(ConversationItem item);

    boolean showMultiPageView(BaseMultiPageAdapter adapter);

    void showDetailView();

    void hideMultiPageView();

    boolean showContactView(BaseMultiPageAdapter adapter);

    void displayMusicRecognitionView(boolean show);

    void hideContactView();

    MultiPageView getMultiPageView();

    ContactView getContactView();

    TextView getSearchResultOperate();

    long getDialogSession();

    boolean isShowing();

    boolean inMultipleForChooseMode();

    boolean inContactChooseMode();

    void closeAssistant();

    void closeAssistant(DialogInterface.OnDismissListener listener);

    void showLoadingView(boolean show);

    void showMultiPageView(boolean show);

    void stopSpeak();

    void closeAfterSpeak(String content);


    void updateShowText(String statusText);

}
