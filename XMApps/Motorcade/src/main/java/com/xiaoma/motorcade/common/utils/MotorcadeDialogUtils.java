package com.xiaoma.motorcade.common.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoma.motorcade.R;
import com.xiaoma.ui.constract.ToastLevel;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.InputDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.dialog.impl.XMSmallTextDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

/**
 * Created by ZYao.
 * Date ：2019/1/18 0018
 */
public class MotorcadeDialogUtils {
    public static final int DIALOG_TYPE_MOTORCADE = 0;
    public static final int DIALOG_TYPE_NICKNAME = 1;
    private static final int MAX_COMMAND_LENGTH = 6;
    private static final int MAX_NAME_LENGTH = 16;

    private static boolean showNetWorkError(Context context, FragmentManager fragmentManager) {
        if (!NetworkUtils.isConnected(context)) {
            XMToast.toastException(context, R.string.net_work);
            return true;
        }
        return false;
    }

    public static void showCreateDialog(final FragmentActivity context, final FragmentManager fragmentManager,
                                        final DialogClickListener listener) {
        final InputDialog dialog = new InputDialog(context);
        final EditText edit = dialog.getEditText();
        edit.setFilters(getFilter(MAX_NAME_LENGTH));
        setOnKeyListener(context, edit, listener, dialog);
        setHintSize(edit, context.getString(R.string.enter_motorcade));
        dialog.setTitle(context.getString(R.string.create_motorcade))
                .setPositiveButton(context.getString(R.string.sure), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        if (showNetWorkError(context, fragmentManager)) return;
                        String text = edit.getText().toString().replaceAll("\\s*", "");
                        if (TextUtils.isEmpty(text)) {
                            XMToast.toastException(context, context.getString(R.string.input_null_motorcade));
                            edit.setText("");
                            return;
                        }
                        listener.onSure(text);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        listener.onCancel();
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public static void showJoinDialog(final FragmentActivity context, final FragmentManager fragmentManager,
                                      final DialogClickListener listener) {
        final InputDialog dialog = new InputDialog(context);
        final EditText edit = dialog.getEditText();
        edit.setFilters(getFilter(MAX_COMMAND_LENGTH));
        edit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        setOnKeyListener(context, edit, listener, dialog);
        setHintSize(edit, context.getString(R.string.dialog_et_hint));
        dialog.setTitle(context.getString(R.string.join_motorcade))
                .setPositiveButton(context.getString(R.string.sure), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        if (showNetWorkError(context, fragmentManager)) return;
                        String text = edit.getText().toString().replaceAll("\\s*", "");
                        if (TextUtils.isEmpty(text)) {
                            XMToast.toastException(context, context.getString(R.string.input_null_command));
                            edit.setText("");
                            return;
                        }
                        listener.onSure(text);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        listener.onCancel();
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public static void showExitDialog(final FragmentActivity context, final FragmentManager fragmentManager,
                                      final DialogClickListener listener) {
        final ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setTitle(context.getString(R.string.setting_motorcade_exit_tips))
                .setContent(context.getString(R.string.exit_motorcade_sure))
                .setPositiveButton(context.getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (showNetWorkError(context, fragmentManager)) return;
                        listener.onSure("");
                        dialog.dismiss();
                    }
                })

                .show();
    }

    public static void showMotorcadeNameDialog(final FragmentActivity context,
                                               final FragmentManager fragmentManager,
                                               final DialogClickListener listener,
                                               CharSequence name, int type) {
        final InputDialog dialog = new InputDialog(context);
        final EditText edit = dialog.getEditText();
        edit.setFilters(getFilter(MAX_NAME_LENGTH));
        setOnKeyListener(context, edit, listener, dialog);
        setHintSize(edit, name.toString());
        String title;
        if (type == DIALOG_TYPE_MOTORCADE) {
            title = context.getString(R.string.motorcade_name);
        } else {
            title = context.getString(R.string.diaplay_nick);
        }
        dialog.setTitle(title)
                .setPositiveButton(context.getString(R.string.sure), new InputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String editext) {
                        if (showNetWorkError(context, fragmentManager)) {
                            return;
                        }
                        String text = edit.getText().toString().replaceAll("\\s*", "");
                        if (TextUtils.isEmpty(text)) {
                            XMToast.toastException(context, context.getString(R.string.input_null_content));
                            edit.setText("");
                            return;
                        }
                        listener.onSure(text);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new InputDialog.OnCancelClickListener() {
                    @Override
                    public void onCancelClick() {
                        listener.onCancel();
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    public static void showNoPermissionError(FragmentManager fragmentManager) {
        XMSmallTextDialog mSmallDialog = XMCompatDialog.createSmallDialog();
        mSmallDialog.updateMsgAndState(ToastLevel.EXCEPTION, R.string.setting_no_permission);
        mSmallDialog.showDialog(fragmentManager);
    }

    public static void showShareCommandDialog(final Context context, final FragmentManager fragmentManager,
                                              final DialogClickListener listener) {
        View view = View.inflate(context, R.layout.dialog_share_command, null);
        TextView sureBtn = view.findViewById(R.id.dialog_share_btn_sure);
        TextView cancelBtn = view.findViewById(R.id.dialog_share_btn_cancel);
        final XmDialog builder = new XmDialog.Builder(fragmentManager)
                .setView(view)
                .create();
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSure("");
                builder.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
                builder.dismiss();
            }
        });
        builder.show();
    }

    private static InputFilter[] getFilter(final int length) {
        return new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                source = source.toString().replaceAll(" ", "");
                int dindex = 0;
                int count = 0;
                while (count <= length && dindex < dest.length()) {
                    char c = dest.charAt(dindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > length) {
                    return dest.subSequence(0, dindex - 1);
                }

                int sindex = 0;
                while (count <= length && sindex < source.length()) {
                    char c = source.charAt(sindex++);
                    if (c < 128) {
                        count = count + 1;
                    } else {
                        count = count + 2;
                    }
                }
                if (count > length) {
                    sindex--;
                }
                return source.subSequence(0, sindex);
            }
        }};
    }

    public interface DialogClickListener {
        void onSure(String text);

        void onCancel();
    }

    private static void setOnKeyListener(final Context context, final EditText editText,
                                         final DialogClickListener listener, final InputDialog dialog) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == event.getKeyCode()) {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        XMToast.toastException(context, R.string.please_input_motorcade);
                        return true;
                    }
                    listener.onSure(editText.getText().toString());
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int focusType = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
                    dialog.setSoftInputMode(focusType);
                }
            }
        });
    }

    private static void setHintSize(EditText editText, String hint) {
        SpannableString ss = new SpannableString(hint);//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(30, false);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannedString(ss));
    }

}
