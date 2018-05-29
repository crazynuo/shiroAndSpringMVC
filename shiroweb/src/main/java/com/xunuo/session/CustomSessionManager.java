package com.xunuo.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
* @Description:    重写retrieveSession 方法 避免多次读取session,造成 redis压力太大
* @Author:         xunuo
* @CreateDate:     2018/5/23 10:25
* @UpdateUser:     xunuo
* @UpdateDate:     2018/5/23 10:25
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class CustomSessionManager extends DefaultWebSessionManager {
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException{
        // 把第一次读取到的session放到request中
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest servletRequest = null;
        if (sessionKey instanceof WebSessionKey) {
            servletRequest = ((WebSessionKey) sessionKey).getServletRequest();
        }
        if (servletRequest != null && sessionId != null) {
            Session session = (Session) servletRequest.getAttribute(sessionId.toString());
            if (session != null) {
                return  session;
            }
        }
        // 从redis里取
        Session session = super.retrieveSession(sessionKey);
        if (servletRequest != null && sessionId != null) {
            // 把取出的session，放入requeset
            servletRequest.setAttribute(sessionId.toString(),session);
        }
        return  session;
}
}
