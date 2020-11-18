package com.myProject.filter;

import com.alibaba.fastjson.JSON;
import com.bean.HttpResult;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

/**
 * @Author: menfeng
 * @Date: 2019/12/31 13:56
 * @Version 1.0
 */
@Slf4j
@Component
public class MyFilter extends ZuulFilter {

    @Override
    public Object run() {
        try {
            // TODO Auto-generated method stub
            Map<String, Object> data = new HashMap<>();
            //获取上下文
            RequestContext requestContext = getCurrentContext();
            //获取request对象
            HttpServletRequest request = requestContext.getRequest();
            //token对象
            String token = request.getHeader("token");
            if (StringUtils.isBlank((token))) {
                token = request.getParameter("token");
            }
            //登录校验逻辑  根据公司情况自定义 JWT
            //token为空，就不能访问
            if (StringUtils.isBlank(token)) {
                //停止访问，并返回出错的消息
                requestContext.setSendZuulResponse(false);
                //防止中文乱码
                requestContext.getResponse().setContentType("text/html;charset=UTF-8");
                //设置返回的状态码和正文
                requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                data.put("code",HttpStatus.UNAUTHORIZED.value());
                data.put("message",HttpStatus.UNAUTHORIZED.getReasonPhrase());
                requestContext.setResponseBody(JSON.toJSONString(data));
            } else {
                String url = "http://10.170.130.240:30115/cas/oauth2.0/profile?access_token=" + token;
                HttpResult doGet = HttpClientUtils.doGet(url);
                log.info("doget--{}",doGet);
                if(doGet.getCode() != 200){
                    requestContext.setSendZuulResponse(false);
                    requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                    data.put("code",HttpStatus.UNAUTHORIZED.value());
                    data.put("message",HttpStatus.UNAUTHORIZED.getReasonPhrase());
                    requestContext.setResponseBody(JSON.toJSONString(data));
                }
            }
        } catch (Exception e) {
            //正常的话，继续向下走
            log.info("zuulFilter发生错误", e);
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        // TODO Auto-generated method stub
        return true; //表示是否需要执行该filter，true表示执行，false表示不执行
    }

    @Override
    public int filterOrder() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String filterType() {
        // TODO Auto-generated method stub
        return "pre"; //定义filter的类型，有pre、route、post、error四种
    }
}
