package com.xiaoma.utils;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xiaoma.utils.log.KLog;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StringUtil {
    private static final int PHONE_NUMBER_LENGTH = 11;

    public static String format(String format, Object... args) {
        String str = null;
        try {
            str = String.format(format, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (str == null) {
            str = "";
        }
        return str;
    }

    public static boolean contains(String src, String sub) {
        return src != null && src.contains(sub);
    }

    public static boolean equals(String src, String tar) {
        return src != null && src.equals(tar);
    }

    public static String ToUtf8(String src) {
        if (src != null) {
            try {
                src = URLEncoder.encode(src, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            src = "";
        }
        return src;
    }

    public static boolean isNotEmpty(String src) {
        return !(src == null || src.isEmpty());
    }

    public static boolean isEmpty(String src) {
        return src == null || src.isEmpty();
    }

    public static String optString(String src) {
        return optString(src, "");
    }

    public static String optString(String src, String defaultStr) {
        if (TextUtils.isEmpty(src)) {
            return defaultStr;
        }
        return src;
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    public static String stringToMD5(String string) {
        if (string == null || string.isEmpty()) return null;
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String getDateByYMD() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String getEventLogForm(String time, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(time);
        builder.append(" (");
        builder.append(name);
        builder.append(") ");
        return builder.toString();
    }

    public static String getFileSuffixByUrl(String url) {
        if (TextUtils.isEmpty(url)) return "";
        String fileName = getFileNameByUrl(url);
        if (TextUtils.isEmpty(fileName)) return "";
        String[] fileNames = fileName.split("\\.");
        if (fileNames != null && fileNames.length > 0) {
            return "." + fileNames[fileNames.length - 1];
        } else {
            return "";
        }
    }

    public static String getFileNameByUrl(String url) {
        if (TextUtils.isEmpty(url)) return "";
        String[] strs = url.split("/");
        if (strs != null && strs.length > 0)
            return strs[strs.length - 1];
        else
            return "";
    }

    public static String getFileNameByUrlWithOutSuffix(String url) {
        if (TextUtils.isEmpty(url)) return "";
        String fileName = getFileNameByUrl(url);
        if (TextUtils.isEmpty(fileName)) return "";
        String[] fileNames = fileName.split("\\.");
        if (fileNames != null && fileNames.length > 0) {
            return fileNames[0];
        } else {
            return "";
        }
    }

    public static String getFileNameByUrl(String url, String suffix) {
        if (url == null || url.isEmpty()) {
            KLog.e("getFileNameByUrl error, the url is empty");
            return null;
        }
        String fileName = MD5Utils.getStringMD5(url);
        if (TextUtils.isEmpty(fileName)) {
            fileName = StringUtil.getFileNameByUrlWithOutSuffix(url);
        }
        fileName += suffix;
        return fileName;
    }

    public static String getRealFilePath(String localDir, String url, String suffix) {
        if (!localDir.endsWith("/")) {
            localDir = localDir + "/";
        }
        String filePath = localDir + getFileNameByUrl(url, suffix);
        KLog.d("getDownLoadFilePath : filePath = " + filePath);
        return filePath;
    }

    /**
     * 注意:如果此方法有改动,注意同步Common模块中的CommonUtils.getImagePathByType,
     * 因为{User#getPicPath()}方法中使用的这个方法,而{@link #getImagePathByType(String, String)}原本是定义在Commons模块中的,
     * 本模块不能依赖Commons,因此暂时只能拷贝过来使用,
     * 使用该方法的原因可以参考注释{User#getPicPath()}
     *
     * @param url
     * @param imageType
     * @return
     */
    public static String getImagePathByType(String url, String imageType) {
        String retUrl;
        if (url == null || url.isEmpty() || imageType == null || imageType.isEmpty() || !url.startsWith("http")) {
            retUrl = url;
        } else {
            if (url.contains(StringUtil.ImageType.MIDDLE_IMG)) {
                if (imageType.equals(StringUtil.ImageType.MIDDLE_IMG)) return url;
                url = url.replace(StringUtil.ImageType.MIDDLE_IMG, "");
            } else if (url.contains(StringUtil.ImageType.SMALL_IMG)) {
                if (imageType.equals(StringUtil.ImageType.SMALL_IMG)) return url;
                url = url.replace(StringUtil.ImageType.SMALL_IMG, "");
            }
            int last = url.lastIndexOf('/');
            if (last <= 0) return url;
            String fileFullName = url.substring(last + 1);
            if (fileFullName == null || fileFullName.isEmpty()) return url;
            String[] name = fileFullName.split("\\.");
            if (name.length != 2) return url;
            StringBuilder sb = new StringBuilder();
            sb.append(url, 0, last + 1);
            sb.append(name[0]);
            sb.append(imageType);
            sb.append(".");
            sb.append(name[1]);
            retUrl = sb.toString();
        }
        KLog.d(String.format("#getImagePathByType: oriUrl:%s, retUrl:%s", url, retUrl));
        return retUrl;
    }

    public static String getSignedNumber(int num) {
        if (num > 0) {
            return "+" + String.valueOf(num);
        }
        return String.valueOf(num);
    }

    public static boolean isMobileValid(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        if (phone.length() != PHONE_NUMBER_LENGTH) {
            return false;
        }
        if (!isNumber(phone)) {
            return false;
        }
        return true;
    }

    public interface ImageType {
        String SMALL_IMG = "_small";
        String MIDDLE_IMG = "_middle";
    }

    /**
     * 保留两位小数
     *
     * @param number
     * @return
     */
    public static String keep2Decimal(float number) {
        return keep2Decimal(String.valueOf(number));
    }

    /**
     * 保留两位小数
     *
     * @param number
     * @return
     */
    public static String keep2Decimal(double number) {
        return keep2Decimal(String.valueOf(number));
    }

    /**
     * 保留两位小数
     *
     * @param numberStr
     * @return
     */
    public static String keep2Decimal(String numberStr) {
        if (!isNumber(numberStr)) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#.##");
        double number = Double.valueOf(numberStr);
        return df.format(number);
    }

    /**
     * 在字符串头部加空格,用于段落开头缩进(适用text size:30px)
     *
     * @param str
     * @return String
     */
    public static String addHeadSpaceToString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return "\t\t\t" + str.trim();
    }

    public static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            char[] chars = str.toCharArray();
            int sz = chars.length;
            boolean hasExp = false;
            boolean hasDecPoint = false;
            boolean allowSigns = false;
            boolean foundDigit = false;
            int start = chars[0] == '-' ? 1 : 0;
            int i;
            if (sz > start + 1 && chars[start] == '0') {
                if (chars[start + 1] == 'x' || chars[start + 1] == 'X') {
                    i = start + 2;
                    if (i == sz) {
                        return false;
                    }

                    while (i < chars.length) {
                        if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                            return false;
                        }

                        ++i;
                    }

                    return true;
                }

                if (Character.isDigit(chars[start + 1])) {
                    for (i = start + 1; i < chars.length; ++i) {
                        if (chars[i] < '0' || chars[i] > '7') {
                            return false;
                        }
                    }

                    return true;
                }
            }

            --sz;

            for (i = start; i < sz || i < sz + 1 && allowSigns && !foundDigit; ++i) {
                if (chars[i] >= '0' && chars[i] <= '9') {
                    foundDigit = true;
                    allowSigns = false;
                } else if (chars[i] == '.') {
                    if (hasDecPoint || hasExp) {
                        return false;
                    }

                    hasDecPoint = true;
                } else if (chars[i] != 'e' && chars[i] != 'E') {
                    if (chars[i] != '+' && chars[i] != '-') {
                        return false;
                    }

                    if (!allowSigns) {
                        return false;
                    }

                    allowSigns = false;
                    foundDigit = false;
                } else {
                    if (hasExp) {
                        return false;
                    }

                    if (!foundDigit) {
                        return false;
                    }

                    hasExp = true;
                    allowSigns = true;
                }
            }

            if (i < chars.length) {
                if (chars[i] >= '0' && chars[i] <= '9') {
                    return true;
                } else if (chars[i] != 'e' && chars[i] != 'E') {
                    if (chars[i] == '.') {
                        return (!hasDecPoint && !hasExp) && foundDigit;
                    } else if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                        return foundDigit;
                    } else if (chars[i] != 'l' && chars[i] != 'L') {
                        return false;
                    } else {
                        return foundDigit && !hasExp && !hasDecPoint;
                    }
                } else {
                    return false;
                }
            } else {
                return !allowSigns && foundDigit;
            }
        }
    }

    public static String convertBigDecimal(@IntRange(from = 0) long bigDecimal) {
        if (bigDecimal <= 9999L) {
            return Long.toString(bigDecimal);
        } else if (bigDecimal <= 99999999L) {
            return bigDecimal / 10000 + "万";
        } else if (bigDecimal <= 999999999999L) {
            return bigDecimal / 100000000 + "亿";
        } else if (bigDecimal <= 9999999999999999L) {
            return bigDecimal / 1000000000000L + "兆";
        } else {
            // TODO:文案
            return "数不清";
        }
    }

    /**
     * 名字转拼音,取首字母
     *
     * @param name
     * @return
     */
    public static String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        //汉字转换成拼音

        String pinyin = CharacterParseUtil.getInstance().getSelling(name);
        if (TextUtils.isEmpty(pinyin) || TextUtils.isEmpty(pinyin.trim())) {
            return letter;
        }
        String firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            return letter;
        }
        return pinyin;
    }

    /**
     * 名字转拼音,取首字母
     *
     * @param name
     * @return
     */
    public static String getSortLetter2(String name) {
        String letter = "~";
        if (TextUtils.isEmpty(name)) {
            return letter;
        }
        //汉字转换成拼音

        String pinyin = CharacterParseUtil.getInstance().getSelling(name);
        if (TextUtils.isEmpty(pinyin) || TextUtils.isEmpty(pinyin.trim())) {
            return letter;
        }
        String firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")&&!firstLetter.matches("[0-9]")) { // 如果不在A-Z和0-9中则默认为“~”
            return letter;
        }
        return pinyin;
    }

    public static boolean isMobileNO(String mobileNums) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         * @param str
         * @return 待检测的字符串
         */
        // String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "^[1]([3-9])[0-9]{9}$";
        if (TextUtils.isEmpty(mobileNums)) {
            return false;
        } else {
            return mobileNums.matches(telRegex);
        }
    }

    /*
       获取结束时间
     */
    @NonNull
    public static String getOutTime(String time, String min) {
        String outHour = new String();
        String outMin = new String();
        String showTime = time;
        String duration = min;
        int outTimeHour = ConvertUtils.stringToInt(showTime.substring(0, 2));
        int outTimeMin = ConvertUtils.stringToInt(showTime.substring(2));
        int totalMin = outTimeMin + ConvertUtils.stringToInt(duration);
        outTimeHour = outTimeHour + totalMin / 60;
        outTimeMin = totalMin % 60;
        if (outTimeMin < 10) {
            outMin = "0" + outTimeMin;
        } else {
            outMin = "" + outTimeMin;
        }
        if (outTimeHour < 24) {
            if (outTimeHour < 10) {
                outHour = "0" + outTimeHour;
            } else {
                outHour = "" + outTimeHour;
            }
        } else {
            outTimeHour = outTimeHour % 24;
            outHour = "0" + outTimeHour;
        }
        return outHour + ":" + outMin;
    }


    public static String[] concatArray(String[] a, String[] b) {
        if (a == null || b == null) {
            return new String[0];
        }
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static String join(CharSequence delimiter, Object... elements) {
        if (delimiter == null || elements == null)
            return null;
        final StringBuilder sb = new StringBuilder();
        for (final Object element : elements) {
            sb.append(element).append(delimiter);
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - delimiter.length());
        }
        return sb.toString();
    }

    public static String join(CharSequence delimiter, List<Object> elements) {
        if (delimiter == null || CollectionUtil.isListEmpty(elements))
            return null;
        final StringBuilder sb = new StringBuilder();
        for (final Object element : elements) {
            sb.append(element).append(delimiter);
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - delimiter.length());
        }
        return sb.toString();
    }
}
