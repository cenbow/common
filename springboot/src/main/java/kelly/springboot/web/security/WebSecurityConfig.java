package kelly.springboot.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
//开启Spring Security的功能
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //authorizeRequests哪些URL需要被保护、哪些不需要被保护
                .authorizeRequests()
                // /和/user不需要任何认证就可以访问
                .antMatchers("/", "/user", "/v").permitAll()
                //其他的路径都必须通过身份验证。
                .anyRequest().authenticated()
                .and()
                //当需要用户登录时候，转到的登录页面
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //在内存中创建了一个用户，该用户的名称为user，密码为password，用户角色为USER。
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}