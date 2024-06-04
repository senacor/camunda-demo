package com.senacor.camunda.demo.onboarding;

public record CreditCard(String owner, String number, Integer expirationMonth, Integer expirationYear, String cvv, String status) {
}
