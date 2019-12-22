package cn.edu.njupt.configure;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 加载dll或者so后缀文件的listener
 */
public class InitOpencv  implements ServletContextListener {


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.load(SystemVariables.loadOpencvSystemFile());
    }



}
