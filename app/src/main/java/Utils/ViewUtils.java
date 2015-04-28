package Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * ViewUtil
 * 类描述：showToast,
 * 创建人：zgy
 * 创建日期：2015-5-30 上午3:11:07
 */
public class ViewUtils {
	
	private static Toast mToast;
	
	public static void showToast(Context context,
			int toastId) {
		if(mToast!=null){
			mToast.cancel();
		}
		mToast = Toast.makeText(context, context.getText(toastId).toString(), Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	public static void showToast(Context context,String msg){
		if(mToast!=null){
			mToast.cancel();
		}
		mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void showToastLongTime(Context context,String formatStr) {
		if(mToast!=null){
			mToast.cancel();
		}
		mToast = Toast.makeText(context, formatStr, Toast.LENGTH_LONG);
		mToast.show();
	}
	
	public static void showToastLongTime(Context context,int formatStr) {
		if(mToast!=null){
			mToast.cancel();
		}
		mToast = Toast.makeText(context, formatStr, Toast.LENGTH_LONG);
		mToast.show();
	}
	
	public static void cancelToast(){
		if(mToast!=null){
			mToast.cancel();
		}
	}
	
	public static void showAalertWithItems(Context context,String[] items, DialogInterface.OnClickListener dialogOnClickListener,CharSequence... titleMsg){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		//builder.setIcon(R.drawable.dialog_icon_warning);
		if(titleMsg == null){			
			builder.setTitle("温馨提示");
		}else if(titleMsg.length == 1){
			builder.setTitle(titleMsg[0]);
		}
		builder.setItems(items, dialogOnClickListener);
		builder.setCancelable(false);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		builder.show();
	}
	
	private static long lastClickTime;
	/**
	 * isFastDoubleClick
	 * 描述：防止重复点击
	 * 创建人：zgy
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			lastClickTime = time;
			return true;
		}
		return false;
	}
	
	/**
	 * 描述：显示键盘
	 * 创建人：zgy
	 * @param view
	 * 修改人：
	 * 修改时间：
	 */
	public static void setKeyboardFocus(final View view) {
		try {
			view.setFocusable(true);   
			view.setFocusableInTouchMode(true);   
			view.requestFocus();
			(new Handler()).postDelayed(new Runnable() {
				public void run() {
					view.dispatchTouchEvent(MotionEvent.obtain(
							SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, 0, 0, 0));
					view.dispatchTouchEvent(MotionEvent.obtain(
							SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_UP, 0, 0, 0));
				}
			}, 100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
