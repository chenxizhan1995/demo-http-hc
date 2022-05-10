package cc.xizhan.demo.http.hc;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    // TODO: 每次请求都重新创建客户端的代价有多大？比比速度

    /**
     * 发送文件
     */
    @Test
    public void test4()throws IOException{
        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost post = new HttpPost("https://httpbin.org/post");
            URL url = this.getClass().getClassLoader().getResource("300.jpg");
            FileBody img1 = new FileBody(new File(url.getFile()));
            StringBody comment = new StringBody("一张图片", ContentType.TEXT_PLAIN);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("image1", img1)
                    .addPart("comment", comment)
                    .build();
            post.setEntity(reqEntity);

            System.out.println("post data:" + post);
            try (CloseableHttpResponse response = client.execute(post)){
                System.out.println("-----------------------------------------");
                System.out.println("response: " + response);
                HttpEntity resEntity = response.getEntity();
                if (resEntity!= null){
                    System.out.println("Content length:" + resEntity.getContentLength());
                    EntityUtils.consume(resEntity);
                }
            }
        }
    }

    @Test
    public void test6(){
        System.out.println(ContentType.TEXT_PLAIN); // text/plain; charset=ISO-8859-1
        System.out.println(ContentType.APPLICATION_JSON); // application/json; charset=UTF-8
        System.out.println(ContentType.MULTIPART_FORM_DATA); // multipart/form-data; charset=ISO-8859-1
        ContentType type1 = ContentType.create("application/json", "UTF-8");
        System.out.println(type1);
    }
}
