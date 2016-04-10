package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
import com.angkorteam.mbaas.model.entity.tables.MobileTable;
import com.angkorteam.mbaas.model.entity.tables.records.MobileRecord;
import org.jooq.DSLContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 4/10/16.
 */
public class IdentityHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private DSLContext context;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(com.angkorteam.mbaas.plain.Identity.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MobileTable mobileTable = Tables.MOBILE.as("mobileTable");
        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String userAgent = webRequest.getHeader(HttpHeaders.USER_AGENT);
        String clientId = webRequest.getHeader("client_id");
        String clientSecret = webRequest.getHeader("client_secret");
        String remoteIp;
        String mobileId = null;
        String userId = null;
        String applicationId = null;
        String accessToken = null;
        if (authorization != null && !"".equals(authorization) && authorization.length() >= 7 && authorization.substring(0, 6).toLowerCase().equals("bearer")) {
            accessToken = authorization.substring(7);
        }
        MobileRecord mobileRecord = null;
        if (accessToken != null && !"".equals(accessToken)) {
            mobileRecord = context.select(mobileTable.fields()).from(mobileTable).where(mobileTable.ACCESS_TOKEN.eq(accessToken)).fetchOneInto(mobileTable);
        }

        remoteIp = webRequest.getHeader("X-FORWARDED-FOR");
        if (remoteIp == null) {
            remoteIp = webRequest.getNativeRequest(HttpServletRequest.class).getRemoteAddr();
        }

        if (mobileRecord != null) {
            mobileId = mobileRecord.getMobileId();
            if (clientId != null && !"".equals(clientId)) {
                clientId = mobileRecord.getClientId();
            }
            userId = mobileRecord.getUserId();
            applicationId = mobileRecord.getApplicationId();
        }

        Identity identity = new Identity(userId, applicationId, clientId, mobileId, userAgent, remoteIp, clientSecret, accessToken);
        return identity;
    }

    public DSLContext getContext() {
        return context;
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }
}