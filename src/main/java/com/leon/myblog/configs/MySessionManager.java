package com.leon.myblog.configs;

import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.UUID;

/**
 * 自定义session管理
 * <br/>
 * 传统结构项目中，shiro从cookie中读取sessionId以此来维持会话，在前后端分离的项目中（也可在移动APP项目使用），
 * 我们选择在ajax的请求头中传递sessionId，因此需要重写shiro获取sessionId的方式。
 * 自定义MySessionManager类继承DefaultWebSessionManager类，重写getSessionId方法
 * @author ：leon
 * @date ：2020/02/11 10:52
 */

public class MySessionManager extends DefaultWebSessionManager {

    /**
     * 这个是服务端要返回给客户端，
     */
    public final static String TOKEN_NAME = "TOKEN";
    /**
     * 这个是客户端请求给服务端带的header
     */
    public final static String AUTHORIZATION = "Authorization";
    //public final static Logger LOG =         LoggerFactory.getLogger(MySessionManager.class);

    @Override
    public Serializable getSessionId(SessionKey key) {
        Serializable sessionId = key.getSessionId();
        if(sessionId == null){
            HttpServletRequest request = WebUtils.getHttpRequest(key);
            HttpServletResponse response = WebUtils.getHttpResponse(key);
            sessionId = this.getSessionId(request,response);
        }
        //HttpServletRequest request = WebUtils.getHttpRequest(key);
        //request.setAttribute(TOKEN_NAME,sessionId.toString());
        return sessionId;
    }

    @Override
    protected Serializable getSessionId(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(AUTHORIZATION);
        if(token == null){
            token = UUID.randomUUID().toString();
        }

        //这段代码还没有去查看其作用，但是这是其父类中所拥有的代码，重写完后我复制了过来...开始
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
                ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, token);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());
        //这段代码还没有去查看其作用，但是这是其父类中所拥有的代码，重写完后我复制了过来...结束
        return token;
    }

}