package com.beta.controller;

import com.beta.constants.ExceptionPageTemplate;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by beta on 2016/11/2.
 */
@RestController
public class IndexController {
    Logger log = LoggerFactory.getLogger(IndexController.class);

    /**
     * 拦截所有的请求，重新构造请求，使用Proxy发送到目标分服务器
     * <p>
     * 1.使用httpClient
     * 2.如何保持请求信息
     *
     * @param request
     */
    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, HttpServletResponse response) {
        //TODO 这里的地址应该从url中解析出来

        //String url = "http://news.baidu.com/";
        String url = "http://www.freebuf.com/articles/web/21832.html";
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            String headName = headNames.nextElement();
            log.info("request cookie==============" + headName + "=" + request.getHeader(headName));
            if ("host".equalsIgnoreCase(headName)) continue;
            get.addHeader(headName, request.getHeader(headName));
        }
        HttpResponse res = null;
        try {
            res = client.execute(get);
            log.info(request.getRemoteAddr() + "|" + "Response Code : "
                    + res.getStatusLine().getStatusCode());
            String result = getResponseString(res);
            response.setStatus(res.getStatusLine().getStatusCode());
            setCookieForResponse(res, response);
            return result;
        } catch (IOException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            log.error(request.getRemoteAddr(), e.fillInStackTrace());
            return ExceptionPageTemplate.SERVER_ERROR_PAGE;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(HttpServletRequest request, HttpServletResponse response) {
        //TODO 这里的地址应该从url中解析出来

        String url = "http://www.baidu.com";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            String headName = headNames.nextElement();
            post.addHeader(headName, request.getHeader(headName));
        }
        HttpResponse res = null;
        try {
            setRequestEntity(request, post);
            res = client.execute(post);
            log.info(request.getRemoteAddr() + "|" + "Response Code : "
                    + res.getStatusLine().getStatusCode());
            String result = getResponseString(res);
            response.setStatus(res.getStatusLine().getStatusCode());
            setCookieForResponse(res, response);
            return result;
        } catch (IOException e) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            log.error(request.getRemoteAddr(), e.fillInStackTrace());
            return ExceptionPageTemplate.SERVER_ERROR_PAGE;
        }
    }

    private void setCookieForResponse(HttpResponse res, HttpServletResponse response) {
        Header[] cookies = res.getHeaders("Set-Cookie");
        if (null == cookies) return;
        for (Header c : cookies) {
            response.addHeader(c.getName(), c.getValue());
        }
    }

    /**
     * 封装请求参数
     *
     * @param request
     * @param post
     * @throws UnsupportedEncodingException
     */
    private void setRequestEntity(HttpServletRequest request, HttpPost post) throws UnsupportedEncodingException {
        Enumeration<String> params = request.getParameterNames();
        if (null != params) {
            List<NameValuePair> urlParameters = new ArrayList<>();
            while (params.hasMoreElements()) {
                String param = params.nextElement();
                urlParameters.add(new BasicNameValuePair(param, request.getParameter(param)));
            }
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
    }

    /**
     * 封装响应字符串
     *
     * @param res
     * @return
     * @throws IOException
     */
    private String getResponseString(HttpResponse res) throws IOException {
        Header[] headers = res.getAllHeaders();
        for (Header h : headers) {
            log.info("response cookie****************" + h.getName() + "|" + h.getValue());
        }
        String stringEncoding = "gbk";
        Header contentHead = res.getFirstHeader("Content-Type");
        if ((null != contentHead) && ((-1 != contentHead.getValue().indexOf("utf-8"))
                ||(-1 != contentHead.getValue().indexOf("UTF-8")))) {
            stringEncoding = "utf-8";
        }
        HttpEntity httpEntity = res.getEntity();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(httpEntity.getContent(), stringEncoding));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

}
