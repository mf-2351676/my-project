package com.myProject.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: menfeng
 * @Date: 2019/12/31 13:56
 * @Version 1.0
 */
@Component
@Slf4j
public class MyFilter extends ZuulFilter {

    @Override
    public Object run() {
        // TODO Auto-generated method stub
        JSONObject details = (JSONObject) JSONObject.toJSON(SecurityContextHolder.getContext().getAuthentication().getDetails());
        log.info("token -------- {}" ,details.getString("tokenValue"));
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest req = (HttpServletRequest)RequestContext.getCurrentContext().getRequest();
        requestContext.addZuulRequestHeader("token", details.getString("tokenValue"));
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
