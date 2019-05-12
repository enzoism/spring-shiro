package com.downeyjr.shiro.realms;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

public class ShiroRealm extends AuthenticatingRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		System.out.println("[FirstRealm] doGetAuthenticationInfo");

		//1. 把 AuthenticationToken 转换为 UsernamePasswordToken 
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		
		//2. 从 UsernamePasswordToken 中来获取 username
		String username = upToken.getUsername();
		
		//3. 调用数据库的方法, 从数据库中查询 username 对应的用户记录
		System.out.println("从数据库中获取 username: " + username + " 所对应的用户信息.");
		
		//4. 若用户不存在, 则可以抛出 UnknownAccountException 异常
		if("unknown".equals(username)){
			throw new UnknownAccountException("用户不存在!");
		}
		
		//5. 根据用户信息的情况, 决定是否需要抛出其他的 AuthenticationException 异常. 
		if("monster".equals(username)){
			throw new LockedAccountException("用户被锁定");
		}
		
		//6. 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo
		//	以下信息是从数据库中获取的.
		//1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象. 
		//2). credentials: 密码. 
		//3). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
		
		
		//6.1 没有加密
//		Object principal = username;
//		Object credentials = "123456";//没有MD5加密  
//		String realmName = getName();
//		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, realmName);
		
		//6.2 MD5加密
//		Object principal = username;
//		Object credentials = "fc1709d0a95a6be30bc5926fdb7f22f4";//MD5加密  ：fc1709d0a95a6be30bc5926fdb7f22f4
//		String realmName = getName();
//		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, realmName);	
		
		//6.3 加盐
		Object principal = username;
		Object credentials = null; 
		if("admin".equals(username)){
			credentials = "038bdaf98f2037b31f1e75b5b4c9b26e";
		}else if("user".equals(username)){
			credentials = "098d2c478e9c11555ce2823231e02ec1";
		}
		String realmName = getName();
		ByteSource credentialsSalt = ByteSource.Util.bytes(username);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		

		return info;
	}
	//让我们自己知道结果是什么,现在是模拟，正常情况是直接存放数据库
	public static void main(String[] args) {
		//1.
		String hashAlgorithmName = "MD5";
		Object credentials = "123456";
		int hashIterations = 1024;
		//没有加盐
		//Object salt = null;
		//加盐
		Object salt = ByteSource.Util.bytes("user");
		//2.
		Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
		System.out.println(result);
	}
}

