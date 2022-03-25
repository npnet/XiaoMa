package com.xiaoma.personal.order.constants;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.xiaoma.personal.R;
import com.xiaoma.ui.dialog.ConfirmDialog;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/26 0026 17:31
 *       desc：拨打电话 、取消预定
 * </pre>
 */
public final class OrderDialogFactory {

    private OrderDialogFactory() {
        throw new RuntimeException();
    }


    public static void createCallPhoneOrCancelPredestineDialog(
            FragmentActivity activity,
            String desc,
            String content,
            String operation,
            final DialogHandlerCallback callback) {

        final ConfirmDialog dialog = new ConfirmDialog(activity);
        dialog.setContent(desc+"\n"+content)
                .setPositiveButton(operation, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callback.handle();
                    }
                })
                .setNegativeButton(activity.getString(R.string.select_negative_cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

//        View view = View.inflate(activity, R.layout.dialog_cancel_order, null);
//        TextView descText = view.findViewById(R.id.cancel_order_content_first);
//        TextView contentText = view.findViewById(R.id.cancel_order_content_second);
//        TextView operationText = view.findViewById(R.id.cancel_predestine_order);
//        descText.setText(desc);
//        contentText.setText(content);
//        operationText.setText(operation);
//
//        final XmDialog xmDialog = new XmDialog.Builder(activity)
//                .setView(view)
//                .setWidth(640)
//                .setHeight(470)
//                .addOnClickListener(R.id.cancel_predestine_order, R.id.confirm_predestine_order)
//                .setOnViewClickListener(new OnViewClickListener() {
//                    @Override
//                    public void onViewClick(View view, XmDialog tDialog) {
//                        tDialog.dismiss();
//                        if (view.getId() == R.id.cancel_predestine_order) {
//                            callback.handle();
//                        }
//                    }
//                })
//                .create();
//        xmDialog.show();
    }


}
