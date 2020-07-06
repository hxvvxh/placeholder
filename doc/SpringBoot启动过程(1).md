## SpringBoot之ApplicationContextInitializer

### 一、 ApplicationContextInitializer 介绍

 	用于在spring容器刷新之前初始化Spring ConfigurableApplicationContext的回调接口。（剪短说就是在容器刷新之前调用该类的 initialize 方法。并将 ConfigurableApplicationContext 类的实例传递给该方法

 	通常用于需要对应用程序上下文进行编程初始化的web应用程序中。例如，根据上下文环境注册属性源或激活配置文件等

 	可排序的（实现Ordered接口，或者添加@Order注解

### 二、三种实现方式

实现 ApplicationContextInitializer 接口  实现自己的initializer类

（1）在主类的main的方法中添加

```java
application.addInitializers(new MyApplicationContextInitializer());
```

（2）配置文件中配置(yml,properties等)

```java
context.initializer.classes=XXXX
```

(3)META-INF/spring.factories

```java
org.springframework.context.ApplicationContextInitializer=\
XXX
//排序指定 @Order(Ordered.LOWEST_PRECEDENCE) 排序并没有生效。当然采用实现Ordered接口的方式，排序验证结果都是一样的
```

### 三：排序

```java
@Order(Ordered.HIGHEST_PRECEDENCE)//最高的执行级别   越高越早执行
```



## SpringBoot启动过程

### 一： 启动过程

```java
/**
 *applicationContext
 * {@link ApplicationContext}.
 *
 * @param args applicationContext
 * @return a running {@link ApplicationContext}
 *
 * 运行spring应用，并刷新一个新的 ApplicationContext（Spring的上下文）
 * ConfigurableApplicationContext 是 ApplicationContext 接口的子接口。在 ApplicationContext
 * 基础上增加了配置上下文的工具。 ConfigurableApplicationContext是容器的高级接口
 */
public ConfigurableApplicationContext run(String... args) {
    //记录程序运行时间
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    // ConfigurableApplicationContext Spring 的上下文
    ConfigurableApplicationContext context = null;
    Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    configureHeadlessProperty();
    //从META-INF/spring.factories中获取监听器
    //1、获取并启动监听器
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting();
    try {
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(
                args);
        //2、构造应用上下文环境
        ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
        //处理需要忽略的Bean
        configureIgnoreBeanInfo(environment);
        //打印banner
        Banner printedBanner = printBanner(environment);
        ///3、初始化应用上下文
        context = createApplicationContext();
        //实例化SpringBootExceptionReporter.class，用来支持报告关于启动的错误
        exceptionReporters = getSpringFactoriesInstances(
                SpringBootExceptionReporter.class,
                new Class[]{ConfigurableApplicationContext.class}, context);
        //4、刷新应用上下文前的准备阶段
        prepareContext(context, environment, listeners, applicationArguments, printedBanner);
        //5、刷新应用上下文
        refreshContext(context);
        //刷新应用上下文后的扩展接口
        afterRefresh(context, applicationArguments);
        //时间记录停止
        stopWatch.stop();
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass)
                    .logStarted(getApplicationLog(), stopWatch);
        }
        //发布容器启动完成事件
        listeners.started(context);
        callRunners(context, applicationArguments);
    } catch (Throwable ex) {
        handleRunFailure(context, ex, exceptionReporters, listeners);
        throw new IllegalStateException(ex);
    }

    try {
        listeners.running(context);
    } catch (Throwable ex) {
        handleRunFailure(context, ex, exceptionReporters, null);
        throw new IllegalStateException(ex);
    }
    return context;
}
```

### 二：启动的重要步骤

```text
第一步：获取并启动监听器
第二步：构造应用上下文环境
第三步：初始化应用上下文
第四步：刷新应用上下文前的准备阶段
第五步：刷新应用上下文
第六步：刷新应用上下文后的扩展接口
```

### 三：第一步：获取并启动监听器

```java
getRunListeners(args)
```

事件机制在Spring是很重要的一部分内容，通过事件机制我们可以监听Spring容器中正在发生的一些事件，同样也可以自定义监听事件。Spring的事件为Bean和Bean之间的消息传递提供支持。当一个对象处理完某种任务后，通知另外的对象进行某些处理，常用的场景有进行某些操作后发送通知，消息、邮件等情况。

```java
getSpringFactoriesInstances()
```

