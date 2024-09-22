package com.lkgroup.ecommerce.services.user_service;

import com.lkgroup.ecommerce.common.domain.support.MyJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Security;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableJpaRepositories(repositoryFactoryBeanClass = MyJpaRepositoryFactoryBean.class, basePackages = "com.lkgroup.ecommerce")
@EntityScan(basePackages = "com.lkgroup.ecommerce")
@ComponentScan(basePackages = {"com.lkgroup.ecommerce"})
public class UserServiceApplication {

	public static void main(String[] args) {
		Security.setProperty("crypto.policy", "unlimited");
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
