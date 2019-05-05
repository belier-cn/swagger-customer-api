### 添加swagger额外的接口文档
[![Maven Central](https://img.shields.io/maven-central/v/cn.belier/swagger-customer-api.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22cn.belier%22%20AND%20a:%22swagger-customer-api%22)

### 如何使用

#### 引入依赖
```xml
<dependency>
  <groupId>cn.belier</groupId>
  <artifactId>swagger-customer-api</artifactId>
  <version>最新版本，参见上面的图标</version>
</dependency>

```

#### 编写接口文档

用 CustomerApi 标记，不使用 RestController

```java

@Api(tags = "测试接口")
@CustomerApi
@RequestMapping("test")
public class TestApi {

    @ApiOperation("测试")
    @GetMapping("test")
    public String test(@RequestParam @ApiParam("测试参数") String test) {
        return test;
    }

}

```

#### 开启扫描注解

可以指定包名，不指定默认为标记的类的包

```java
@SwaggerCustomerApiScanner
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

```

TestApi 的接口不会在 Spring MVC 中生效，但是会在文档中显示