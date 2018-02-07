package com.fcs.design.pattern.context;

import com.fcs.design.pattern.build.Subject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by fengcs on 2018/2/7.
 */
public interface SubjectContext extends Map<String, Object>{

    Serializable getSessionId();

    void setSessionId(Serializable sessionId);

    Subject getSubject();

    public void setSubject(Subject subject);
}
