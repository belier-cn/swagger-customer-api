package cn.belier.swagger.customer;

import com.google.common.collect.Sets;
import org.reflections.Reflections;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link SwaggerCustomerApiScanner}
 *
 * @author belier
 * @date 2019/4/24
 */
public class CustomerApiRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String VALUE = "value";

    private static final String BASE_PACKAGES = "basePackages";

    private static final String BASE_PACKAGE_CLASSES = "basePackageClasses";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        Set<String> basePackages = getBasePackages(annotationMetadata);

        Set<Class<?>> customerApiClasses = getCustomerApiClasses(basePackages);

        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(CustomerApiRequestHandlerProvider.class)
                .addConstructorArgValue(customerApiClasses)
                .getBeanDefinition();

        String beanName = StringUtils.uncapitalize(beanDefinition.getBeanClass().getSimpleName());

        registry.registerBeanDefinition(beanName, beanDefinition);

    }


    /**
     * 获取要扫描的 package
     *
     * @param annotationMetadata 注解元数据
     * @return package 列表
     */
    private Set<String> getBasePackages(AnnotationMetadata annotationMetadata) {

        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(annotationMetadata.getAnnotationAttributes(SwaggerCustomerApiScanner.class.getName()));

        Set<String> basePackages = new HashSet<>();

        assert annotationAttributes != null;

        for (Class<?> clazz : annotationAttributes.getClassArray(VALUE)) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        for (String pkg : annotationAttributes.getStringArray(BASE_PACKAGES)) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annotationAttributes.getClassArray(BASE_PACKAGE_CLASSES)) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        // 默认当前注解对应的包
        if (basePackages.isEmpty() && annotationMetadata instanceof StandardAnnotationMetadata) {
            Class<?> introspectedClass = ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass();
            basePackages.add(ClassUtils.getPackageName(introspectedClass));
        }

        return basePackages;
    }


    /**
     * 获取标识了{@link CustomerApi}注解的类
     *
     * @param basePackages 扫描的包列表
     * @return class 列表
     */
    private Set<Class<?>> getCustomerApiClasses(Set<String> basePackages) {

        Set<Class<?>> classes = Sets.newHashSet();

        for (String basePackage : basePackages) {

            Reflections reflections = new Reflections(basePackage);

            classes.addAll(reflections.getTypesAnnotatedWith(CustomerApi.class));
        }

        return classes;

    }
}
