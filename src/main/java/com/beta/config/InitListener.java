package com.beta.config;


import com.beta.constants.Constant;
import com.beta.constants.ExceptionPageTemplate;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;

/**
 * Created by beta on 2016/11/3.
 */
public class InitListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {
            //加载常量配置项
            configConstant();
            //加载错误页面配置项
            loadPageTemplate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将定义的模版文件注入到字符串常量当中去
     * @throws IOException
     */
    private void loadPageTemplate() throws IOException {
        InputStream cons = this.getClass().getResourceAsStream(Constant.SERVER_ERROR_PAGE_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(cons));
        StringBuffer sb = new StringBuffer();
        String line="";
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        ExceptionPageTemplate.SERVER_ERROR_PAGE = sb.toString();
    }

    private void configConstant() {

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
