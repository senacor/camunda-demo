package com.senacor.camunda.demo.onboarding;

import com.senacor.camunda.demo.onboarding.generated.api.model.ApiOnboarding;

import java.util.UUID;

public record CamundaProcessVariables(UUID onboardingId, ApiOnboarding onboardingRequest, String errorCode) {
}