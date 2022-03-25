package com.xiaoma.assistant.utils;


import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import com.xiaoma.assistant.R;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.image.transformation.RoundedCornersTransformation;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.utils.log.KLog;
import java.util.Locale;

/**
 * Created by qiuboxiang on 2019/3/9 17:13
 * Desc:
 */
public class CommonUtils {

    private static final int RADIUS = 10;
    private static final int BIG_RADIUS = 20;

    public static String getFormattedNumber(double number) {
//        return number % 1 == 0 ? String.valueOf((int) number) : String.format(Locale.CHINA, "%.1f", number);
        return String.format(Locale.CHINA, "%.1f", number);
    }

    public static String getFormattedDistance(Context context, double distanceNum) {
        return String.format(context.getString(R.string.thousand_address_distance), getFormattedNumber(distanceNum));
    }

    public static void setItemImage(Context context, String url, ImageView imageView) {
        loadImage(context, url, imageView, RADIUS, RoundedCornersTransformation.CornerType.TOP);
    }

    public static void setFullItemImage(Context context, String url, ImageView imageView) {
        loadImage(context, url, imageView, RADIUS, RoundedCornersTransformation.CornerType.ALL);
    }

    public static void setBigItemImage(Context context, String url, ImageView imageView) {
        ImageLoader.with(context)
                .load(url)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.image_place_holder)
                .transform(new RoundedCornersTransformation(BIG_RADIUS, RoundedCornersTransformation.CornerType.ALL), //圆角
                        new SquareCornerLightTransForm(context, 0, true, context.getDrawable(R.drawable.high_light), false)) //高光
                .into(imageView);
    }

    private static void loadImage(Context context, String url, ImageView imageView, int radius, RoundedCornersTransformation.CornerType cornerType) {
        ImageLoader.with(context)
                .load(url)
                .placeholder(R.drawable.image_place_holder)
                .error(R.drawable.image_place_holder)
                .transform(new CenterCrop(),
                        new RoundedCornersTransformation(radius, cornerType), //圆角
                        new SquareCornerLightTransForm(context, 0, true, context.getDrawable(R.drawable.high_light), false)) //高光
                .into(imageView);
    }

    public static SpannableString getPriceSpannableString(Context context, double price, String prefix, String suffix) {
        String text = prefix + getFormattedNumber(price) + suffix;
        SpannableString spannableString = new SpannableString(text);
        int boldTextLastIndex = text.length() - suffix.length();
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.white)), 0, boldTextLastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(context.getColor(R.color.gray)), boldTextLastIndex, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(28), 0, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(24), text.length() - 1, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static ParserLocationParam getLocation() {
        LatLng currentPosition = LocationManager.getInstance().getCurrentPosition();
        if (currentPosition != null) {
            String location = currentPosition.longitude + "," + currentPosition.latitude;
            String city = LocationManager.getInstance().getCurrentCity();
            KLog.d("SemanticByNetworkProcessor : getLocation lat = " +
                    currentPosition.latitude + ", lng = " +
                    currentPosition.longitude + ", city = " + city);
            return new ParserLocationParam(location, city);
        } else {
            KLog.e("SemanticByNetworkProcessor : getLocation currentPosition = " + null);
            LocationInfo info = LocationInfo.getDefault();
            String location = info.getLongitude() + "," + info.getLatitude();
            String city = info.getCity();
            return new ParserLocationParam(location, city);
        }
    }

    public static String getFormattedTime(Context context, int minutes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (minutes >= 60 * 24) {
            int days = minutes / (60 * 24);
            int hours = minutes % (60 * 24) / 60;
            minutes = minutes % (60 * 24) % 60;
            stringBuilder.append(days);
            stringBuilder.append(context.getString(R.string.day));
            if (hours != 0) {
                stringBuilder.append(hours);
                stringBuilder.append(context.getString(R.string.hour));
            }
            if (minutes != 0) {
                stringBuilder.append(minutes);
                stringBuilder.append(context.getString(R.string.minutes));
            }
        } else if (minutes >= 60) {
            int hours = minutes / 60;
            minutes = minutes % 60;
            stringBuilder.append(hours);
            stringBuilder.append(context.getString(R.string.hour));
            if (minutes != 0) {
                stringBuilder.append(minutes);
                stringBuilder.append(context.getString(R.string.minutes));
            }
        } else {
            stringBuilder.append(minutes);
            stringBuilder.append(context.getString(R.string.minutes));
        }
        return stringBuilder.toString();
    }
}
