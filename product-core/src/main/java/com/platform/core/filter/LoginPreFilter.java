package com.platform.core.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWTExpiredException;
import com.platform.common.cache.Cache;
import com.platform.common.enums.LoginTypeEnum;
import com.platform.common.util.IpUtils;
import com.platform.common.util.ResponseUtil;
import com.platform.core.processor.FormLoginPostProcessor;
import com.platform.core.processor.JsonLoginPostProcessor;
import com.platform.core.processor.LoginPostProcessor;
import com.platform.core.util.AuthenticationUtils;
import com.platform.core.util.TokenUtil;
import com.platform.core.wrapper.ParameterRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class LoginPreFilter extends OncePerRequestFilter {

    private Cache cache;

    private static final Logger log = LoggerFactory.getLogger(LoginPreFilter.class);
    private AntPathRequestMatcher requiresAuthenticationRequestMatcher;
    private Map<LoginTypeEnum, LoginPostProcessor> processors = new EnumMap(LoginTypeEnum.class);

    public LoginPreFilter(String loginProcessingUrl,Cache cache) {
        Assert.notNull(loginProcessingUrl, "loginProcessingUrl must not be null");
        this.requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(loginProcessingUrl, HttpMethod.POST.name());
        this.processors.put(LoginTypeEnum.JSON, new JsonLoginPostProcessor());
        this.processors.put(LoginTypeEnum.FORM, new FormLoginPostProcessor());
        this.cache = cache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.requiresAuthenticationRequestMatcher.matches(request)) {
            ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper(request);
            LoginPostProcessor loginPostProcessor = (LoginPostProcessor) this.processors.get(LoginTypeEnum.JSON);
            String username = loginPostProcessor.obtainUsername(parameterRequestWrapper);
            String password = loginPostProcessor.obtainPassword(parameterRequestWrapper);
            String checkCode = loginPostProcessor.obtainIdentifyCode(parameterRequestWrapper);

            String ipAddr = IpUtils.getIpAddr(request).replaceAll("\\.", "");
            String code = cache.get("captcha" + ipAddr, String.class);
            if (StringUtils.isBlank(code) || StringUtils.isBlank(checkCode)) {
                ResponseUtil.filterResponseResultError(request, response, "product.error.00007");
                return;
            }
            if (!code.equals(checkCode)) {
                ResponseUtil.filterResponseResultError(request, response, "product.error.00008");
                return;
            }
            cache.del("captcha" + ipAddr);

            parameterRequestWrapper.setAttribute("username", username);
            parameterRequestWrapper.setAttribute("password", password);
            JSONObject jsonBody = JSON.parseObject(parameterRequestWrapper.getBody());
            String loginType = (String) jsonBody.get("loginType");
            if ("OAuth".equals(loginType)) {
                this.dealUniqueUser(request, response, parameterRequestWrapper, username);
            } else {
                filterChain.doFilter(parameterRequestWrapper, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void dealUniqueUser(HttpServletRequest request, HttpServletResponse response, ParameterRequestWrapper parameterRequestWrapper, String username) throws IOException, ServletException {
        JSONObject jsonBody = JSON.parseObject(parameterRequestWrapper.getBody());
        String oAuthToken = (String) jsonBody.get("authToken");
        if (StringUtils.isAnyBlank(new CharSequence[]{username, oAuthToken})) {
            ResponseUtil.filterResponseResultError(request, response, "pcmc.oauth.error.0005");
        } else {
            try {
                String tokenUsername = TokenUtil.getUsernameFromToken(oAuthToken);
                String tokenType = TokenUtil.getTypeFromToken(oAuthToken);
                if (!StringUtils.equals(username, tokenUsername) || !"oauth".equals(tokenType)) {
                    ResponseUtil.filterResponseResultError(request, response, "pcmc.oauth.error.0002");
                    return;
                }
            } catch (JWTExpiredException var9) {
                log.error("OAuthToken已失效", var9);
                ResponseUtil.filterResponseResultError(request, response, "pcmc.oauth.error.0004");
                return;
            } catch (Exception var10) {
                log.error("OAuthToken解析异常", var10);
                ResponseUtil.filterResponseResultError(request, response, "pcmc.oauth.error.0003");
                return;
            }

            AuthenticationUtils.setAuthentication(request, username);
            request.getRequestDispatcher("/authentication/system/login/success").forward(parameterRequestWrapper, response);
        }
    }
}
