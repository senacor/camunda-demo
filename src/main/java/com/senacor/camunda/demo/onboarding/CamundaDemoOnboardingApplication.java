package com.senacor.camunda.demo.onboarding;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.senacor.camunda.demo.onboarding.**"
        }
)
@Deployment(resources = {
        "classpath*:/camunda/**/*.bpmn",
        "classpath*:/camunda/**/*.form"
})
public class CamundaDemoOnboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CamundaDemoOnboardingApplication.class, args);
    }
}
