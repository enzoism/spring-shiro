package com.atguigu.shiro.helloworld;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.使用
 *
 */
public class HelloShiro {
	private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);

	public static void main(String[] args) {

		// 1.获取当前的 Subject. 调用 SecurityUtils.getSubject();
		Subject currentUser = SecurityUtils.getSubject();
		// 2.测试使用 Session
		Session session = currentUser.getSession();
		session.setAttribute("someKey", "aValue");
		String value = (String) session.getAttribute("someKey");
		if (value.equals("aValue")) {
			log.info("---> Retrieved the correct value! [" + value + "]");
		}
		// 3.测试当前的用户是否已经被认证. 即是否已经登录.
		// 调动 Subject 的 isAuthenticated()
		if (!currentUser.isAuthenticated()) {
			// 把用户名和密码封装为 UsernamePasswordToken 对象
			UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
			// rememberme
			token.setRememberMe(true);
			try {
				// 执行登录.
				currentUser.login(token);
			}
			// 若没有指定的账户, 则 shiro 将会抛出 UnknownAccountException 异常.
			catch (UnknownAccountException uae) {
				log.info("----> There is no user with username of " + token.getPrincipal());
				return;
			}
			// 若账户存在, 但密码不匹配, 则 shiro 会抛出 IncorrectCredentialsException 异常。
			catch (IncorrectCredentialsException ice) {
				log.info("----> Password for account " + token.getPrincipal() + " was incorrect!");
				return;
			}
			// 用户被锁定的异常 LockedAccountException
			catch (LockedAccountException lae) {
				log.info("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			}
			// 所有认证时异常的父类.
			catch (AuthenticationException ae) {
				// unexpected condition? error?
			}
		}
		// 4.测试是否有某一个角色. 调用 Subject 的 hasRole 方法.
		if (currentUser.hasRole("schwartz")) {
			log.info("----> May the Schwartz be with you!");
		} else {
			log.info("----> Hello, mere mortal.");
			return;
		}
		// 5.测试用户是否具备某一个行为. 调用 Subject 的 isPermitted() 方法。
		if (currentUser.isPermitted("lightsaber:weild")) {
			log.info("----> You may use a lightsaber ring.  Use it wisely.");
		} else {
			log.info("Sorry, lightsaber rings are for schwartz masters only.");
		}
		// 6.测试用户是否具备某一个行为.
		if (currentUser.isPermitted("user:delete:zhangsan")) {
			log.info("----> You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  "
					+ "Here are the keys - have fun!");
		} else {
			log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
		}
		// 7.执行登出. 调用 Subject 的 Logout() 方法.
		System.out.println("---->" + currentUser.isAuthenticated());

		currentUser.logout();

		System.out.println("---->" + currentUser.isAuthenticated());

		System.exit(0);

	}

}
