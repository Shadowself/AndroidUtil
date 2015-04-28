package httpUtils;

/**
 * Created by zhangguoyu on 2015/4/3.
 * when you call {@link AsyncRequest
 * request(String, TaskHandler)} ,you should
 * implement this, override the {@link #onNetError()},
 * {@link #onSuccess(T result)} ,{@link #onFail()},
 */
public abstract class TaskHandler<T> {

    /** finish */
    public abstract void onFinish();

    /** network is break */
    public abstract void onNetError();

    /**
     * have a successful response
     * @param result
     */
    public abstract void onSuccess(T result);

    /** if the timeout,server error */
    public abstract void onFail();

    /**
     * parse the InputStream,must be override this
     * @param result
     */
    public abstract T parseResult(String result);

    /**
     * 调用失败时回调该方法，注意此方法会在后台线程中执行。
     */
    public void onError(){

    }
}