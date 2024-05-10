package com.coCloud.web.validator;

import com.coCloud.core.constants.CoCloudConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * ClassName: WebValidatorConfig
 * Description: 统一的参数校验器，快速失败只要有一个不符合就报错
 *
 * @Author agility6
 * @Create 2024/5/10 13:39
 * @Version: 1.0
 */
@SpringBootConfiguration
@Slf4j
public class WebValidatorConfig {

    private static final String FAIL_FAST_KEY = "hibernate.validator.fail_fast";

    /**
     * 参数后置处理器，放入了validator对象
     * 每次方法执行都通过参数对象执行前后通过参数校验对象去拦截校验是否符合
     *
     * @return
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(coCoValidator());
        log.info("The hibernate validator is loaded successfully!");
        return postProcessor;

    }

    /**
     * 构造项目方法参数校验器
     *
     * @return
     */
    private Validator coCoValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().addProperty(FAIL_FAST_KEY, CoCloudConstants.TRUE_STR).buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
