package Utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by zhangguoyu on 2015/4/7.
 */
public class DeviceUtils {
    /**
     * 默认图片缓存地址
     */
    public final static String DEFAULTBASEPATH = "zgy" + File.separator + "nag" + File.separator;
    public final static String DEFAULTIMGBASEPATH = DEFAULTBASEPATH + "image" + File.separator + "caches" + File.separator;

    public static boolean checkSDCard(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String getSDcardDir(){
        if(checkSDCard()){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        return "";
    }

}
