package cn.belier.swagger.customer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识该类需要进行 Swagger 文档处理
 *
 * @author belier
 * @date 2019/4/24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerApi {
}
