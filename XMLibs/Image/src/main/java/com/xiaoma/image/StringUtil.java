package com.xiaoma.image;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class StringUtil {
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
}
