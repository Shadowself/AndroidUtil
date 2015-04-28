package httpUtils;

import java.util.Map;

/**
 * Created by zhangguoyu on 2015/4/3.
 */
public interface AsyncRequest {

    void request(String url, TaskHandler handler);

    void request(String url, Map<String, String> params, TaskHandler handler);

}
