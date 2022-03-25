package com.xiaoma.voiceprint;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.voiceai.vprcjavasdk.Response;
import com.voiceai.vprcjavasdk.VPRCJavaSDK;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.voiceprint.model.ICallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * @author KY
 * @date 11/22/2018
 */
public class VoicePrintSDK implements IVoicePrint {

    private static final String TAG = VoicePrintSDK.class.getSimpleName();
    private static final String TAG_OAUTH = TAG + "oauth";
    private static final String TAG_GET_GROUP = TAG + "get_group";
    private static final String TAG_ADD_GROUP = TAG + "add_group";
    private static final String TAG_REMOVE_GROUP = TAG + "remove_group";
    private static final String TAG_ADD_USER = TAG + "add_user";
    private static final String TAG_REMOVE_USER = TAG + "remove_user";
    private static final String TAG_GET_TEXTS = TAG + "get_texts";
    private static final String TAG_UPLOAD_VOICE = TAG + "upload_voice";
    private static final String TAG_VERIFY_VOICE = TAG + "verify_voice";
    private static final String TAG_REGISTER_VOICE = TAG + "register_voice";


    private VPRCJavaSDK vprcsdk;
    //由于SDK中的token也是储存在内存中，所以没必要把token储存在本地,只用记录Expires用于判断token过期
    private Long tokenExpires;
    private String groupId;
    private TPUtils.NamedTP namedTP;

    @Override
    public void init(Context context) {
        if (namedTP == null) {
            namedTP = new TPUtils.NamedTP(context, VoicePrintConstant.TP.TP_NAME);
        }
        vprcsdk = VPRCJavaSDK.getInstance().init();
        initTokenAndGroup(null);
    }

    private void initTokenAndGroup(final ICallback.InitCallback callback) {
        oauth(new ICallback.InitCallback() {
            @Override
            public void onGroupGetSuccess(String groupId) {
                VoicePrintSDK.this.groupId = groupId;
                namedTP.put(VoicePrintConstant.TP.GROUP_ID, groupId);
                if (callback != null) {
                    callback.onGroupGetSuccess(groupId);
                }
            }

            @Override
            public void onOauthSuccess() {
                groupId = namedTP.get(VoicePrintConstant.TP.GROUP_ID, "");
                if (TextUtils.isEmpty(groupId)) {
                    getGroup(VoicePrintConstant.VoicePrintConfig.GROUP_NAME, this);
                }
                if (callback != null) {
                    callback.onOauthSuccess();
                }
            }

            @Override
            public void onGroupAddSuccess(String groupId) {
                VoicePrintSDK.this.groupId = groupId;
                namedTP.put(VoicePrintConstant.TP.GROUP_ID, groupId);
                if (callback != null) {
                    callback.onGroupAddSuccess(groupId);
                }
            }

            @Override
            public void onFailure(String tag, String message) {
                if (TAG_GET_GROUP.equals(tag)) {
                    addGroup(VoicePrintConstant.VoicePrintConfig.GROUP_NAME,
                            VoicePrintConstant.VoicePrintConfig.GROUP_DESC, this);
                    if (callback != null) {
                        callback.onFailure(tag, message);
                    }
                } else {
                    KLog.e(tag, message);
                    if (callback != null) {
                        callback.onFailure(tag, message);
                    }
                }
            }
        });
    }