主要是获取到META-INF/spring.factories中的指定的key的value，并实例化

主要代码：

```java

springApplication中创建实例的方法：
Class<?> instanceClass = ClassUtils.forName(name, classLoader);
Assert.isAssignable(type, instanceClass);
Constructor<?> constructor = instanceClass.getDeclaredConstructor(parameterTypes);
T instance = (T) BeanUtils.instantiateClass(constructor, args);
//读取 后并按照@Order 进行排序
```

```java
listeners.starting();//启动监听
```

### 四：第二步：构造应用上下文环境

```java
ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
```

应用上下文环境包括:计算机的环境，Java环境，Spring的运行环境，Spring项目的配置（在SpringBoot中就是那个熟悉的application.properties/yml）等等

主要代码：

```java
private ConfigurableEnvironment prepareEnvironment(
        SpringApplicationRunListeners listeners,
        ApplicationArguments applicationArguments) {
    // Create and configure the environment
    //创建并配置相应的环境
    ConfigurableEnvironment environment = getOrCreateEnvironment();
    //根据用户配置，配置 environment系统环境
    configureEnvironment(environment, applicationArguments.getSourceArgs());
    // 启动相应的监听器，其中一个重要的监听器 ConfigFileApplicationListener 就是加载项目配置文件的监听器。
    listeners.environmentPrepared(environment);
    bindToSpringApplication(environment);
    if (this.webApplicationType == WebApplicationType.NONE) {
        environment = new EnvironmentConverter(getClassLoader())
                .convertToStandardEnvironmentIfNecessary(environment);
    }
    ConfigurationPropertySources.attach(environment);
    return environment;
}
```

法中主要完成的工作，首先是创建并按照相应的应用类型配置相应的环境，然后根据用户的配置，配置系统环境，然后启动监听器，并加载系统配置文件。

```java
Class:ConfigFileApplicationListener//这个是加载项目中的配置文件的监听器
```

#### 4.1 getOrCreateEnvironment()

不同的应用类型初始化不同的系统环境实例

#### 4.2 configureEnvironment(environment, applicationArguments.getSourceArgs())



```java
protected void configureEnvironment(ConfigurableEnvironment environment,
                                    String[] args) {
    // 将main 函数的args封装成 SimpleCommandLinePropertySource 加入environment中。
    configurePropertySources(environment, args);
    // 激活相应的配置文件
    configureProfiles(environment, args);
}
```

#### 4.3、 listeners.environmentPrepared(environment);

主要进入到了：

SimpleApplicationEventMulticaster类的multicastEvent()

```java
	public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
		ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
		Executor executor = getTaskExecutor();
		for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
			if (executor != null) {
				executor.execute(() -> invokeListener(listener, event));
			}
			else {
				invokeListener(listener, event);
			}
		}
	}
```

```java
resolveDefaultEventType(event)//获取所有注册的监听器
```

其中存在一个重要的监听器：ConfigFileApplicationListener！！

这个监听器默认的从注释中标签所示的几个位置加载配置文件，并将其加入 上下文的 environment变量中。当然也可以通过配置指定

### 五、第三步：初始化应用上下文

```java
context = createApplicationContext();
```



```java
	protected ConfigurableApplicationContext createApplicationContext() {
		Class<?> contextClass = this.applicationContextClass;
		if (contextClass == null) {
			try {
				switch (this.webApplicationType) {
				case SERVLET:
                        //AnnotationConfigServletWebServerApplicationContext
					contextClass = Class.forName(DEFAULT_SERVLET_WEB_CONTEXT_CLASS);
					break;
				case REACTIVE:
                        //AnnotationConfigReactiveWebServerApplicationContext
					contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
					break;
				default:
                        //AnnotationConfigApplicationContext
					contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
				}
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException(
						"Unable create a default ApplicationContext, please specify an ApplicationContextClass", ex);
			}
		}
		return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
	}
```

springboot的应用种类：

```java
public enum WebApplicationType {
    /**
     * 应用程序不是web应用，也不应该用web服务器去启动
     */
    NONE,
    /**
     * 应用程序应作为基于servlet的web应用程序运行，并应启动嵌入式servlet web（tomcat）服务器。
     */
    SERVLET,
    /**
     * 应用程序应作为 reactive web应用程序运行，并应启动嵌入式 reactive web服务器。
     */
    REACTIVE
}
```

