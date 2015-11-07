package cn.elvea.service;

import cn.elvea.core.service.EntityService;
import cn.elvea.domain.UserSession;
import cn.elvea.repository.EntityRepository;
import cn.elvea.repository.UserSessionRepository;
import cn.elvea.security.CaptchaFormAuthenticationFilter;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

@Service
@Transactional
public class UserSessionService extends EntityService<UserSession> {
    @Autowired
    UserSessionRepository userSessionRepository;

    @Override
    public EntityRepository<UserSession> getRepository() {
        return userSessionRepository;
    }

    public Session findBySessionId(Serializable sessionId) {
        UserSession userSession = userSessionRepository.findBySessionId(sessionId);
        return coverUserSessionToSession(userSession);
    }

    public void createUserSession(Session session) {
        UserSession userSession = coverSessionToUserSession(session);
        if (userSession != null) {
            if (session.getAttribute(CaptchaFormAuthenticationFilter.Shiro_Session_Username_Key) != null) {
                String username = (String) session.getAttribute(CaptchaFormAuthenticationFilter.Shiro_Session_Username_Key);
                userSession.setUsername(username);
            }
            userSessionRepository.insert(userSession);
        }
    }

    public void deleteUserSession(Session session) {
        UserSession userSession = userSessionRepository.findBySessionId(String.valueOf(session.getId()));
        if (userSession != null) {
            if (session.getAttribute(CaptchaFormAuthenticationFilter.Shiro_Session_Username_Key) != null) {
                String username = (String) session.getAttribute(CaptchaFormAuthenticationFilter.Shiro_Session_Username_Key);
                userSession.setUsername(username);
            }
            userSession.setEndTime(new Date());
            userSessionRepository.update(userSession);
        }
    }

    public void updateUserSession(Session session) {
        UserSession userSession = userSessionRepository.findBySessionId(String.valueOf(session.getId()));
        if (userSession != null) {
            if (session.getAttribute(CaptchaFormAuthenticationFilter.Shiro_Session_Username_Key) != null) {
                String username = (String) session.getAttribute(CaptchaFormAuthenticationFilter.Shiro_Session_Username_Key);
                userSession.setUsername(username);
            }
            userSession.setLastAccessTime(userSessionRepository.getDate());
            userSessionRepository.update(userSession);
        }
    }

    private UserSession coverSessionToUserSession(Session session) {
        UserSession userSession = new UserSession();
        userSession.setSessionId(String.valueOf(session.getId()));
        userSession.setLastAccessTime(session.getLastAccessTime());
        userSession.setHost(session.getHost());
        userSession.setStartTime(session.getStartTimestamp());
        return userSession;
    }

    private Session coverUserSessionToSession(UserSession userSession) {
        SimpleSession session = new SimpleSession();
        session.setId(userSession.getId());
        session.setLastAccessTime(userSession.getLastAccessTime());
        session.setHost(userSession.getHost());
        session.setStartTimestamp(userSession.getStartTime());
        return session;
    }
}
