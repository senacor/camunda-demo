package com.senacor.camunda.demo.onboarding;

import com.senacor.camunda.demo.onboarding.generated.api.model.ApiOnboarding;
import lombok.Data;

import java.util.UUID;

@Data
public class CamundaProcessVariables {

    private UUID onboardingId;
    private ApiOnboarding onboardingRequest;
    private String errorCode;
    private CreditCard creditCardInput;
    private long loopCounter;
}
