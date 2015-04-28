package httpUtils;


/**
 * the implements of {@link TaskHandler} ,handler the {@link String}
 */
public abstract class StringTaskHandler extends TaskHandler<String> {

	private final String TAG = StringTaskHandler.class.getSimpleName();

	@Override
	public String parseResult(String result) {
		return result;
	}

}
