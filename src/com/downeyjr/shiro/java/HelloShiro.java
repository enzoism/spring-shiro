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
 * 1.ʹ��
 *
 */
public class HelloShiro {
	private static final transient Logger log = LoggerFactory.getLogger(Quickstart.class);

	public static void main(String[] args) {

		// 1.��ȡ��ǰ�� Subject. ���� SecurityUtils.getSubject();
		Subject currentUser = SecurityUtils.getSubject();
		// 2.����ʹ�� Session
		Session session = currentUser.getSession();
		session.setAttribute("someKey", "aValue");
		String value = (String) session.getAttribute("someKey");
		if (value.equals("aValue")) {
			log.info("---> Retrieved the correct value! [" + value + "]");
		}
		// 3.���Ե�ǰ���û��Ƿ��Ѿ�����֤. ���Ƿ��Ѿ���¼.
		// ���� Subject �� isAuthenticated()
		if (!currentUser.isAuthenticated()) {
			// ���û����������װΪ UsernamePasswordToken ����
			UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
			// rememberme
			token.setRememberMe(true);
			try {
				// ִ�е�¼.
				currentUser.login(token);
			}
			// ��û��ָ�����˻�, �� shiro �����׳� UnknownAccountException �쳣.
			catch (UnknownAccountException uae) {
				log.info("----> There is no user with username of " + token.getPrincipal());
				return;
			}
			// ���˻�����, �����벻ƥ��, �� shiro ���׳� IncorrectCredentialsException �쳣��
			catch (IncorrectCredentialsException ice) {
				log.info("----> Password for account " + token.getPrincipal() + " was incorrect!");
				return;
			}
			// �û����������쳣 LockedAccountException
			catch (LockedAccountException lae) {
				log.info("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			}
			// ������֤ʱ�쳣�ĸ���.
			catch (AuthenticationException ae) {
				// unexpected condition? error?
			}
		}
		// 4.�����Ƿ���ĳһ����ɫ. ���� Subject �� hasRole ����.
		if (currentUser.hasRole("schwartz")) {
			log.info("----> May the Schwartz be with you!");
		} else {
			log.info("----> Hello, mere mortal.");
			return;
		}
		// 5.�����û��Ƿ�߱�ĳһ����Ϊ. ���� Subject �� isPermitted() ������
		if (currentUser.isPermitted("lightsaber:weild")) {
			log.info("----> You may use a lightsaber ring.  Use it wisely.");
		} else {
			log.info("Sorry, lightsaber rings are for schwartz masters only.");
		}
		// 6.�����û��Ƿ�߱�ĳһ����Ϊ.
		if (currentUser.isPermitted("user:delete:zhangsan")) {
			log.info("----> You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  "
					+ "Here are the keys - have fun!");
		} else {
			log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
		}
		// 7.ִ�еǳ�. ���� Subject �� Logout() ����.
		System.out.println("---->" + currentUser.isAuthenticated());

		currentUser.logout();

		System.out.println("---->" + currentUser.isAuthenticated());

		System.exit(0);

	}

}