    @Override
    public void oauth(final ICallback.OauthCallback callback) {
        vprcsdk.VPRCSdkLogin(VoicePrintConstant.VoicePrintConfig.VOICE_PRINT_HOST,
                VoicePrintConstant.VoicePrintConfig.VOICE_PRINT_PORT,
                VoicePrintConstant.VoicePrintConfig.VOICE_PRINT_APP_ID,
                VoicePrintConstant.VoicePrintConfig.VOICE_PRINT_APP_SECRET, new Response() {
                    @Override
                    public void response(String response, Throwable throwable) {
                        if (throwable != null) {
                            callback.onFailure(TAG_OAUTH, "VoicePrint SDK授权失败 error：" + throwable.getMessage());
                        } else {
                            try {
                                long expires = new JSONObject(response).getLong("expires");
                                if (expires != 0) {
                                    tokenExpires = System.currentTimeMillis() + expires;
                                    callback.onOauthSuccess();
                                } else {
                                    callback.onFailure(TAG_OAUTH, "VoicePrint SDK授权返回异常");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                callback.onFailure(TAG_OAUTH, "VoicePrint SDK授权时json解析异常");
                            }

                        }
                    }
                });
    }

    @Override
    public void addGroup(String groupName, @Nullable String describe, final ICallback.AddGroupCallback callback) {
        vprcsdk.VPRCSdkGroupAdd(groupName, describe, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_ADD_GROUP, "VoicePrint 添加组失败 error：" + throwable.getMessage());
                } else {
                    try {
                        String groupId = new JSONObject(s).getString("groupid");
                        callback.onGroupAddSuccess(groupId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(TAG_ADD_GROUP, "VoicePrint 添加组时json解析错误");
                    }

                }
            }
        });
    }

    @Override
    public void getGroup(String groupName, final ICallback.GetGroupCallback callback) {
        vprcsdk.VPRCSdkGroupSearch(groupName, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_GET_GROUP, "VoicePrint 获取组失败 error：" + throwable.getMessage());
                } else {
                    callback.onGroupGetSuccess(s);
                }
            }
        });
    }

    @Override
    public void removeGroup(List<String> groupIds, final ICallback.CommonCallback callback) {
        vprcsdk.VPRCSdkGroupRemove(groupIds, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG, "VoicePrint 移除组失败 error：" + throwable.getMessage());
                } else {
                    callback.onSuccess(s);
                }
            }
        });
    }

    @Override
    public void addUser(final String userName, @Nullable final String describe, final ICallback.AddUserCallback callback) {
        checkLoginAndRun(new Runnable() {
            @Override
            public void run() {
                realAddUser(userName, describe, callback);
            }
        }, callback);
    }

    private void realAddUser(String userName, @Nullable String describe, final ICallback.AddUserCallback callback) {
        vprcsdk.VPRCSdkClientAdd(userName, describe, groupId, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_ADD_USER, "VoicePrint 添加用户失败 error：" + throwable.getMessage());
                } else {
                    try {
                        String clientId = new JSONObject(s).getString("clientid");
                        if (!TextUtils.isEmpty(clientId)) {
                            callback.onUserAddSuccess(clientId);
                        } else {
                            callback.onFailure(TAG_ADD_USER, "VoicePrint 添加用户时返回clientId为空");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(TAG_ADD_USER, "VoicePrint 添加用户时json解析错误");
                    }
                }
            }
        });
    }

    @Override
    public void removeUser(final List<String> clientIds, final String groupId, final ICallback.CommonCallback callback) {
        checkLoginAndRun(new Runnable() {
            @Override
            public void run() {
                realRemoveUser(clientIds, groupId, callback);
            }
        }, callback);
    }

    private void realRemoveUser(List<String> clientIds, String groupId, final ICallback.CommonCallback callback) {
        vprcsdk.VPRCSdkClientRemove(clientIds, groupId, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_REMOVE_USER, "VoicePrint 移除用户失败 error：" + throwable.getMessage());
                } else {
                    callback.onSuccess(s);
                }
            }
        });
    }

    @Override
    public void uploadVoicePrint(final File file, final ICallback.UploadCallback callback) {
        checkLoginAndRun(new Runnable() {
            @Override
            public void run() {
                realUploadVoicePrint(file, callback);
            }
        }, callback);
    }

    private void realUploadVoicePrint(File file, final ICallback.UploadCallback callback) {
        vprcsdk.VPRCSdkVoiceprintUpload(file, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_UPLOAD_VOICE, "VoicePrint 上传声纹文件失败 error：" + throwable.getMessage());
                } else {
                    try {
                        String fileId = new JSONObject(s).getJSONArray("list").getJSONObject(0).getString("fileid");
                        if (!TextUtils.isEmpty(fileId)) {
                            callback.onUploadSuccess(fileId);
                        } else {
                            callback.onFailure(TAG_UPLOAD_VOICE, "VoicePrint 上传声纹时返回文件Id为空");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(TAG_UPLOAD_VOICE, "VoicePrint 上传声纹时解析json错误");
                    }
                }
            }
        });
    }

    @Override
    public void getTrainingText(final String modeltype, final int modeltextcount, final ICallback.GetTextCallback callback) {
        checkLoginAndRun(new Runnable() {
            @Override
            public void run() {
                realGetTrainingText(modeltype, modeltextcount, callback);
            }
        }, callback);
    }

    private void realGetTrainingText(String modeltype, int modeltextcount, final ICallback.GetTextCallback callback) {
        vprcsdk.VPRCSdkTrainingTextGet(modeltype, modeltextcount, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_GET_TEXTS, "VoicePrint 获取声纹验证文本失败 error：" + throwable.getMessage());
                } else {
                    try {
                        JSONArray texts = new JSONObject(s).getJSONArray("text");
                        List<String> strings = GsonHelper.fromJsonToList(texts.toString(), String[].class);
                        if (!CollectionUtil.isListEmpty(strings)) {
                            callback.onGetTextSuccess(strings);
                        } else {
                            callback.onFailure(TAG_GET_TEXTS, "VoicePrint 获取声纹验证文本为空");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(TAG_GET_TEXTS, "VoicePrint 获取声纹验证文本时解析json错误");
                    }
                }
            }
        });
    }

    @Override
    public void registerVoicePrint(final String clientId, final int src_sample_rate, final String modeltype, final List<String> fileidlist, final ICallback.RegisterVoicePrintCallback callback) {
        checkLoginAndRun(new Runnable() {
            @Override
            public void run() {
                realRegisterVoicePrint(clientId, src_sample_rate, modeltype, fileidlist, callback);
            }
        }, callback);
    }

    private void realRegisterVoicePrint(final String clientId, int src_sample_rate, String modeltype, List<String> fileidlist, final ICallback.RegisterVoicePrintCallback callback) {
        vprcsdk.VPRCSdkVoiceprintRegister(clientId, groupId, src_sample_rate, modeltype, fileidlist, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_REGISTER_VOICE, "VoicePrint 声纹注册失败 error：" + throwable.getMessage());
                } else {
                    try {
                        boolean result = new JSONObject(s).getBoolean("result");
                        String msg = new JSONObject(s).getString("msg");
                        if (result) {
                            callback.onRegisterSuccess(clientId);
                        } else {
                            callback.onFailure(TAG_REGISTER_VOICE, msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(TAG_REGISTER_VOICE, "VoicePrint 声纹注册时解析json错误");
                    }
                }
            }
        });
    }

    @Override
    public void verifyVoicePrint(final String clientId, final int src_sample_rate, final String modeltype, final List<String> fileidlist, final int limitCount, final ICallback.VoicePrintVerifyCallback callback) {
        checkLoginAndRun(new Runnable() {
            @Override
            public void run() {
                realVerifyVoicePrint(clientId, src_sample_rate, modeltype, fileidlist, limitCount, callback);
            }
        }, callback);
    }

    private void realVerifyVoicePrint(String clientId, int src_sample_rate, String modeltype, List<String> fileidlist, int limitCount, final ICallback.VoicePrintVerifyCallback callback) {
        vprcsdk.VPRCSdkVoiceprintVerify(clientId, groupId, src_sample_rate, modeltype, fileidlist, limitCount, new Response() {
            @Override
            public void response(String s, Throwable throwable) {
                if (throwable != null) {
                    callback.onFailure(TAG_VERIFY_VOICE, "VoicePrint 声纹验证失败 error：" + throwable.getMessage());
                } else {
                    try {
                        boolean result = new JSONObject(s).getBoolean("result");
                        if (result) {
                            callback.onVerifySuccess();
                        } else {
                            callback.onFailure(TAG_VERIFY_VOICE, "VoicePrint 声纹验证失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(TAG_VERIFY_VOICE, "VoicePrint 声纹验证时json解析错误");
                    }
                }
            }
        });
    }

    private boolean isLogin() {
        return tokenExpires > System.currentTimeMillis()
                && !TextUtils.isEmpty(groupId);
    }

    private void checkLoginAndRun(final Runnable runnable, final ICallback.FailureCallback failureCallback) {
        if (!isLogin()) {
            initTokenAndGroup(new ICallback.InitCallback() {
                @Override
                public void onGroupGetSuccess(String groupId) {
                    runnable.run();
                }

                @Override
                public void onGroupAddSuccess(String groupId) {
                    runnable.run();
                }

                @Override
                public void onOauthSuccess() {

                }

                @Override
                public void onFailure(String tag, String message) {
                    KLog.e(tag, message);
                    failureCallback.onFailure(tag, message);
                }
            });
        } else {
            runnable.run();
        }
    }
}
