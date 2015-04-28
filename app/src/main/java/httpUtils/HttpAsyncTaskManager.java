package httpUtils;

/**
 * Created by zhangguoyu on 2015/4/3.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * start http request from this,it extends AsyncTask
 */
public class HttpAsyncTaskManager implements AsyncRequest {

    private Context mContext;
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private static final String TAG = HttpAsyncTaskManager.class
            .getSimpleName();

    public HttpAsyncTaskManager(Context context) {
        this.mContext = context;
    }

    /**
     * JsonString Get or Post for String
     * @param url
     * @param param
     * @param handler
     */
    public void requestStream(String url, String param,
                              TaskHandler handler) {
        if (mContext != null) {
            synchronized (this) {
                new HttpStreamTask(mContext, url, 1, param, handler).execute("");
            }

        }
    }

    /**
     *JsonString Get or Post for byte
     * @param url
     * @param param
     * @param handler
     */
    public void requestStreamBytes(String url, String param,
                                   TaskByteHandler handler) {
        if (mContext != null) {
            synchronized (this) {
                new HttpStreamBytesTask(mContext, url, 1, param, handler).execute("");
            }

        }
    }

    /**
     * post picture and describe
     * @param url
     * @param param
     * @param handler
     */
    public void requestMapStream(String url, List<String> param,
                              TaskHandler handler) {
        if (mContext != null) {
            synchronized (this) {
                new HttpMapStreamTask(mContext, url, 1, param, handler).execute("");
            }

        }
    }

    /**
     * get request
     *
     * @param url
     * @param handler
     */
    public void request(String url, TaskHandler handler) {
        if (mContext != null) {
            synchronized (this) {
                new HttpTask(mContext, url, 0, null, handler).execute("");
            }

        }
    }

    /**
     * post request
     *
     * @param url
     * @param params
     *            url parameters,if not use null
     * @param handler
     */
    public void request(String url, Map<String, String> params,
                        TaskHandler handler) {
        if (mContext != null) {
            synchronized (this) {
                new HttpTask(mContext, url, 1, params, handler).execute("");
            }
        }
    }

    /** Map task
     *
     * need test、、
     *
     **/
    private static class HttpTask extends AsyncTask<String, Integer, String> {

        Context context;
        String url;
        /** 0 is get,1 is post */
        int type = 0;
        TaskHandler handler;
        Map<String, String> params;

        public HttpTask(Context context, String url, int type,
                        Map<String, String> params, TaskHandler handler) {
            this.context = context;
            this.url = url;
            this.type = type;
            this.handler = handler;
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            if (!NetWorkStatus.networkIsAvailable(context)) {// network is break
                handler.onNetError();
            }
        }

