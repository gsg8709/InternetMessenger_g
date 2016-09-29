package com.msg.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.text.TextUtils;
/**
 * 
 * @ClassName: Utils 
 * @Description: 工具类
 * @author gengsong@zhongsou.com
 * @date 2014年4月15日 下午2:29:24 
 * @version 3.5.2
 */
public class Utils {

	/*
	 * 从服务器端获取数据
	 */
	public static String getData(String baseURL,
			List<NameValuePair> requestParams) {
		if (null != requestParams) {
			HttpPost httpPost = new HttpPost(baseURL);
			String responseResult = null;
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(requestParams,
						HTTP.UTF_8));
				HttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					responseResult = EntityUtils.toString(httpResponse
							.getEntity());
				} else {
					return null;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpPost.abort();
			}
			if (!TextUtils.isEmpty(responseResult))
				return responseResult;
		}
		return null;
	}
}
