package com.zjc.security.web.config;

import com.zjc.dao.model.SecurityUser;
import com.zjc.dao.model.SysUser;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName 自定义用户session管理
 * @Description
 * @Author ZJC
 * @Date 2020/4/30 13:07
 */
public class MySessionAuthenticationStrategy implements SessionAuthenticationStrategy {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final SessionRegistry sessionRegistry;
    private boolean exceptionIfMaximumExceeded = false;
    private int maximumSessions = 1;

    /**
     * @param sessionRegistry the session registry which should be updated when the
     * authenticated session is changed.
     */
    public MySessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
        Assert.notNull(sessionRegistry, "The sessionRegistry cannot be null");
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * In addition to the steps from the superclass, the sessionRegistry will be updated
     * with the new session information.
     */
    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request, HttpServletResponse response) throws SessionAuthenticationException{



        final List<SessionInformation> sessions = sessionRegistry.getAllSessions(
                authentication.getPrincipal(), false);

        int sessionCount = sessions.size();
        int allowedSessions = getMaximumSessionsForThisUser(authentication,sessionRegistry);



//        if (allowedSessions == -1) {
//            // 禁止登陆
//            return;
//        }

        if (sessionCount == allowedSessions) {
            HttpSession session = request.getSession(false);

            if (session != null) {
                // Only permit it though if this request is associated with one of the
                // already registered sessions
                for (SessionInformation si : sessions) {
                    if (si.getSessionId().equals(session.getId())) {
                        return;
                    }
                }
            }
            // If the session is null, a new one will be created by the parent class,
            // exceeding the allowed number
        }


//        try {
            allowableSessionsExceeded(sessions, allowedSessions, sessionRegistry);
//        } catch (Exception e) {
//            try {
//                response.sendRedirect("timeout");
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }

        //注册本次登陆的session到sessionRegistry用于下次判断
        sessionRegistry.registerNewSession(request.getSession().getId(),
                authentication.getPrincipal());
    }

    /**
     * Method intended for use by subclasses to override the maximum number of sessions
     * that are permitted for a particular authentication. The default implementation
     * simply returns the <code>maximumSessions</code> value for the bean.
     *
     * @param authentication to determine the maximum sessions for
     *
     * @return either -1 meaning unlimited, or a positive integer to limit (never zero)
     */
    protected int getMaximumSessionsForThisUser(Authentication authentication,SessionRegistry sessionRegistry) {

        SecurityUser currUser = (SecurityUser) authentication.getPrincipal();
        //获得所有登陆的用户及其sessionId
        List<SessionInformation> securityUsers = sessionRegistry.getAllSessions(currUser,false);

        return securityUsers.size();
    }

    /**
     * Allows subclasses to customise behaviour when too many sessions are detected.
     *
     * @param sessions either <code>null</code> or all unexpired sessions associated with
     * the principal
     * @param allowableSessions the number of concurrent sessions the user is allowed to
     * have
     * @param registry an instance of the <code>SessionRegistry</code> for subclass use
     *
     */
    protected void allowableSessionsExceeded(List<SessionInformation> sessions,
                                             int allowableSessions, SessionRegistry registry)
            throws SessionAuthenticationException {
        if (exceptionIfMaximumExceeded || (sessions == null)) {
            throw new SessionAuthenticationException(messages.getMessage(
                    "ConcurrentSessionControlAuthenticationStrategy.exceededAllowed",
                    new Object[] { Integer.valueOf(allowableSessions) },
                    "Maximum sessions of {0} for this principal exceeded"));
        }

        if(allowableSessions >= maximumSessions){
            throw new SessionAuthenticationException("当前用户已在其他地方登陆！");
        }

        // Determine least recently used session, and mark it for invalidation
        SessionInformation leastRecentlyUsed = null;

        for (SessionInformation session : sessions) {
            if ((leastRecentlyUsed == null)
                    || session.getLastRequest()
                    .before(leastRecentlyUsed.getLastRequest())) {
                leastRecentlyUsed = session;
            }
        }

        if (leastRecentlyUsed != null) {
            leastRecentlyUsed.expireNow();
        }

    }

    /**
     * Sets the <tt>exceptionIfMaximumExceeded</tt> property, which determines whether the
     * user should be prevented from opening more sessions than allowed. If set to
     * <tt>true</tt>, a <tt>SessionAuthenticationException</tt> will be raised which means
     * the user authenticating will be prevented from authenticating. if set to
     * <tt>false</tt>, the user that has already authenticated will be forcibly logged
     * out.
     *
     * @param exceptionIfMaximumExceeded defaults to <tt>false</tt>.
     */
    public void setExceptionIfMaximumExceeded(boolean exceptionIfMaximumExceeded) {
        this.exceptionIfMaximumExceeded = exceptionIfMaximumExceeded;
    }

    /**
     * Sets the <tt>maxSessions</tt> property. The default value is 1. Use -1 for
     * unlimited sessions.
     *
     * @param maximumSessions the maximimum number of permitted sessions a user can have
     * open simultaneously.
     */
    public void setMaximumSessions(int maximumSessions) {
        Assert.isTrue(
                maximumSessions != 0,
                "MaximumLogins must be either -1 to allow unlimited logins, or a positive integer to specify a maximum");
        this.maximumSessions = maximumSessions;
    }

    /**
     * Sets the {@link MessageSource} used for reporting errors back to the user when the
     * user has exceeded the maximum number of authentications.
     */
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
