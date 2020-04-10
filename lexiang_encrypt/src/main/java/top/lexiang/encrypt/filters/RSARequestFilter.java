package top.lexiang.encrypt.filters;

import com.google.common.base.Charsets;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import top.lexiang.encrypt.rsa.RsaKeys;
import top.lexiang.encrypt.service.RsaService;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Component
public class RSARequestFilter extends ZuulFilter {

    @Autowired
    private RsaService rsaService;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE; //过滤器类型 pre：前置过滤器
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1; //优先级，数字越大，优先级越低
    }

    @Override
    public boolean shouldFilter() {
        return true; //是否执行过滤器
    }

    @Override
    public Object run() throws ZuulException {
        //实现拦截后做的事情
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        try {
            String decryptData = null;
            HashMap dataMap = null;
            String token = null;

            String url = request.getRequestURL().toString();
            /**
             * 获取输入流 为什么使用流而不是getParameter()
             *      原因：传进来的字符串并不是 key value形式
             *      tip:前端传进来的是密文 作用：防止数据被抓包、修改
             * */
            InputStream stream = ctx.getRequest().getInputStream();
            String requestParam = StreamUtils.copyToString(stream, Charsets.UTF_8);

            if(!Strings.isNullOrEmpty(requestParam)) {
                System.out.println(String.format("请求体中的密文：%s", requestParam));
                decryptData = rsaService.RSADecryptDataPEM(requestParam, RsaKeys.getServerPrvKeyPkcs8());
                System.out.println(String.format("解密后的内容：%s", decryptData));
            }

            System.out.println(String.format("request:%s >>> %s, data=%s", request.getMethod(), url, decryptData));

            if (!Strings.isNullOrEmpty(decryptData)) {
                System.out.println("json字符串写入request body");
                final  byte[] reqBodyBytes = decryptData.getBytes();
//
                ctx.setRequest(new HttpServletRequestWrapper(request) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(reqBodyBytes);
                    }

                    @Override
                    public int getContentLength() {
                        return reqBodyBytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return reqBodyBytes.length;
                    }
                });
            }

            System.out.println("转发request");
            //设置request请求头中的Content-Type为application/json，否则api接口模块需要进行url转码操作
            ctx.addZuulRequestHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON) + ";charset=UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(this.getClass().getName() + "运行出错" + e.getMessage());
        }
        return null;
    }
}
