package com.senacor.camunda.demo.onboarding;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CamundaConstants {

    public static final String CAMUNDA_PROCESS_ID_ONBOARDING = "onboarding-process";

    public static final String CAMUNDA_VARIABLES_ONBOARDING_ID = "onboardingId";
    public static final String CAMUNDA_VARIABLES_ONBOARDING_REQUEST = "onboardingRequest";
    public static final String CAMUNDA_VARIABLES_ONBOARDING_MESSAGE_RESULT = "onboardingMessageResult";
}
