package xunuo.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

public class AuthenticationTest {
    SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
    @Before
    public void addUser(){
        simpleAccountRealm.addAccount("Mark","123456","admin","user");
    }
    @Test
    public void testAuthentication() {
        // 构建SecurityManager
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        // 主体提交请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        defaultSecurityManager.setRealm(simpleAccountRealm);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("Mark","123456");
        subject.login(token);
        System.out.println("isAuthenticated :" + subject.isAuthenticated());
        subject.checkRoles("admin","user");
        subject.logout();
        System.out.println("isAuthenticated :" + subject.isAuthenticated());

    }
}
