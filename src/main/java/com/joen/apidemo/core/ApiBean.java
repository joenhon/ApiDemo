package com.joen.apidemo.core;

import com.joen.apidemo.annotations.ApiService;
import com.joen.apidemo.proxys.ApiProxyFactory;
import com.joen.apidemo.utils.Scanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Description
 * @Author Joen
 * @Date 2020-12-29 11:18:15
 */
@Order(0)
@Component
public class ApiBean implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {


    private ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try {
            /*API service装载*/
            Set<Class<?>> classSet = Scanner.getAnnotationClasses(ApiService.class);
            for (Class<?> aClass : classSet) {

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(aClass);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
                definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
                definition.setBeanClass(ApiProxyFactory.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                // 注册bean名,一般为类名首字母小写
                beanDefinitionRegistry.registerBeanDefinition(aClass.getName(), definition);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        environment = applicationContext.getEnvironment();
    }

    public static String getProperty(String key){
        return environment.getProperty(key);
    }

    public static <T> T getProperty(String key,Class<T> aClass){
        return environment.getProperty(key,aClass);
    }

}
