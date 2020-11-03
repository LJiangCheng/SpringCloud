package com.springboot.auth.authorization.provider;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SmsCodeProviderFallback implements SmsCodeProvider {

    /*循环引用 暂时去除
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SmsCodeProviderFallback(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }*/

    @Override
    public String getSmsCode(String mobile, String businessType) {
        // 该类为mock, 目前暂时没有sms的服务
        return new BCryptPasswordEncoder().encode("123456");
    }
}
