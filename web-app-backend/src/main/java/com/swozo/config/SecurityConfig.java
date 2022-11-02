package com.swozo.config;

import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.security.PasswordHandler;
import com.swozo.security.filters.AuthFilter;
import com.swozo.security.filters.FilterExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import java.util.List;
import java.util.Map;

import static org.passay.IllegalCharacterRule.ERROR_CODE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final int MIN_PASSWD_DIGITS  = 2;
    private static final int MIN_PASSWD_LOWER   = 2;
    private static final int MIN_PASSWD_UPPER   = 2;
    private static final int MIN_PASSWD_SPECIAL = 2;
    public final static String ALLOWED_PASSWD_SPECIAL_CHARACTERS = "!@#$%^&*()_+";

    private final AuthFilter authFilter;
    private final FilterExceptionHandler filterExceptionHandler;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()
                .and()
                .authorizeRequests().anyRequest().permitAll() // AuthFilter decides who gets in
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(filterExceptionHandler, ChannelProcessingFilter.class)
                .addFilterAfter(authFilter, SecurityContextPersistenceFilter.class)
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SCryptPasswordEncoder();
    }

    @Bean
    public PasswordHandler passwordHandler() {
        var lowercaseRule = new CharacterRule(PolishCharacterData.LowerCase);
        lowercaseRule.setNumberOfCharacters(MIN_PASSWD_LOWER);

        var uppercaseRule = new CharacterRule(PolishCharacterData.UpperCase);
        uppercaseRule.setNumberOfCharacters(MIN_PASSWD_UPPER);

        var digitRule = new CharacterRule(EnglishCharacterData.Digit);
        digitRule.setNumberOfCharacters(MIN_PASSWD_DIGITS);

        var specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return ALLOWED_PASSWD_SPECIAL_CHARACTERS;
            }
        };

        var specialCharRule = new CharacterRule(specialChars);
        specialCharRule.setNumberOfCharacters(MIN_PASSWD_SPECIAL);

        return new PasswordHandler(
                List.of(lowercaseRule, uppercaseRule, digitRule, specialCharRule),
                new PasswordGenerator(),
                Map.of(
                        ValidationNames.Errors.PASSWORD_MIN_LOWERCASE, MIN_PASSWD_LOWER,
                        ValidationNames.Errors.PASSWORD_MIN_UPPERCASE, MIN_PASSWD_UPPER,
                        ValidationNames.Errors.PASSWORD_MIN_DIGITS, MIN_PASSWD_DIGITS,
                        ValidationNames.Errors.PASSWORD_MIN_SPECIAL, MIN_PASSWD_SPECIAL,
                        ValidationNames.Errors.PASSWORD_ALLOWED_SPECIALS, ALLOWED_PASSWD_SPECIAL_CHARACTERS
                )
        );
    }
}
