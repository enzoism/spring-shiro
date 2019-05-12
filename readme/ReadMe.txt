ShiroSpring的配置使用

1.第一步：导入相关的Jar包（Spring+Shiro）
2.第二步：配置Spring和SpringMVC
3.第三步：配置web.xml和Spring的配置文件


注意事项：
1.applicationContext.xml是Spring的配置文件
2.spring-servlet.xml是SpringMVC的配置文件
3.在web.xml文件中配置的东西是软件整体启用的大框架前提：一般是过滤器和监听器
4.Shiro要在Spring的配置文件中配置，即applicationContext.xml中
5.如果我们在配置文件的时候，总莫名其妙的报错（复制其他项目中的配置），就创建一个bean,找到SpringConfig的Bean，创建，然后复制代码进去


-----------------------------配置Spring------------------------------------
1.使用Spring IDE创建一个applicationContext.xml文件
2.生成web.xml文件
3.将Spring的配置文件部署就好了
	3.1 web.xml的文件配置
	<!-- 配置Spring -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:applicationContext.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
	3.2 application.xml什么都不需要配置
4.完成这一切只需要Spring的Jar包就够了

-----------------------------配置SpringMVC------------------------------------
1.web.xml的文件配置SpringMVC
	<!-- 配置SpringMVC -->
	<servlet>
		<servlet-name>spring</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>spring</servlet-name>
		<url-pattern>/</url-pattern>
	</servlet-mapping>
2.单独的在WEB-INF文件夹里面创建一个spring-servlet.xml文件
	<!-- 配置文件扫描包 -->
	<context:component-scan base-package="com.atguigu.shiro"></context:component-scan>
	<!-- 配置ViewResolver -->
	<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/"></property>
		<property name="suffix" value=".jsp"></property>
	</bean>
	<!-- 配置命名空间 -->
	<mvc:annotation-driven></mvc:annotation-driven>
	<mvc:default-servlet-handler/>
3.做到这一步就可以写一个网页测试使用了Index.jsp然后去访问，如果成功就可以使用了

-----------------------------配置Shiro------------------------------------
1.导入相关的4个Jar包
2.在web.xml中配置ShiroFilter
	<!-- 配置ShiroFilter -->
	<!-- 1. 配置 Shiro 的 shiroFilter. 2. DelegatingFilterProxy 实际上是 Filter 的一个代理对象. 
		默认情况下, Spring 会到 IOC 容器中查找和 <filter-name> 对应的 filter bean. 也可以通过 targetBeanName 
		的初始化参数来配置 filter bean 的 id. -->
	<filter>
		<filter-name>shiroFilter</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<init-param>
			<param-name>targetFilterLifecycle</param-name>
			<param-value>true</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>shiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
3.在applicationContext.xml（Spring）中配置相关的参数
	3.1 securityManager
	3.2 cacheManager:需要加入 ehcache 的 jar 包及配置文件.
	3.3 jdbcRealm:先用自己的实现类
	3.4 lifecycleBeanPostProcessor：可以自定的来调用配置在 Spring IOC 容器中 shiro bean 的生命周期方法. 
	3.5 启用 IOC 容器中使用 shiro 的注解：但必须在配置了 LifecycleBeanPostProcessor 之后才可以使用. 
	3.6 配置 ShiroFilter：id 必须和 web.xml 文件中配置的 DelegatingFilterProxy 的 <filter-name> 一致.
		里面放置哪些页面受保护，以及访问这些页面需要的权限
	3.7 配置一个 bean, 该 bean 实际上是一个 Map. 通过实例工厂方法的方式 