应用上下文可以理解成IoC容器的高级表现形式

应用上下文对IoC容器是持有的关系。他的一个属性beanFactory就是IoC容器（DefaultListableBeanFactory）

```
DefaultListableBeanFactory//springboot中IOC容器的体现
```

### 六：刷新应用上下文前准备阶段

```java
prepareContext(context, environment, listeners, applicationArguments, printedBanner);
```

```java
private void prepareContext(ConfigurableApplicationContext context,
                            ConfigurableEnvironment environment, SpringApplicationRunListeners listeners,
                            ApplicationArguments applicationArguments, Banner printedBanner) {
    //设置容器环境
    context.setEnvironment(environment);
    //执行容器后置处理
    postProcessApplicationContext(context);
    //执行容器中的 ApplicationContextInitializer 包括spring.factories和通过三种方式自定义的
    applyInitializers(context);
    //向各个监听器发送容器已经准备好的事件
    listeners.contextPrepared(context);
    if (this.logStartupInfo) {
        logStartupInfo(context.getParent() == null);
        logStartupProfileInfo(context);
    }

    // Add boot specific singleton beans
    //将main函数中的args参数封装成单例Bean，注册进容器
    context.getBeanFactory().registerSingleton("springApplicationArguments",
            applicationArguments);
    //将 printedBanner 也封装成单例，注册进容器
    if (printedBanner != null) {
        context.getBeanFactory().registerSingleton("springBootBanner", printedBanner);
    }

    // Load the sources
    Set<Object> sources = getAllSources();
    Assert.notEmpty(sources, "Sources must not be empty");
    //加载我们的启动类，将启动类注入容器
    load(context, sources.toArray(new Object[0]));
    //发布容器已加载事件
    listeners.contextLoaded(context);
}
```

getAllSources() 拿到启动类

```java
load(context, sources.toArray(new Object[0]));
```

load方法中 

```java
BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
```

创建BeanDefinitionLoader ，继续跟进。。。

getBeanDefinitionRegistry(...)方法

```java
private BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context) {
   if (context instanceof BeanDefinitionRegistry) {
      return (BeanDefinitionRegistry) context;
   }
   if (context instanceof AbstractApplicationContext) {
      return (BeanDefinitionRegistry) ((AbstractApplicationContext) context).getBeanFactory();
   }
   throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
}
```

强行将context上下文转换成上下文强转为BeanDefinitionRegistry

**BeanDefinitionRegistry定义了很重要的方法registerBeanDefinition()，该方法将BeanDefinition注册进DefaultListableBeanFactory容器的beanDefinitionMap中**



创建方法

```java
BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
```



```java
BeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
    Assert.notNull(registry, "Registry must not be null");
    Assert.notEmpty(sources, "Sources must not be empty");
    this.sources = sources;
    //注解形式的Bean定义读取器 比如：@Configuration @Bean @Component @Controller @Service等等
    this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
    //XML形式的Bean定义读取器
    this.xmlReader = new XmlBeanDefinitionReader(registry);
    if (isGroovyPresent()) {
        this.groovyReader = new GroovyBeanDefinitionReader(registry);
    }
    //类路径扫描器
    this.scanner = new ClassPathBeanDefinitionScanner(registry);
    //扫描器添加排除过滤器
    this.scanner.addExcludeFilter(new ClassExcludeFilter(sources));
}
```

IoC容器的初始化分为三个步骤，上面三个属性在，BeanDefinition的Resource定位，和BeanDefinition的注册中起到了很重要的作用。



跟进load()方法

```java
private int load(Object source) {
    Assert.notNull(source, "Source must not be null");
    // 从Class加载
    if (source instanceof Class<?>) {
        return load((Class<?>) source);
    }
    // 从Resource加载
    if (source instanceof Resource) {
        return load((Resource) source);
    }
    // 从Package加载
    if (source instanceof Package) {
        return load((Package) source);
    }
    // 从 CharSequence 加载 ？？？
    if (source instanceof CharSequence) {
        return load((CharSequence) source);
    }
    throw new IllegalArgumentException("Invalid source type " + source.getClass());
}
```

当前我们的主类会按Class加载

