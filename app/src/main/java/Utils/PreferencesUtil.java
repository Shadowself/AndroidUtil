package Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * comm preferenceUtil
 */
public class PreferencesUtil {
	public static final String PERSONPREFERENCES = "GRADUATION-GARDEN";
	private Context mContext;
	private SharedPreferences mSharedPreferences;

	public PreferencesUtil(Context context) {
		mContext = context;
		mSharedPreferences = mContext.getSharedPreferences(PERSONPREFERENCES, Context.MODE_MULTI_PROCESS);
	}
	
	public void remove(String key){
		this.mSharedPreferences.edit().remove(key).commit();
	}
	
	// 保存配置信息
	public void saveString(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}
	
	// 保存配置信息
	public void saveInt(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}
	
	// 保存配置信息
	public void saveLong(String key, Long value) {
		mSharedPreferences.edit().putLong(key, value).commit();
	}
	
	// 保存配置信息
	public void saveBoolean(String key, boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	// 获得配置信息
	public String getString(String key, String... defValue) {
		if (defValue.length > 0)
			return mSharedPreferences.getString(key, defValue[0]);
		else
			return mSharedPreferences.getString(key, null);

	}
	
	// 获得配置信息
	public int getInt(String key, int... defValue) {
		if (defValue.length > 0)
			return mSharedPreferences.getInt(key, defValue[0]);
		else
			return mSharedPreferences.getInt(key, 0);

	}
	
	// 获得配置信息
	public Long getLong(String key, int... defValue) {
		if (defValue.length > 0)
			return mSharedPreferences.getLong(key, defValue[0]);
		else
			return mSharedPreferences.getLong(key, 0);
		
	}
	
	// 获得配置信息
	public boolean getBoolean(String key, boolean... defValue) {
		if (defValue.length > 0)
			return mSharedPreferences.getBoolean(key, defValue[0]);
		else
			return mSharedPreferences.getBoolean(key, false);

	}
	
	public SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}
	
}
