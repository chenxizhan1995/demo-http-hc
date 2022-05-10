package cc.xizhan.demo.http.hc;

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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 每个请求都重新创建 HttpClient，与复用 HttpClient，其效率相差多大？
 * <p>
 * 1. 单次请求，新建 HttpClient + POST 发送三个名值对
 * 2. 单次请求，新建 HttpClient + POST 发送 1MB 的文件
 * 3. N 次请求 POST 发送三个名值对
 * 3.1 每个请求都新建 HttpClient
 * 3.2 只新建一次，复用 HttpClient
 * 4. N 次请求 POST 发送 1MB 的文件
 * 4.1 每个请求都新建 HttpClient
 * 4.2 只新建一次，复用 HttpClient
 */
public class HcTimingTest {

    public CloseableHttpClient client() throws IOException {
        return HttpClients.createDefault();
    }

    /**
     * 新建 Client，POST 发送三个名值对
     *
     * @throws IOException
     * @throws ParseException
     */
    public void postNVOnce() throws IOException, ParseException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            postNV(client);
        }
    }

    /**
     * 新建 Client，POST 发送三个名值对
     *
     * @throws IOException
     * @throws ParseException
     */
    public void postNV(CloseableHttpClient client) throws IOException, ParseException {
        HttpPost post = new HttpPost("http://172.16.12.17/post");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", "Jack"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        nvps.add(new BasicNameValuePair("comment", "blablabla...."));

        post.setEntity(new UrlEncodedFormEntity(nvps));

        try (CloseableHttpResponse res = client.execute(post)) {
            HttpEntity resEntity = res.getEntity();
            if (resEntity != null) {
                String s = EntityUtils.toString(resEntity);
            } else {
                System.out.println("响应为空");
            }
        }
    }

    @Test
    public void test1() throws IOException, ParseException {
        System.out.println("单次请求，新建 HttpClient + POST 发送三个名值对");
        long start = System.currentTimeMillis();
        postNVOnce();
        long end = System.currentTimeMillis();
        System.out.println("time used: " + (end - start) + " ms");
    }
    @Test
    public void test3_1() throws IOException, ParseException {
        System.out.println("N 次请求 POST 发送三个名值对，每次新建client");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            postNVOnce();
        }
        long end = System.currentTimeMillis();
        System.out.println("time used: " + (end - start) + " ms");
    }
    @Test
    public void test3_2() throws IOException, ParseException {
        System.out.println("N 次请求 POST 发送三个名值对，重用client");

        long start = System.currentTimeMillis();
        try(CloseableHttpClient client = HttpClients.createDefault()){
            for (int i = 0; i < 100; i++) {
                postNV(client);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("time used: " + (end - start) + " ms");
    }
}
/* 实验记录
单次请求，新建 HttpClient + POST 发送三个名值对
time used: 10 ms

N 次请求 POST 发送三个名值对，每次新建client
time used: 1978 ms
N 次请求 POST 发送三个名值对，重用client
time used: 672 ms


单次请求，新建 HttpClient + POST 发送三个名值对
time used: 11 ms

N 次请求 POST 发送三个名值对，每次新建client
time used: 1939 ms

N 次请求 POST 发送三个名值对，重用client
time used: 913 ms

所以，创建100次客户端，1s，平均一次 10ms 多一些
*/