```java
private int load(Class<?> source) {
    if (isGroovyPresent()
            && GroovyBeanDefinitionSource.class.isAssignableFrom(source)) {
        // Any GroovyLoaders added in beans{} DSL can contribute beans here
        GroovyBeanDefinitionSource loader = BeanUtils.instantiateClass(source,
                GroovyBeanDefinitionSource.class);
        load(loader);
    }
    if (isComponent(source)) {
        //将 启动类的 BeanDefinition注册进 beanDefinitionMap
        this.annotatedReader.register(source);
        return 1;
    }
    return 0;
}
```



sComponent(source)判断主类是不是存在@Component注解，主类@SpringBootApplication是一个组合注解



跟进register()方法，最终进到AnnotatedBeanDefinitionReader类的doRegisterBean()方法。

该方法中将主类封装成AnnotatedGenericBeanDefinition

```java
BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
//方法将BeanDefinition注册进beanDefinitionMap
```

```java
public static void registerBeanDefinition(
        BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
        throws BeanDefinitionStoreException {
    // Register bean definition under primary name.
    // primary name 其实就是id吧
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
    // Register aliases for bean name, if any.
    // 然后就是注册别名
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());方法

```java
//实质上是 向beanDefinitionMap 中put值
//最终调用了DefaultListableBeanFactory类的registerBeanDefinition()方法
```

### 七：刷新应用上下文

```java
refreshContext(context);
```



AbstractApplicationContext类的refresh()

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // Prepare this context for refreshing.
        //刷新上下文环境
        prepareRefresh();
        // Tell the subclass to refresh the internal bean factory.
        //这里是在子类中启动 refreshBeanFactory() 的地方
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        // Prepare the bean factory for use in this context.
        //准备bean工厂，以便在此上下文中使用
        prepareBeanFactory(beanFactory);
        try {
            // Allows post-processing of the bean factory in context subclasses.
            //设置 beanFactory 的后置处理
            // 对Web Spring而言， 这儿注册了个BeanPostProcessor.
            // 这步很重要， 因为后续的BeanFactoryPostProcessor的实例类需要这个做预处理
            postProcessBeanFactory(beanFactory);
            // Invoke factory processors registered as beans in the context.
            //调用 BeanFactory 的后处理器，这些处理器是在Bean 定义中向容器注册的
            // 在此处通过PostProcessorRegistrationDelegate对BeanFactoryPostProcessor进行初始化.
            // 在 Spring载入流程中 通过扫包(component-scan)、 自定义标签(<bean />)等方式获取到的BeanFactoryPostProcessor，将会在这儿进行初始化。
            // 一般令人喜闻乐见的PropertySourcesPlaceholderConfigurer就是在这儿进行初始化的。它会将配置文件切分成kv形式的Properties。在后续流程中使用。
            // BeanFactoryPostProcessor也是Spring Bean, 也遵守与Spring生命周期的规则。
            // BeanFactoryPostProcessor不仅被初始化，还被执行了postProcessBeanFactory方法
            invokeBeanFactoryPostProcessors(beanFactory);
            // Register bean processors that intercept bean creation.
            //注册Bean的后处理器，在Bean创建过程中调用
            // 在此处通过PostProcessorRegistrationDelegate对BeanPostProcessor等进行初始化.
            // 流程很简单，将BeanPostProcessor加入预备列表. 用于贯穿Spring Bean的生命周期
            // TODO 比如包含@Resource、@Value等非配置类注解的Bean， 就会通过BeanPostProcessor对它们进行赋值。
            // BeanPostProcessor仅仅被初始化，没有执行它的方法（它本就是给别的类用的）
            registerBeanPostProcessors(beanFactory);
            // Initialize message source for this context.
            //对上下文中的消息源进行初始化
            initMessageSource();
            // Initialize event multicaster for this context.
            //初始化上下文中的事件机制
            // 指定SimpleApplicationEventMulticaster作为系统观察者
            // 可见ApplicationEventPublisherAware, ApplicationEvent
            initApplicationEventMulticaster();
            // Initialize other special beans in specific context subclasses.
            //初始化其他特殊的Bean
            onRefresh();
            // Check for listener beans and register them.
            //检查监听Bean并且将这些监听Bean向容器注册
            // 在initApplicationEventMulticaster()中注册了观察者， 在此处为观察者添加被观察者。
            // 在这里观察者们就可以接受Context消息了。
            registerListeners();
            // Instantiate all remaining (non-lazy-init) singletons.
            //实例化所有的（non-lazy-init）单件
            // 这里是对所有剩余Bean(non-lazy-init)的初始化
            // 也是Spring初始化的很关键一步。
            finishBeanFactoryInitialization(beanFactory);
            // Last step: publish corresponding event.
            //发布容器事件，结束Refresh过程
            // 此处开启了Spring的生命周期
            // 事件也可以开始被发送
            finishRefresh();
        } catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                        "cancelling refresh attempt: " + ex);
            }
            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();
            // Reset 'active' flag.
            cancelRefresh(ex);
            // Propagate exception to caller.
            throw ex;
        } finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
        }
    }
}
```

