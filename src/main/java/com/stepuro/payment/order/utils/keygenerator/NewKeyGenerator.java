package com.stepuro.payment.order.utils.keygenerator;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;
import reactor.util.annotation.NonNull;

import java.lang.reflect.Method;

public class NewKeyGenerator implements KeyGenerator {
    @Override
    @NonNull
    public Object generate(Object target, @NonNull Method method, @NonNull Object... params) {
        return target.getClass().getSimpleName() + "_"
                + method.getName() + "_"
                + StringUtils.arrayToDelimitedString(params, "_");
    }
}
