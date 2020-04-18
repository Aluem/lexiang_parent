package top.lexiang.common.shiro.session;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletMapping;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * 自定义的sessionManager
 */
public class CustomSessionManager extends DefaultWebSessionManager {

    /**
     * 头信息中有sessionid
     *      请求头：Authorization: sessionid
     *
     * 指定sessionid的获取方式
     */
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String accept = WebUtils.toHttp(request).getHeader("Accept");

        System.out.println("accept===>" + accept);
        //获取请求头Authorization中的数据Accept
        String id = WebUtils.toHttp(request).getHeader("Authorization");

        if (StringUtils.isEmpty(id)) {
            //如果没有携带，返回false
            System.out.println("请求头数据不对或者无数据");
            return false;
        } else {
            //请求头信息：Bearer sessionid
            System.out.println("传进来的sessoinid=====>" + id);
            id = id.replace("Bearer ","");
            //返回sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,"header");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID,Boolean.TRUE);
            System.out.println("sessionid已经保存");
            return id;
        }
    }

}