refresh()方法中 比较重要的两步

```java
//执行所有的BeanFactoryPostProcessor
invokeBeanFactoryPostProcessors(beanFactory);
//对所有剩下的非懒加载的bean做初始化
finishBeanFactoryInitialization(beanFactory)
```



#### 一：obtainFreshBeanFactory()

```java
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
    //刷新BeanFactory
    refreshBeanFactory();
    //获取beanFactory
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    if (logger.isDebugEnabled()) {
        logger.debug("Bean factory for " + getDisplayName() + ": " + beanFactory);
    }
    return beanFactory;
}
```

obtainFreshBeanFactory()方法，其实就是拿到我们之前创建的beanFactory。三个工作，刷新beanFactory，获取beanFactory，返回beanFactory



```java
@Override
protected final void refreshBeanFactory() throws IllegalStateException {
    if (!this.refreshed.compareAndSet(false, true)) {
        throw new IllegalStateException(
                "GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
    }
    this.beanFactory.setSerializationId(getId());
}
/*
　　1，AbstractApplicationContext类有两个子类实现了refreshBeanFactory()，但是在前面第三步初始化上下文的时候，
实例化了GenericApplicationContext类，所以没有进入AbstractRefreshableApplicationContext中的refreshBeanFactory()方法。
　　2，this.refreshed.compareAndSet(false, true) 
　　这行代码在这里表示：GenericApplicationContext只允许刷新一次 　　
　　这行代码，很重要，不是在Spring中很重要，而是这行代码本身。首先看一下this.refreshed属性： 
private final AtomicBoolean refreshed = new AtomicBoolean(); 
　　java J.U.C并发包中很重要的一个原子类AtomicBoolean。通过该类的compareAndSet()方法可以实现一段代码绝对只实现一次的功能。
*/
```

#### 二：prepareBeanFactory(beanFactory);

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // Tell the internal bean factory to use the context's class loader etc.
    // 配置类加载器：默认使用当前上下文的类加载器
    beanFactory.setBeanClassLoader(getClassLoader());
    // 配置EL表达式：在Bean初始化完成，填充属性的时候会用到
    beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
    // 添加属性编辑器 PropertyEditor
    beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

    // Configure the bean factory with context callbacks.
    // 添加Bean的后置处理器
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    // 忽略装配以下指定的类
    beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
    beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
    beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
    beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

    // BeanFactory interface not registered as resolvable type in a plain factory.
    // MessageSource registered (and found for autowiring) as a bean.
    // 将以下类注册到 beanFactory（DefaultListableBeanFactory） 的resolvableDependencies属性中
    beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
    beanFactory.registerResolvableDependency(ResourceLoader.class, this);
    beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
    beanFactory.registerResolvableDependency(ApplicationContext.class, this);

    // Register early post-processor for detecting inner beans as ApplicationListeners.
    // 将早期后处理器注册为application监听器，用于检测内部bean
    beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

    // Detect a LoadTimeWeaver and prepare for weaving, if found.
    //如果当前BeanFactory包含loadTimeWeaver Bean，说明存在类加载期织入AspectJ，
    // 则把当前BeanFactory交给类加载期BeanPostProcessor实现类LoadTimeWeaverAwareProcessor来处理，
    // 从而实现类加载期织入AspectJ的目的。
    if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
        // Set a temporary ClassLoader for type matching.
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }

    // Register default environment beans.
    // 将当前环境变量（environment） 注册为单例bean
    if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
        beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
    }
    // 将当前系统配置（systemProperties） 注册为单例Bean
    if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
        beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
    }
    // 将当前系统环境 （systemEnvironment） 注册为单例Bean
    if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
        beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
    }
}
```

#### 三、postProcessBeanFactory(beanFactory);

postProcessBeanFactory()方法向上下文中添加了一系列的Bean的后置处理器。后置处理器工作的时机是在所有的beanDenifition加载完成之后，bean实例化之前执行。简单来说Bean的后置处理器可以修改BeanDefinition的属性信息



#### 四：invokeBeanFactoryPostProcessors(beanFactory) 重点！！！



```java
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
		// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
	}
