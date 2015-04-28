package httpUtils;


/**
 * the implements of {@link TaskHandler} ,handler the {@link String}
 */
public abstract class ByteTaskHandler extends TaskByteHandler<byte[]> {

	private final String TAG = ByteTaskHandler.class.getSimpleName();

	@Override
	public byte[] parseResult(byte[] result) {
		return result;
	}

}
