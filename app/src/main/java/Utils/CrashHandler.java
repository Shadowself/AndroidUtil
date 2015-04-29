package Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告. 
 * 创建人：李满义
 * 创建时间：2014-5-12 下午4:24:59
 * 修改人：
 * 修改时间：
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public final String TAG = "CrashHandler";
	private Context mContext;
	/**
	 * 异常日志文件保存地址
	 */
	private static String crashSavePath = "crash" + File.separator;
	/**
	 * CrashHandler 实例
	 */
	private static CrashHandler INSTANCE = new CrashHandler();


	/**
	 * 系统默认的 UncaughtException 处理类
	 */
	private UncaughtExceptionHandler mDefaultHandler;

	/**
	 * 用来存储设备信息和异常信息
	 */
	private Map<String, String> infos = new HashMap<String, String>();

	/**
	 * 用于格式化日期,作为日志文件名的一部分
	 */
	private DateFormat sdf_crash = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
	
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 2;// sd卡中日志文件的最多保存天数
	private static final int MEMORY_LOG_FILE_MAX_SIZE = 3 * 1024 * 1024;           //内存中日志文件最大值，3M  

	/** 保证只有一个 CrashHandler 实例 */
	private CrashHandler() {
	}

	/** 获取 CrashHandler 实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 描述：初始化
	 * 创建人：李满义
	 * 创建时间：2014-5-12 下午4:25:15
	 * @param context
	 * 修改人：
	 * 修改时间：
	 */
	public void init(Context context) {
		mContext = context;
	
		// 获取系统默认的 UncaughtException 处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	
		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}

			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 描述：自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 * 创建人：李满义
	 * 创建时间：2014-5-12 下午4:25:35
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 * 修改人：
	 * 修改时间：
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		final String msg = ex.getLocalizedMessage();
		// 使用 Toast 来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, String.format("很抱歉，程序出现异常：(%s)", StringUtils.isNotBlank(msg) ? msg : "null"), Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();

		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfoToFile(ex);
		return true;
	}

	/**
	 * 描述：收集设备参数信息
	 * 创建人：李满义
	 * 创建时间：2014-5-12 下午4:25:57
	 * @param ctx
	 * 修改人：
	 * 修改时间：
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 描述：保存错误信息到文件中
	 * 创建人：李满义
	 * 创建时间：2014-5-12 下午4:26:19
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 * 修改人：
	 * 修改时间：
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		Date nowtime = new Date();
		sb.append("\n\n\n");
		sb.append(myLogSdf.format(nowtime));
		sb.append("\n");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = sdf_crash.format(new Date(timestamp));
			String fileName = "crash_" + time + "_error" + ".log";
			
			if (SDCardUtil.isSDCardEnable()) {
				String path = SDCardUtil.getSDCardPath()  +  crashSavePath;
				File file = new File(path, fileName);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}else{					
					if(file.length() >= MEMORY_LOG_FILE_MAX_SIZE){
						file.deleteOnExit();
					}
				}
				
				try {
					FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
					BufferedWriter bufWriter = new BufferedWriter(filerWriter);
					bufWriter.write(sb.toString());
					bufWriter.newLine();
					bufWriter.close();
					filerWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}

		return null;
	}
	
	/**
	 * 删除过期的日志文件
	 * */
	public void delOutTimeFile() {// 删除日志文件
		try {
			String path = SDCardUtil.getSDCardPath()  + crashSavePath;
			File logDir = new File(path);
			if(logDir.exists() && logDir.isDirectory() && logDir.listFiles().length > 0){
				File logFiles[] = logDir.listFiles();
				for(File logFile : logFiles){
					if (logFile.isDirectory()) {
						continue;
					}
					String logFileName = logFile.getName();
					String []seps = logFileName.split("_");
					if (seps.length > 1) {					
						if(compareDaysDiff(seps[1]) >= SDCARD_LOG_FILE_SAVE_DAYS){//删除过期日志
							logFile.delete();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*String needDelFiel = logfile.format(getDateBefore());
		File file = new File(logDir, needDelFiel + MYLOGFILEName);
		if (file.exists()) {
			file.delete();
		}*/
	}
	
	public static long compareDaysDiff(String beginDate){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try{
		    Date d_beginDate = df.parse(beginDate);
		    Date d_endDate = new Date(System.currentTimeMillis());
		    long diff = d_endDate.getTime() - d_beginDate.getTime();
		    long months = diff / (1000 * 60 * 60 * 24);
		    return months;
		}catch (Exception e){
		}
		return 0;
	}
}