```

IoC容器的初始化过程包括三个步骤，在invokeBeanFactoryPostProcessors()方法中完成了IoC容器初始化过程的三个步骤

##### 4.1，第一步：Resource定位

在SpringBoot中，我们都知道他的包扫描是从主类所在的包开始扫描的，prepareContext()方法中，会先将主类解析成BeanDefinition，然后在refresh()方法的invokeBeanFactoryPostProcessors()方法中解析主类的BeanDefinition获取basePackage的路径。这样就完成了定位的过程。其次SpringBoot的各种starter是通过SPI扩展机制实现的自动装配，SpringBoot的自动装配同样也是在invokeBeanFactoryPostProcessors()方法中实现的。还有一种情况，在SpringBoot中有很多的@EnableXXX注解，细心点进去看的应该就知道其底层是@Import注解，在invokeBeanFactoryPostProcessors()方法中也实现了对该注解指定的配置类的定位加载。

　　常规的在SpringBoot中有三种实现定位，第一个是主类所在包的，第二个是SPI扩展机制实现的自动装配（比如各种starter），第三种就是@Import注解指定的类。

##### 4.2，第二步：BeanDefinition的载入

在第一步中说了三种Resource的定位情况，定位后紧接着就是BeanDefinition的分别载入。所谓的载入就是通过上面的定位得到的basePackage，SpringBoot会将该路径拼接成：classpath*:org/springframework/boot/demo/**/*.class这样的形式，然后一个叫做PathMatchingResourcePatternResolver的类会将该路径下所有的.class文件都加载进来，然后遍历判断是不是有@Component注解，如果有的话，就是我们要装载的BeanDefinition。

##### 4.3，第三个过程：注册BeanDefinition

这个过程通过调用上文提到的BeanDefinitionRegister接口的实现来完成。这个注册过程把载入过程中解析得到的BeanDefinition向IoC容器进行注册。我们可以看到，在IoC容器中将BeanDefinition注入到一个ConcurrentHashMap中，IoC容器就是通过这个HashMap来持有这些BeanDefinition数据的。比如DefaultListableBeanFactory
中的beanDefinitionMap属性



```java
//路跟踪调用栈，来到ConfigurationClassParser类的parse()方法。
```

```java
// ConfigurationClassParser类
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    this.deferredImportSelectors = new LinkedList<>();
    for (BeanDefinitionHolder holder : configCandidates) {
        BeanDefinition bd = holder.getBeanDefinition();
        try {
            // 如果是SpringBoot项目进来的，bd其实就是前面主类封装成的 AnnotatedGenericBeanDefinition（AnnotatedBeanDefinition接口的实现类）
            if (bd instanceof AnnotatedBeanDefinition) {
                parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
            } else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
            } else {
                parse(bd.getBeanClassName(), holder.getBeanName());
            }
        } catch (BeanDefinitionStoreException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanDefinitionStoreException(
                    "Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
        }
    }
    // 加载默认的配置---》（对springboot项目来说这里就是自动装配的入口了）
  this.deferredImportSelectorHandler.process();
}
```



继续沿着parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());方法跟下去

```java
// ConfigurationClassParser类
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    processConfigurationClass(new ConfigurationClass(metadata, beanName));
}
// ConfigurationClassParser类
protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
    ...
    // Recursively process the configuration class and its superclass hierarchy.
    //递归地处理配置类及其父类层次结构。
    SourceClass sourceClass = asSourceClass(configClass);
    do {
        //递归处理Bean，如果有父类，递归处理，直到顶层父类
        sourceClass = doProcessConfigurationClass(configClass, sourceClass);
    }
    while (sourceClass != null);

    this.configurationClasses.put(configClass, configClass);
}
// ConfigurationClassParser类
protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass)
        throws IOException {

    // Recursively process any member (nested) classes first
    //首先递归处理内部类，（SpringBoot项目的主类一般没有内部类）
    processMemberClasses(configClass, sourceClass);

    // Process any @PropertySource annotations
    // 针对 @PropertySource 注解的属性配置处理
    for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
            sourceClass.getMetadata(), PropertySources.class,
            org.springframework.context.annotation.PropertySource.class)) {
        if (this.environment instanceof ConfigurableEnvironment) {
            processPropertySource(propertySource);
        } else {
            logger.warn("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
                    "]. Reason: Environment must implement ConfigurableEnvironment");
        }
    }

    // Process any @ComponentScan annotations
    // 根据 @ComponentScan 注解，扫描项目中的Bean（SpringBoot 启动类上有该注解）
    Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
            sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
    if (!componentScans.isEmpty() &&
            !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
        for (AnnotationAttributes componentScan : componentScans) {
            // The config class is annotated with @ComponentScan -> perform the scan immediately
            // 立即执行扫描，（SpringBoot项目为什么是从主类所在的包扫描，这就是关键了）
            Set<BeanDefinitionHolder> scannedBeanDefinitions =
                    this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
            // Check the set of scanned definitions for any further config classes and parse recursively if needed
            for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                if (bdCand == null) {
                    bdCand = holder.getBeanDefinition();
                }
                // 检查是否是ConfigurationClass（是否有configuration/component两个注解），如果是，递归查找该类相关联的配置类。
                // 所谓相关的配置类，比如@Configuration中的@Bean定义的bean。或者在有@Component注解的类上继续存在@Import注解。
                if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
                    parse(bdCand.getBeanClassName(), holder.getBeanName());
                }
            }
        }
    }

    // Process any @Import annotations
    //递归处理 @Import 注解（SpringBoot项目中经常用的各种@Enable*** 注解基本都是封装的@Import）
    processImports(configClass, sourceClass, getImports(sourceClass), true);

    // Process any @ImportResource annotations
    AnnotationAttributes importResource =
            AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
    if (importResource != null) {
        String[] resources = importResource.getStringArray("locations");
        Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
        for (String resource : resources) {
            String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
            configClass.addImportedResource(resolvedResource, readerClass);
        }
    }

    // Process individual @Bean methods
    Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
    for (MethodMetadata methodMetadata : beanMethods) {
        configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
    }

    // Process default methods on interfaces
    processInterfaces(configClass, sourceClass);

    // Process superclass, if any
    if (sourceClass.getMetadata().hasSuperClass()) {
        String superclass = sourceClass.getMetadata().getSuperClassName();
        if (superclass != null && !superclass.startsWith("java") &&
                !this.knownSuperclasses.containsKey(superclass)) {
            this.knownSuperclasses.put(superclass, configClass);
            // Superclass found, return its annotation metadata and recurse
            return sourceClass.getSuperClass();
        }
    }

    // No superclass -> processing is complete
    return null;
}


