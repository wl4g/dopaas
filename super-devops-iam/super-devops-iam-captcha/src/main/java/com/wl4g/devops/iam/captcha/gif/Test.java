package com.wl4g.devops.iam.captcha.gif;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		Captcha captcha = new GifCaptcha("abcde");// gif格式动画验证码
		captcha.out(new FileOutputStream("E:/1.gif"));
	}

}
