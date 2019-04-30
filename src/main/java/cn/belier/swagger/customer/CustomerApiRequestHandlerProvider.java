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

    private Set<Class<?>> classes = Sets.newHashSet();

    public CustomerApiRequestHandlerProvider(Collection<Class<?>> classes) {
        if (!CollectionUtils.isEmpty(classes)) {
            this.classes.addAll(classes);
        }
    }

    @Override
    public List<RequestHandler> requestHandlers() {

        List<RequestHandler> requestHandlers = Lists.newArrayList();

        for (Class<?> aClass : classes) {
            Object bean;

            try {
                bean = aClass.newInstance();
            } catch (Exception e) {
                log.warn("{}反射创建实例失败，请确保该类有无参构造器", aClass);
                continue;
            }

            Method[] methods = aClass.getMethods();

            for (Method method : methods) {

                RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);

                if (requestMapping != null) {


                    RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(requestMapping.value())
                            .consumes(requestMapping.consumes())
                            .headers(requestMapping.headers())
                            .methods(requestMapping.method())
                            .produces(requestMapping.produces())
                            .params(requestMapping.params())
                            .build();

                    HandlerMethod handlerMethod = new HandlerMethod(bean, method);

                    requestHandlers.add(new WebMvcRequestHandler(handlerMethodResolver, requestMappingInfo, handlerMethod));

                }
            }
        }


        return requestHandlers;
    }


}
