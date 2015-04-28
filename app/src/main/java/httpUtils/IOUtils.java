package httpUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {

	/**Stream reverse to String*/
	public static String stream2String(final InputStream instream)
			throws IOException {
		final StringBuilder sb = new StringBuilder();
		try {
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(instream, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally {
			closeStream(instream);
		}
		return sb.toString();
	}
	
	/**Stream reverse to String*/
	public static byte[] stream2Bytes(final InputStream instream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = instream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}

	/**close Stream*/
	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e("IOUtils", "Could not close stream", e);
			}
		}
	}
}
