package com.downeyjr.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/shiro")
public class ShiroController {
	@RequestMapping("/login")
	public String login(String username, String password){
		Subject currentUser = SecurityUtils.getSubject();
		System.out.println("--------------");
		if (!currentUser.isAuthenticated()) {
        	// 1.把用户名和密码封装为 UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // 2.模仿记住我的选项
            token.setRememberMe(true);
            try {
            	System.out.println("1. " + token.hashCode());
                currentUser.login(token);
            	System.out.println("登录成功");
            } 
            // 3.所有认证时异常的父类. 
            catch (AuthenticationException ae) {
            	System.out.println("登录失败: " + ae.getMessage());
            }
        }
		return "redirect:/list.jsp";
	}
	
}