/*
　　在以上代码的parse(bdCand.getBeanClassName(), holder.getBeanName());会进行递归调用，
因为当Spring扫描到需要加载的类会进一步判断每一个类是否满足是@Component/@Configuration注解的类，
如果满足会递归调用parse()方法，查找其相关的类。
　　同样的processImports(configClass, sourceClass, getImports(sourceClass), true);
通过@Import注解查找到的类同样也会递归查找其相关的类。
　　两个递归在debug的时候会很乱，用文字叙述起来更让人难以理解，所以，我们只关注对主类的解析，及其类的扫描过程。
*/
```

for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(... 获取主类上的@PropertySource注解，解析该注解并将该注解指定的properties配置文件中的值存储到Spring的 Environment中，Environment接口提供方法去读取配置文件中的值，参数是properties文件中定义的key值。

　　 Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable( sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class); 解析主类上的@ComponentScan注解，呃，怎么说呢，后面的代码将会解析该注解并进行包扫描。

　　 processImports(configClass, sourceClass, getImports(sourceClass), true); 解析主类上的@Import注解，并加载该注解指定的配置类



### 八：springboot启动时各个扩展点加载的时机

#### 一：BeanFactoryPostProcessor

 	postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)

 	容器refresh()阶段

```
	  SpringApplication
	->run()
	->refreshContext(context)
      AbstractApplicationContext
    ->refresh()
    ->invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) 
    ->invokeBeanFactoryPostProcessors(registryProcessors, beanFactory)
