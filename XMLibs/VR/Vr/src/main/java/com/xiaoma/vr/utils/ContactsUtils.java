package com.xiaoma.vr.utils;

import com.xiaoma.vr.model.Contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator
 * 2017/5/10 0010.
 */

public class ContactsUtils {

    /**
     * 得到手机通讯录联系人信息
     **/
    public static List<Contacts> getPhoneContacts() {
        List<Contacts> contactses = new ArrayList<>();
        /*ContentResolver resolver = CoreManager.getContext().getContentResolver();
        Cursor phoneCursor = null;
        // 获取手机联系人
        try {
            String[] PHONES_PROJECTION = new String[] {
                    CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NUMBER,
                    CommonDataKinds.Phone.CONTACT_ID, CommonDataKinds.Phone.TYPE };
            phoneCursor = resolver.query(CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION,
                    null, null, null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    //得到手机号码
                    String phoneNumber = phoneCursor.getString(1);
                    //当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    //得到联系人名称
                    String contactName = phoneCursor.getString(0);
                    //得到联系人ID
                    Long contactid = phoneCursor.getLong(2);
                    int type = phoneCursor.getInt(3);
                    Contacts contact = new Contacts(contactName.replace(" ",""),
                            phoneNumber.replace(" ", ""), contactid,
                            CoreManager.getContext().getString(CommonDataKinds.Phone.getTypeLabelResource(type)));
                    contactses.add(contact);
                }
                phoneCursor.close();
            }
        }catch (Exception e){
            if(phoneCursor != null && !phoneCursor.isClosed()){
                phoneCursor.close();
            }
        }*/
        return contactses;
    }

    public static void upLoadContactInfos(){
        /*Intent intent = new Intent();
        intent.setAction(Constants.Actions.UPLOAD_CONTACT_IAT);
        CoreManager.getContext().sendBroadcast(intent);*/
    }

    public static String getAllContactUserName(){
//        List<Contacts> allResult = LiteOrmDBManager.getInstance().queryData(new QueryBuilder<>(Contacts.class));
        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < allResult.size() ; i++) {
//            stringBuilder.append(allResult.get(i).getUserName());
//            stringBuilder.append("\n");
//        }
        return stringBuilder.toString();
    }

    public static List<Contacts> getMails(String content, String category){
        /*if (!TextUtils.isEmpty(content)) {
            List<Contacts> allResult = new ArrayList<>();
            QueryBuilder qbNumber = new QueryBuilder<>(Contacts.class)
                    .where("phoneNumber = ?", new String[]{content});
            allResult.addAll(LiteOrmDBManager.getInstance().queryData(qbNumber));
            QueryBuilder qb = new QueryBuilder<>(Contacts.class)
                    .where("userName LIKE ?", new String[]{"%"+content+"%"});
            allResult.addAll(LiteOrmDBManager.getInstance().queryData(qb));
            if(TextUtils.isEmpty(category)) return allResult;
            List<Contacts> filterResult = new ArrayList<>();
            for (Contacts contacts : allResult) {
                if(contacts.getCategory().equals(category)){
                    filterResult.add(contacts);
                }
            }
            if(filterResult.isEmpty()){
                filterResult.addAll(allResult);
            }
            return filterResult;
        }*/
        return null;
    }

    public static void intentToCallView(String phoneNumber){
        //跳转到拨号界面，同时传递电话号码
//        Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
//        dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        CoreManager.getContext().startActivity(dialIntent);
    }
}
