package com.xunuo.session;

import com.xunuo.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String SHIRO_SESSION_PREFIX = "xunuo-session";

    private byte[] getKey(String key) {
        return (SHIRO_SESSION_PREFIX + key).getBytes();
    }

    private void saveSession(Session seesion) {
        if (seesion != null && seesion.getId() != null) {
            byte[] key = getKey(seesion.getId().toString());
            byte[] value = SerializationUtils.serialize(seesion);
            jedisUtil.set(key, value);
            jedisUtil.expire(key, 600);

        }
    }
    /**
     * Subclass hook to actually persist the given <tt>Session</tt> instance to the underlying EIS.
     *
     * @param session the Session instance to persist to the EIS.
     * @return the id of the session created in the EIS (i.e. this is almost always a primary key and should be the
     * value returned from {@link Session#getId() Session.getId()}.
     */
    @Override
    protected Serializable doCreate(Session session) {
        System.out.println("doCreate session!!!!!!!!");
        Serializable sessionId = generateSessionId(session);
        // 绑定session和sessionId
        assignSessionId(session,sessionId);
        byte[] key = getKey(session.getId().toString());
        byte[] value = SerializationUtils.serialize(session);
        jedisUtil.set(key, value);
        jedisUtil.expire(key, 600);

        return sessionId;
    }

    /**
     * Subclass implementation hook that retrieves the Session object from the underlying EIS or {@code null} if a
     * session with that ID could not be found.
     *
     * @param sessionId the id of the <tt>Session</tt> to retrieve.
     * @return the Session in the EIS identified by <tt>sessionId</tt> or {@code null} if a
     * session with that ID could not be found.
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        System.out.println("read session!!!!!!!!");
        if (sessionId == null) {
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        // 反序列化成session对象
        return (Session) SerializationUtils.deserialize(value);
    }

    /**
     * Updates (persists) data from a previously created Session instance in the EIS identified by
     * {@code {@link Session#getId() session.getId()}}.  This effectively propagates
     * the data in the argument to the EIS record previously saved.
     * <p/>
     * In addition to UnknownSessionException, implementations are free to throw any other
     * exceptions that might occur due to integrity violation constraints or other EIS related
     * errors.
     *
     * @param session the Session to update
     * @throws UnknownSessionException if no existing EIS session record exists with the
     *                                 identifier of {@link Session#getId() session.getSessionId()}
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        System.out.println("update session!!!!!!!!");
        this.saveSession(session);
    }

    /**
     * Deletes the associated EIS record of the specified {@code session}.  If there never
     * existed a session EIS record with the identifier of
     * {@link Session#getId() session.getId()}, then this method does nothing.
     *
     * @param session the session to delete.
     */
    @Override
    public void delete(Session session) {
        System.out.println("delete session!!!!!!!!");
        if (session == null && session.getId() == null) {
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.del(key);

    }

    /**
     * Returns all sessions in the EIS that are considered active, meaning all sessions that
     * haven't been stopped/expired.  This is primarily used to validate potential orphans.
     * <p/>
     * If there are no active sessions in the EIS, this method may return an empty collection or {@code null}.
     * <h4>Performance</h4>
     * This method should be as efficient as possible, especially in larger systems where there might be
     * thousands of active sessions.  Large scale/high performance
     * implementations will often return a subset of the total active sessions and perform validation a little more
     * frequently, rather than return a massive set and validate infrequently.  If efficient and possible, it would
     * make sense to return the oldest unstopped sessions available, ordered by
     * {@link Session#getLastAccessTime() lastAccessTime}.
     * <h4>Smart Results</h4>
     * <em>Ideally</em> this method would only return active sessions that the EIS was certain should be invalided.
     * Typically that is any session that is not stopped and where its lastAccessTimestamp is older than the session
     * timeout.
     * <p/>
     * For example, if sessions were backed by a relational database or SQL-92 'query-able' enterprise cache, you might
     * return something similar to the results returned by this query (assuming
     * {@link SimpleSession SimpleSession}s were being stored):
     * <pre>
     * select * from sessions s where s.lastAccessTimestamp < ? and s.stopTimestamp is null
     * </pre>
     * where the {@code ?} parameter is a date instance equal to 'now' minus the session timeout
     * (e.g. now - 30 minutes).
     *
     * @return a Collection of {@code Session}s that are considered active, or an
     * empty collection or {@code null} if there are no active sessions.
     */
    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.getAllKeys(this.SHIRO_SESSION_PREFIX);
        Set<Session> sessions = new HashSet<>();
        if (CollectionUtils.isEmpty(sessions)) {
            return null;
        }
        for (byte[] key : keys) {
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