4.页面的访问权限
	1). anon 可以被匿名访问 
	2). authc 必须认证(即登录)后才可能访问的页面. 
	3). logout 登出. 
	4). roles 角色过滤器 -->
	<property name="filterChainDefinitions"> 
		<value> 
			/login.jsp = anon 
			/shiro/login = anon 
			/shiro/logout = logout 
			/user.jsp = roles[user] 
			/admin.jsp= roles[admin] 
			# everything else requires authentication: 
			/** = authc 
		</value> 
	</property>
5.按照上面的	<property name="filterChainDefinitions"> 配置以后，页面的访问权限就开始生效了
	5.1 只能进行页面没有授权的页面
	5.2 如果访问其他页面，就会跳转到认证页面，让用户进行登陆
		因为：/** = authc 里面统配的是多有页面，只要没有/login.jsp = anon的都被拦截了
		我们可以修改成指定页面：/** = anon  和 	/list.jsp = authc
		这样就只拦截/list.jsp，其余的页面都可以正常的访问

	
4.在applicationContext.xml（Spring）中配置相关的参数
	3.1 securityManager
	3.2 cacheManager
	3.3 authenticator
	3.4 jdbcRealm
	3.5 secondRealm
	3.6 lifecycleBeanPostProcessor
	3.7 启用 IOC 容器中使用 shiro 的注解
	3.8 配置 ShiroFilter
	3.9 配置一个 bean, 该 bean 实际上是一个 Map. 通过实例工厂方法的方式 


-----------------------------开始Shiro的认证思路------------------------------------
1.获取当前的Subject,调用SecurityUtils.getSubject();
2.测试当前的用户是否已经被认证，即是否已经登录，调用Subject的isAutherticated();
3.如没有被认证，则把用户名和密码封装成UsernameAndPasswordToken对象
	3.1 创建一个表单页面
	3.2 把请求提交到SpringMVC的Handler
	3.3 获取用户名和密码
4.执行登陆：调用Subject的login(AuthenticaionToken)方法
5.自定义Realm的方法，从数据库中获取对应的记录，返回给Shiro
	5.1 实际上需要继承org.apache.shiro.realm.AuthenticatingRealm类
	5.2 实现doGetAuthenticationInfo(AuthenticaionToken)方法
	5.3 本质是将封装的Token传入ShiroRealm的参数token中了
	public class ShiroRealm extends AuthenticatingRealm {
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		System.out.println("[FirstRealm] doGetAuthenticationInfo");
		return null;
	}}
6.由Shiro完成密码的比对
7.这里面没有设计授权的操作
-----------------------------开始Shiro的认证代码------------------------------------
1.创建Form表单提交到Handler(Controller)
2.里面完成数据的接受验证，将数据封装成UsernameAndPasswordToken
	@RequestMapping("/login")
	public String login(@RequestParam("username") String username, 
			@RequestParam("password") String password){
		Subject currentUser = SecurityUtils.getSubject();
		System.out.println("--------------");
		if (!currentUser.isAuthenticated()) {
        	// 把用户名和密码封装为 UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // Remember Me
            token.setRememberMe(true);
            try {
            	System.out.println("1. " + token.hashCode());
            	// 执行登录. 
                currentUser.login(token);
            } 
            // 所有认证时异常的父类. 
            catch (AuthenticationException ae) {
            	System.out.println("登录失败: " + ae.getMessage());
            }
        }
		return "redirect:/list.jsp";
	}
3.在ShiroRealm中对请求的内容进行处理
	3.1 把 AuthenticationToken 转换为 UsernamePasswordToken 
	3.2 从 UsernamePasswordToken 中来获取 username
	3.3 调用数据库的方法, 从数据库中查询 username 对应的用户记录
	3.4 若用户不存在, 则可以抛出 UnknownAccountException 异常
	3.5 根据用户信息的情况, 决定是否需要抛出其他的 AuthenticationException 异常. 
	3.6 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo 
4.如果按照上面的步骤登陆正确之后，再次点击登陆，即使密码错误也不会出现任何的错误，因为有缓存
	我们要搞一个登出操作
-----------------------------开始Shiro的密码比对------------------------------------
1.SimpleAuthenticationInfo
	SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, realmName);
	principal：认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象. 
	credentials：密码. 
	realmName：当前 realm 对象的 name. 调用父类的 getName() 方法即可
2.密码的比对使用	AuthenticationInfo的属性CredentialsMatcher来进行
3.在applicationContext.xml里面配置我们的“加密方式”和“加密次数”（CredentialsMatcher的属性）
	public static void main(String[] args) {
		String hashAlgorithmName = "MD5";
		Object credentials = "123456";
		Object salt = null;//没有加盐
		int hashIterations = 1024;
		Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
		System.out.println(result);
	}
4.为了让不同的用户，即使相同的密码进行加密的时候也可以不同，加盐
	1. 加密之后的结果应该把salt加进去
	2. 返回值里面应该将salt带上
	
