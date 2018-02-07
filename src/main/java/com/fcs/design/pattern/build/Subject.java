package com.fcs.design.pattern.build;

import com.fcs.design.pattern.context.SubjectContext;

import java.io.Serializable;

/**
 * Created by fengcs on 2018/2/7.
 * copy from shiro subject.
 * 相关类太多 所以注释了 凑合着看吧(接口里弄build我还是第一次见)
 */
public interface Subject {

    Object getPrincipal();

    boolean isPermitted(String permission);

    //......

    public static class Builder {

//        private final SubjectContext subjectContext;
//
//        private final SecurityManager securityManager;

//        public Builder() {
//            this(SecurityUtils.getSecurityManager());
//        }
//
//        public Builder(SecurityManager securityManager) {
//            if (securityManager == null) {
//                throw new NullPointerException("SecurityManager method argument cannot be null.");
//            }
//            this.securityManager = securityManager;
//            this.subjectContext = newSubjectContextInstance();
//            if (this.subjectContext == null) {
//                throw new IllegalStateException("Subject instance returned from 'newSubjectContextInstance' " +
//                        "cannot be null.");
//            }
//            this.subjectContext.setSecurityManager(securityManager);
//        }


//        protected SubjectContext newSubjectContextInstance() {
//            return new DefaultSubjectContext();
//        }

//        protected SubjectContext getSubjectContext() {
//            return this.subjectContext;
//        }

//        public Builder sessionId(Serializable sessionId) {
//            if (sessionId != null) {
//                this.subjectContext.setSessionId(sessionId);
//            }
//            return this;
//        }

//        public Builder host(String host) {
//            if (StringUtils.hasText(host)) {
//                this.subjectContext.setHost(host);
//            }
//            return this;
//        }
//
//        public Builder session(Session session) {
//            if (session != null) {
//                this.subjectContext.setSession(session);
//            }
//            return this;
//        }

//        public Builder principals(PrincipalCollection principals) {
//            if (!CollectionUtils.isEmpty(principals)) {
//                this.subjectContext.setPrincipals(principals);
//            }
//            return this;
//        }
//
//        public Builder sessionCreationEnabled(boolean enabled) {
//            this.subjectContext.setSessionCreationEnabled(enabled);
//            return this;
//        }
//
//        public Builder authenticated(boolean authenticated) {
//            this.subjectContext.setAuthenticated(authenticated);
//            return this;
//        }

//        public Builder contextAttribute(String attributeKey, Object attributeValue) {
//            if (attributeKey == null) {
//                String msg = "Subject context map key cannot be null.";
//                throw new IllegalArgumentException(msg);
//            }
//            if (attributeValue == null) {
//                this.subjectContext.remove(attributeKey);
//            } else {
//                this.subjectContext.put(attributeKey, attributeValue);
//            }
//            return this;
//        }

//        public Subject buildSubject() {
//            return this.securityManager.createSubject(this.subjectContext);
//        }
    }

}
