package com.zrk.demo.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zrk.oauthclient.shiro.support.UsernamePasswordAndClientToken;

@Controller
@RequestMapping("/")
public class LoginController {
	@RequestMapping(value = "/login",method = RequestMethod.GET)
	public String loginGet(Model model) {
		return "login";
	}

	@RequestMapping(value="/login",method = RequestMethod.POST)
	public String login(HttpServletRequest request,Model model,@RequestParam(required=true,value="username")String username,@RequestParam(required=true,value="pwd")String pwd) {
		Subject subject = SecurityUtils.getSubject();
		//使用自定义Token
		UsernamePasswordAndClientToken token = new UsernamePasswordAndClientToken(username, pwd);
//		UsernamePasswordToken token = new UsernamePasswordToken(username,pwd);
		token.setRememberMe(false);
		try {
			subject.login(token);
			if (subject.isAuthenticated()){
				SavedRequest savedRequest = WebUtils.getSavedRequest(request);
				String url = savedRequest!=null?savedRequest.getRequestUrl():null;
				if(url!=null)
					return "redirect:"+url;
				return "redirect:/";
			}
		} catch (UnknownAccountException e) {// 此用户不存在，请注册后再登录
			token.clear();
			model.addAttribute("result", "此用户不存在，请注册后再登录");
		} catch (AuthenticationException e) {// http://jinnianshilongnian.iteye.com/blog/2019547  密码错误
			token.clear();
			model.addAttribute("result", "密码错误");
		}
		return "login";
	}

	@RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
	public String unauthorized() {
		return "unauthorized";
	}
	
}