```

 

####  二：BeanPostProcessor

 	postProcessBeforeInitialization(Object bean, String beanName) 

 	postProcessAfterInitialization(Object bean, String beanName)

 	容器refresh()阶段

​	Springboot 的web环境：

```
 	  SpringApplication
 	->run()
 	->refreshContext(context)
      AbstractApplicationContext
    ->refresh()
    ->onRefresh()
      ServletWebServerApplicationContext
    ->onRefresh()
    ->createWebServer()
    -> ...
      BeanPostProcessor
    ->postProcessBeforeInitialization(Object bean, String beanName)
    ->postProcessAfterInitialization(Object bean, String beanName)
```

 	

 	普通的单例bean

```
 	 SpringApplication::run()
 	->refreshContext(context)
    ->AbstractApplicationContext::refresh()
    ->finishBeanFactoryInitialization(beanFactory)
    ->beanFactory.preInstantiateSingletons()
     DefaultListableBeanFactory
    ->preInstantiateSingletons()
    ->getBean(String name)
    ->getSingleton(String beanName, ObjectFactory<?> singletonFactory)
    ->...
      BeanPostProcessor
    ->postProcessBeforeInitialization(Object bean, String beanName)
    ->postProcessAfterInitialization(Object bean, String beanName)
```



####  	三：ApplicationListener<E extends ApplicationEvent> extends EventListener

 	事件通知机制：观察者设计模式的实现

 	每当ApplicationContext发布ApplicationEvent时，ApplicationListener Bean将自动被触发

 	加载时机：每个event加载的时机都不同！！！



####  	四：BeanDefinitionRegistryPostProcessor

 	BeanDefinitionRegistryPostProcessor继承了BeanFactoryPostProcessor 是个beanFactory的后置处理器。

 	在容器refresh()阶段会先执行BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)方法。

 	再去执行BeanFactoryPostProcessor的postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)方法。

 	

```java
//该方法用来注册更多的bean到spring容器中
postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) 
//主要用来对bean定义做一些改变
postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
```



```java
//提供了丰富的方法来操作BeanDefinition，判断、注册、移除等
BeanDefinitionRegistry
```



```
BeanFactoryPostProcessor可以修改各个注册的Bean，BeanDefinitionRegistryPostProcessor可以动态将Bean注册
```

 	加载时机

 	和BeanFactoryPostProcessor的postProcessBeanFactory方法在同一个方法中。

####  	五：SpringApplicationRunListener

 	SpringApplicationRunListener 接口的作用主要就是在Spring Boot 启动初始化的过程中可以通过SpringApplicationRunListener接口回调来让用户在启动的各个流程中可以加入自己的逻辑。

 	

```java
public interface SpringApplicationRunListener {

    // 在run()方法开始执行时，该方法就立即被调用，可用于在初始化最早期时做一些工作
    void starting();
    // 当environment构建完成，ApplicationContext创建之前，该方法被调用
    void environmentPrepared(ConfigurableEnvironment environment);
    // 当ApplicationContext构建完成时，该方法被调用
    void contextPrepared(ConfigurableApplicationContext context);
    // 在ApplicationContext完成加载，但没有被刷新前，该方法被调用
    void contextLoaded(ConfigurableApplicationContext context);
    // 在ApplicationContext刷新并启动后，CommandLineRunners和ApplicationRunner未被调用前，该方法被调用
    void started(ConfigurableApplicationContext context);
    // 在run()方法执行完成前该方法被调用
    void running(ConfigurableApplicationContext context);
    // 当应用运行出错时该方法被调用
    void failed(ConfigurableApplicationContext context, Throwable exception);
}
```



实现SpringApplicationRunListener接口，并且必须提供一个包含参数（SpringApplication application, String[] args）的构造方法



在spring.factories中配置自定义SpringApplicationRunListener.之后在启动Spring Boot应用时，就会自动将HelloApplicationRunListener实例化，并在Spring容器初始化的各个阶段回调对应的方法。





#### 六：EnvironmentPostProcessor

 	动态管理配置文件扩展接口 是springBoot的内容

 	使用这个进行配置文件的集中管理，而不需要每个项目都去配置配置文件

```java
@FunctionalInterface
public interface EnvironmentPostProcessor {

	/**
	 * Post-process the given {@code environment}.
	 * @param environment the environment to post-process
	 * @param application applicationContext
	 */
	void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application);

}
```

   加载时间：在run()的环境准备阶段prepareEnvironment() 使用监听器的方式listener 执行EnvironmentPostProcessor的postProcessEnvironment方法

