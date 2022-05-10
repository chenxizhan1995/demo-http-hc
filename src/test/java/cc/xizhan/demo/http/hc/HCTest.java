package cc.xizhan.demo.http.hc;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HCTest {
    @Test
    public void test1() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet httpGet = new HttpGet("https://httpbin.org/get");
            try(CloseableHttpResponse response = httpClient.execute(httpGet)){
                System.out.println(response.getCode() + "..." + response.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                EntityUtils.consume(entity);
            }
        }
    }
    @Test
    public void test2() throws IOException{
        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost post = new HttpPost("https://httpbin.org/post");

            List<NameValuePair> nvps = new ArrayList<>(3);
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            post.setEntity(new UrlEncodedFormEntity(nvps));

            try (CloseableHttpResponse response = client.execute(post)){
                System.out.println(response.getCode() + "..." + response.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                System.out.println(EntityUtils.toString(entity));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
