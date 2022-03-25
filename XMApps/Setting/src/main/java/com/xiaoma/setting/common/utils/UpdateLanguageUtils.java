/** 
 * @Title:  UpdateLanguageUtils.java 
 * @author:  Xiho
 * @data:  2016年3月1日 下午7:09:45 <创建时间>
 * 
 * @history：<以下是历史记录>
 *
 * @modifier: <修改人>
 * @modify date: 2016年3月1日 下午7:09:45 <修改时间>
 * @log: <修改内容>
 *
 * @modifier: <修改人>
 * @modify date: 2016年3月1日 下午7:09:45 <修改时间>
 * @log: <修改内容>
 */
package com.xiaoma.setting.common.utils;

import java.lang.reflect.Method;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 *
 * 

 */
@SuppressWarnings("unchecked")
public class UpdateLanguageUtils {

	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";

	public static void updateLanguage(Locale locale) {
		try {
			Object objIActMag, objActMagNative;

			Class clzIActMag = Class.forName("android.app.IActivityManager");

			Class clzActMagNative = Class
					.forName("android.app.ActivityManagerNative");

			Method mtdActMagNative$getDefault = clzActMagNative
					.getDeclaredMethod("getDefault");

			objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);

			Method mtdIActMag$getConfiguration = clzIActMag
					.getDeclaredMethod("getConfiguration");

			Configuration config = (Configuration) mtdIActMag$getConfiguration
					.invoke(objIActMag);

			config.locale = locale;

			Class clzConfig = Class
					.forName("android.content.res.Configuration");
			java.lang.reflect.Field userSetLocale = clzConfig
					.getField("userSetLocale");
			userSetLocale.set(config, true);

			// 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
			// 会重新调用 onCreate();
			Class[] clzParams = { Configuration.class };

			Method mtdIActMag$updateConfiguration = clzIActMag
					.getDeclaredMethod("updateConfiguration", clzParams);

			mtdIActMag$updateConfiguration.invoke(objIActMag, config);

//			BackupManager.dataChanged("com.android.providers.settings");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**

	 * 更改应用语言

	 *

	 * @param context

	 * @param locale 语言地区

	 * @param persistence 是否持久化

	 */

	public static void changeAppLanguage(Context context, Locale locale,

										 boolean persistence) {

		Resources resources = context.getResources();

		DisplayMetrics metrics = resources.getDisplayMetrics();

		Configuration configuration = resources.getConfiguration();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

			configuration.setLocale(locale);

		} else {

			configuration.locale = locale;

		}

		resources.updateConfiguration(configuration, metrics);

		if (persistence) {

			saveLanguageSetting(context, locale);

		}

	}



	private static void saveLanguageSetting(Context context, Locale locale) {

		String name = context.getPackageName() + "_" + LANGUAGE;

		SharedPreferences preferences =

				context.getSharedPreferences(name, Context.MODE_PRIVATE);

		preferences.edit().putString(LANGUAGE, locale.getLanguage()).apply();

		preferences.edit().putString(COUNTRY, locale.getCountry()).apply();

	}

}
