package xunuo.test;

import com.xunuo.shiro.realm.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class CustomRealmTest {
    @Test
    public void testAuthentication() {
        CustomRealm customRealm = new CustomRealm();
        // 构建SecurityManager
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);
        // 主体提交请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        // 加密算法
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        // 加密算法
        matcher.setHashAlgorithmName("md5");
        // 加密次数
        matcher.setHashIterations(1);
        customRealm.setCredentialsMatcher(matcher);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan","123456");
        subject.login(token);
        System.out.println("isAuthenticated :" + subject.isAuthenticated());
        subject.checkRoles("admin");
        subject.checkPermissions("user:delete","user:update");

    }
}
