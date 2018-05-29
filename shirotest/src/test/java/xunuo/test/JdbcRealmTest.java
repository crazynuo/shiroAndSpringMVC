package xunuo.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class JdbcRealmTest {
    DruidDataSource dataSource = new DruidDataSource();
    {
        dataSource.setUrl("jdbc:mysql://localhost:3306/mytest?sessionVariables=sql_mode=''&jdbcCompliantTruncation=false&useUnicode=true&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

    }
    @Test
    public void testAuthentication() {

        // 有默认的执行sql
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        String sql = "select user_password from test_user where user_name=?";
        jdbcRealm.setAuthenticationQuery(sql);
        String roleSql = "select role_name from test_user_role where user_name=?";
        jdbcRealm.setUserRolesQuery(roleSql);
        //权限开关,默认false;
        jdbcRealm.setPermissionsLookupEnabled(true);
        // 构建SecurityManager
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);
        // 主体提交请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan","123456");
        subject.login(token);
        System.out.println("isAuthenticated :" + subject.isAuthenticated());
        subject.checkRole("admin");

    }
}