        @Override
        protected String doInBackground(String... param) {
            // TODO Auto-generated method stub
            if (NetWorkStatus.networkIsAvailable(context)) {// network is well
                String responseStr = null;
                InputStream is = null;
                Request request = null;
                Response response = null;
                if(type == 0){  //get
                    try {
                        request = new Request.Builder().url(url).build();
                        response = OkHttpUtil.execute(request);
                        if (response.isSuccessful()) {
                            is = response.body().byteStream();
                            responseStr = IOUtils.stream2String(is);
                            return responseStr;
                        } else {
                            handler.onError();
                            Log.e(TAG, IOUtils.stream2String(is));
//                            throw new IOException("Unexpected code " + response);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "____get___" + e.toString() + "_____");
                        handler.onError();
                    }
                } else {// post
                    try {

                        List<NameValuePair> pair = new ArrayList<NameValuePair>();
                        if (params != null && !params.isEmpty()) {
                            for (Map.Entry<String, String> entry : params
                                    .entrySet()) {
                                pair.add(new BasicNameValuePair(entry.getKey(),
                                        entry.getValue()));
                            }
                        }

                        RequestBody body = new MultipartBuilder()
                                .type(MultipartBuilder.FORM)
                                .addPart(
                                        RequestBody.create(MediaType.parse("UTF-8"), pair.toString()))
                                .build();
                        request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        response = OkHttpUtil.execute(request);
                        if (response.isSuccessful()) {
                            is = response.body().byteStream();
                            return IOUtils.stream2String(is);
                        } else {
                            handler.onError();
                            Log.e(TAG, IOUtils.stream2String(is));
                        }

                    } catch (Exception e) {

                        Log.e(TAG, "____post___" + e.toString() + "_____");
                        handler.onError();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (result == null) {
                handler.onFail();
            } else {
                handler.onSuccess(handler.parseResult(result));
            }
            handler.onFinish();
        }

    }

    /** JsonString
     * String task
     */
    private static class HttpStreamTask extends
            AsyncTask<String, Integer, String> {

        Context context;
        String url;
        /** 0 is get,1 is post */
        int type = 0;
        TaskHandler handler;
        String json;

        public HttpStreamTask(Context context, String url, int type,
                              String json, TaskHandler handler) {
            this.context = context;
            this.url = url;
            this.type = type;
            this.handler = handler;
            this.json = json;
        }

        @Override
        protected void onPreExecute() {
            if (!NetWorkStatus.networkIsAvailable(context)) {// network is break
                handler.onNetError();
            }
        }

        @Override
        protected String doInBackground(String... param) {
            // TODO Auto-generated method stub
            if (NetWorkStatus.networkIsAvailable(context)) {// network is well
                String responseStr = null;
                InputStream is = null;
                Request request = null;
                Response response = null;
                if (type == 0) {// get
                    try {
                        request = new Request.Builder().url(url).build();
                        response = OkHttpUtil.execute(request);
                        if (response.isSuccessful()) {
                            is = response.body().byteStream();
                            responseStr = IOUtils.stream2String(is);
                            return responseStr;
                        } else {
                            handler.onError();
                            Log.e(TAG, IOUtils.stream2String(is));
//                            throw new IOException("Unexpected code " + response);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "____get___" + e.toString() + "_____");
                        handler.onError();
                    }
                } else {// post
                    try {
                        RequestBody body = RequestBody.create(JSON, json);
                        request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        response = OkHttpUtil.execute(request);
                        if (response.code() == 200) {
                            is = response.body().byteStream();
                            return IOUtils.stream2String(is);
                        } else {
                            handler.onError();
                            Log.e(TAG, IOUtils.stream2String(is));
                        }

                    } catch (Exception e) {

                        Log.e(TAG, "____post___" + e.toString() + "_____");
                        handler.onError();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (result == null) {
                handler.onFail();
            } else {
                handler.onSuccess(handler.parseResult(result));
            }
            handler.onFinish();
        }

    }

    /** post picture
     * String task
     */
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    private static class HttpMapStreamTask extends
            AsyncTask<String, Integer, String> {

        Context context;
        String url;
        /** 0 is get,1 is post */
        int type = 0;
        TaskHandler handler;
        List<String> json;

        public HttpMapStreamTask(Context context, String url, int type,
                              List<String> json, TaskHandler handler) {
            this.context = context;
            this.url = url;
            this.type = type;
            this.handler = handler;
            this.json = json;
        }

        @Override
        protected void onPreExecute() {
            if (!NetWorkStatus.networkIsAvailable(context)) {// network is break
                handler.onNetError();
            }
        }

        @Override
        protected String doInBackground(String... param) {
            // TODO Auto-generated method stub
            if (NetWorkStatus.networkIsAvailable(context)) {// network is well
                String responseStr = null;
                InputStream is = null;
                Request request = null;
                Response response = null;
                if (type == 0) {// get
                    try {
                        request = new Request.Builder().url(url).build();
                        response = OkHttpUtil.execute(request);
                        if (response.isSuccessful()) {
                            is = response.body().byteStream();
                            responseStr = IOUtils.stream2String(is);
                            return responseStr;
                        } else {
                            handler.onError();
                            Log.e(TAG, IOUtils.stream2String(is));
//                            throw new IOException("Unexpected code " + response);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "____get___" + e.toString() + "_____");
                        handler.onError();
                    }
                } else {// post
                    try {
                        long time=System.currentTimeMillis();
                        RequestBody requestBody = new MultipartBuilder()
                                .type(MultipartBuilder.FORM)
                                .addFormDataPart("storehouseId",json.get(0))
                                .addFormDataPart("title", json.get(1))
                                .addFormDataPart("image", time+"pest.jpg",
                                        RequestBody.create(MEDIA_TYPE_JPG, new File(json.get(2))))
                                .build();

                        request = new Request.Builder()
                                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                                .url(url)
                                .post(requestBody)
                                .build();

                        response = OkHttpUtil.execute(request);
                        if (response.code() == 200) {
                            is = response.body().byteStream();
                            return IOUtils.stream2String(is);
                        } else {
                            handler.onError();
                            Log.e(TAG, IOUtils.stream2String(is));
                        }

                    } catch (Exception e) {

                        Log.e(TAG, "____post___" + e.toString() + "_____");
                        handler.onError();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (result == null) {
                handler.onFail();
            } else {
                handler.onSuccess(handler.parseResult(result));
            }
            handler.onFinish();
        }

    }

    /**
     * JsonString
     *  byte task
     */
    private static class HttpStreamBytesTask extends AsyncTask<String, Integer, byte[]> {

        Context context;
        String url;
        /** 0 is get,1 is post */
        int type = 0;
        TaskByteHandler handler;
        String json;

        public HttpStreamBytesTask(Context context, String url, int type, String json, TaskByteHandler handler) {
            this.context = context;
            this.url = url;
            this.type = type;
            this.handler = handler;
            this.json = json;
        }

        @Override
        protected void onPreExecute() {
            if (!NetWorkStatus.networkIsAvailable(context)) {// network is break
                handler.onNetError();
            }
        }

        @Override
        protected byte[] doInBackground(String... param) {
            // TODO Auto-generated method stub
            if (NetWorkStatus.networkIsAvailable(context)) {// network is well
                InputStream is = null;
                byte[] responseStr = null;
                Request request = null;
                Response response = null;
                if (type == 0) {// get
                    try {
                        request = new Request.Builder().url(url).build();
                        response = OkHttpUtil.execute(request);
                        if (response.isSuccessful()) {
                            is = response.body().byteStream();
                            return IOUtils.stream2Bytes(is);
                        } else {
                            Log.e(TAG, IOUtils.stream2String(is));
//                            throw new IOException("Unexpected code " + response);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "____get___" + e.toString() + "_____");
                    }
                } else {// post
                    try {
                        RequestBody body = RequestBody.create(JSON, json);
                        request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();

                        response = OkHttpUtil.execute(request);
                        if (response.isSuccessful()) {
                            is = response.body().byteStream();
                            return IOUtils.stream2Bytes(is);
                        } else {
                            Log.e(TAG, IOUtils.stream2String(is));
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "____post___" + e.toString() + "_____");
                    }
                }
            }
            return null;
        }

        private int getShort(byte[] data) {
            return (int)((data[0]<<8) | data[1]&0xFF);
        }

        protected void onPostExecute(byte[] result) {
            if (result == null) {
                handler.onFail();
            } else {
                handler.onSuccess(handler.parseResult(result));
            }
            handler.onFinish();
        }
    }

}
