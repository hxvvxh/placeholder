package com.hp.placeholder.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 监听器-观察者模式的实现
 * ApplicationListener 属于spring-context的内容  注意区分SpringApplicationRunListener(springboot 的内容)
 * ApplicationListener的作用是监听spring在运行时的各个情况 -- 通过ApplicationEvent来选择
 * 在spring的各个时期 通过调用ApplicationListener#onApplicationEvent方法来实现
 * 其中ApplicationListener<T> T就是用来筛选对应的监听对象
 *
 *
 * springboot 在启动的时候存在四个事件
 * ApplicationEvent：                  source：
 *
 * ContextRefreshedEvent               AnnotationConfigServletWebServerApplicationContext
 *
 * ServletWebServerInitializedEvent（springboot的内容）    TomcatWebServer
 *
 * ApplicationStartedEvent（springboot的内容）            SpringApplication
 * ApplicationReadyEvent （springboot的内容）                SpringApplication
 *
 *
 * spring的事件监听内容：
 * ContextRefreshedEvent ApplicationContext 被初始化或刷新时，该事件被发布。这也可以在 ConfigurableApplicationContext接口中使用 refresh() 方法来发生。此处的初始化是指：所有的Bean被成功装载，后处理Bean被检测并激活，所有Singleton Bean 被预实例化，ApplicationContext容器已就绪可用
 * ContextStartedEvent 当使用 ConfigurableApplicationContext （ApplicationContext子接口）接口中的 start() 方法启动 ApplicationContext 时，该事件被发布。你可以调查你的数据库，或者你可以在接受到这个事件后重启任何停止的应用程序
 * ContextStoppedEvent 当使用 ConfigurableApplicationContext 接口中的 stop() 停止 ApplicationContext 时，发布这个事件。你可以在接受到这个事件后做必要的清理的工作。
 * ContextClosedEvent 当使用 ConfigurableApplicationContext 接口中的 close() 方法关闭 ApplicationContext 时，该事件被发布。一个已关闭的上下文到达生命周期末端；它不能被刷新或重启
 * RequestHandledEvent 这是一个 web-specific 事件，告诉所有 bean HTTP 请求已经被服务。只能应用于使用DispatcherServlet的Web应用。在使用Spring作为前端的MVC控制器时，当Spring处理用户请求结束后，系统会自动触发该事件
 *
 *
 */
@Component
@Slf4j
public class HpApplicationListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("收到事件{}",event.toString());
    }
}
