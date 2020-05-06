package com.zjc.security.web.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HttpSessionMutexListener;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @ClassName MySessionListener
 * @Description
 * @Author ZJC
 * @Date 2020/4/30 16:45
 */
//@Configuration
//public class MySessionListener implements HttpSessionListener {
//
//    @Autowired
//    SessionRegistry sessionRegistry;
//
//    @Override
//    public void sessionCreated(HttpSessionEvent event) {
////        event.getSession()
//    }
//
//    @Override
//    public void sessionDestroyed(HttpSessionEvent event) {
//        sessionRegistry.removeSessionInformation(event.getSession().getId());
//    }
//
//}
