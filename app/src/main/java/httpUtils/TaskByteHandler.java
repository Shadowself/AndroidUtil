package httpUtils;

/**
 */
public abstract class TaskByteHandler<T> {
	
	/** finish */
	public abstract void onFinish();
	
	/** network is break */
	public abstract void onNetError();

	/**
	 * have a successful response
	 * 
	 * @param result
	 */
	public abstract void onSuccess(T result);

	/** if the timeout,server error */
	public abstract void onFail();

	/**
	 * parse the InputStream,must be override this
	 * 
	 * @param result
	 */
	public abstract byte[] parseResult(byte[] result);
	
}
