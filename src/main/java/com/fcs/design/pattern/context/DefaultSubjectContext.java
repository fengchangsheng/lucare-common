package com.fcs.design.pattern.context;

import com.fcs.design.pattern.build.Subject;

import java.io.Serializable;

/**
 * Created by fengcs on 2018/2/7.
 */
public class DefaultSubjectContext extends MapContext implements SubjectContext{

    private static final String SESSION_ID = DefaultSubjectContext.class.getName() + ".SESSION_ID";

    private static final String AUTHENTICATION_TOKEN = DefaultSubjectContext.class.getName() + ".AUTHENTICATION_TOKEN";

    private static final String AUTHENTICATION_INFO = DefaultSubjectContext.class.getName() + ".AUTHENTICATION_INFO";

    private static final String SUBJECT = DefaultSubjectContext.class.getName() + ".SUBJECT";

    private static final String PRINCIPALS = DefaultSubjectContext.class.getName() + ".PRINCIPALS";

    private static final String SESSION = DefaultSubjectContext.class.getName() + ".SESSION";

    private static final String AUTHENTICATED = DefaultSubjectContext.class.getName() + ".AUTHENTICATED";

    private static final String HOST = DefaultSubjectContext.class.getName() + ".HOST";

    public static final String SESSION_CREATION_ENABLED = DefaultSubjectContext.class.getName() + ".SESSION_CREATION_ENABLED";

    public Serializable getSessionId() {
        return getTypedValue(SESSION_ID, Serializable.class);
    }

    public void setSessionId(Serializable sessionId) {
        nullSafePut(SESSION_ID, sessionId);
    }

    public Subject getSubject() {
        return getTypedValue(SUBJECT, Subject.class);
    }

    public void setSubject(Subject subject) {
        nullSafePut(SUBJECT, subject);
    }
}
