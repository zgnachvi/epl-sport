package com.goodbarber.premierleaguene.listeners;

import com.goodbarber.premierleaguene.parser.NewsParser;
import com.goodbarber.premierleaguene.repository.ConnectionManager;
import com.goodbarber.premierleaguene.utils.ProjectConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class AppListener implements ServletContextListener{

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ProjectConfig.load();
        ConnectionManager.init();

        executorService.scheduleWithFixedDelay(new NewsParser(null), 1, 20, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        executorService.shutdown();
        ConnectionManager.close();
    }

    public static void submit(Runnable runnable){
        executorService.submit(runnable);
    }
}
