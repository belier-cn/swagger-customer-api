package cn.belier.swagger.customer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author belier
 * @date 2019/4/24
 */
@Slf4j
public class CustomerApiRequestHandlerProvider implements RequestHandlerProvider {

    @Autowired
    private HandlerMethodResolver handlerMethodResolver;

    /**
     * 要处理的类的集合
     */
    private Set<Class<?>> classes = Sets.newHashSet();

    public CustomerApiRequestHandlerProvider(Collection<Class<?>> classes) {
        if (!CollectionUtils.isEmpty(classes)) {
            this.classes.addAll(classes);
        }
    }

    @Override
    public List<RequestHandler> requestHandlers() {

        List<RequestHandler> requestHandlers = Lists.newArrayList();

        for (Class<?> c : classes) {

            Object bean = newInstance(c);

            if (bean == null) {
                // 不能创建对象就跳过
                continue;
            }

            Method[] methods = c.getMethods();

            for (Method method : methods) {

                WebMvcRequestHandler requestHandler = getWebMvcRequestHandler(bean, c, method);

                if (requestHandler != null) {

                    requestHandlers.add(requestHandler);
                }

            }
        }

        return requestHandlers;
    }

    /**
     * 创建实例
     *
     * @param c 类
     * @return 类对应的实例
     */
    private Object newInstance(Class c) {

        try {
            return c.newInstance();
        } catch (Exception e) {
            log.warn("{}反射创建实例失败，请确保该类有无参构造器", c);
        }

        return null;
    }


    /**
     * 获取方法对应的 {@link WebMvcRequestHandler}
     *
     * @param bean        方法对应的类的实例
     * @param handlerType 方法对应的类
     * @param method      方法
     * @return {@link WebMvcRequestHandler}
     */
    private WebMvcRequestHandler getWebMvcRequestHandler(Object bean, Class<?> handlerType, Method method) {

        RequestMappingInfo requestMappingInfo = getMappingForMethod(method, handlerType);

        if (requestMappingInfo == null) {
            return null;
        }

        HandlerMethod handlerMethod = new HandlerMethod(bean, method);

        return new WebMvcRequestHandler(handlerMethodResolver, requestMappingInfo, handlerMethod);

    }


    /**
     * 获取方法对应的 {@link RequestMappingInfo}
     *
     * @param method      方法
     * @param handlerType 方法对应的类
     * @return {@link RequestMappingInfo}
     */
    private RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }


    /**
     * 创建 {@link RequestMappingInfo}
     *
     * @param element {@link AnnotatedElement}
     * @return {@link RequestMappingInfo}
     */
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {

        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);

        if (requestMapping == null) {
            return null;
        }
        return RequestMappingInfo
                .paths(requestMapping.path())
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name())
                .build();

    }


}
