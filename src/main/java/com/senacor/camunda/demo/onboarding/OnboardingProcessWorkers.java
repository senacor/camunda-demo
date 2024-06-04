package com.senacor.camunda.demo.onboarding;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnboardingProcessWorkers {

    private final ZeebeClient client;

    @JobWorker(type="onboarding-process-business-validation-person-minor")
    public void isPersonMinor(ActivatedJob activatedJob) {
        var birthday = activatedJob.getVariablesAsType(CamundaProcessVariables.class)
                .onboardingRequest()
                .getPerson()
                .getBirthday();
        var age = Period.between(birthday, LocalDate.now()).getYears();
        if (age < 18) {
            log.error("Do business validation: Person is too young. Birthday is {}", birthday);
            client.newThrowErrorCommand(activatedJob).errorCode("customer-onboarding-error-person-is-minor")
                    .errorMessage("You are too young, please contact your parents.")
                    .send()
                    .join();
        } else {
            log.info("Do business validation: person is old enough to have an account.");
        }
    }

    @JobWorker(type="onboarding-process-business-validation-income")
    public void hasEnoughMoney(ActivatedJob activatedJob) {
        var monthIncome = activatedJob.getVariablesAsType(CamundaProcessVariables.class)
                .onboardingRequest()
                .getMonthIncome();
        if (monthIncome < 500) {
            log.error("Do business validation: Person does not have enough money. Income per month is {}", monthIncome);
            client.newThrowErrorCommand(activatedJob).errorCode("customer-onboarding-error-person-has-too-low-income")
                    .errorMessage("You don't have enough money, please ask parents to support you.")
                    .send()
                    .join();
        } else {
            log.info("Do business validation: Person has enough money.");
        }
    }

    @JobWorker(type="onboarding-process-account-creation")
    public void createAccount(ActivatedJob activatedJob) {
        var onboardingRequest = activatedJob.getVariablesAsType(CamundaProcessVariables.class).onboardingRequest();
        var amountOfCreditCards = Optional.ofNullable(onboardingRequest.getAmountOfCreditCards()).orElse(-1);
        if (amountOfCreditCards > 10) {
            log.error("Account creation: Is not allowed to have more than 10 credit cards. Requested amount is {}", amountOfCreditCards);
            int remainingRetries = activatedJob.getRetries() > 0 ? activatedJob.getRetries() - 1 : 0;
            client.newFailCommand(activatedJob).retries(remainingRetries)
                    .retryBackoff(Duration.ofSeconds(10))
                    .errorMessage("You reached limit of max allowed credit cards.")
                    .send()
                    .join();
        } else {
            var owner = onboardingRequest.getPerson().getFirstName() + " " + onboardingRequest.getPerson().getSecondName();
            var creditCards = IntStream.range(0, amountOfCreditCards)
                    .mapToObj(ignore -> new CreditCard(owner, null, null, null, null, null))
                    .toList();
            client.newSetVariablesCommand(activatedJob.getProcessInstanceKey())
                    .variables(Map.of("creditCards", creditCards))
                    .send()
                    .join();
            log.info("Account created successfully.");
        }
    }

    @JobWorker(type="onboarding-process-giro-card-creation")
    public void createGiroCard(ActivatedJob activatedJob) {
        var onboardingRequest = activatedJob.getVariablesAsType(CamundaProcessVariables.class).onboardingRequest();
        var owner = onboardingRequest.getPerson().getFirstName() + " " + onboardingRequest.getPerson().getSecondName();
        var expirationDate = LocalDate.now().plusYears(10);
        var giroCard = new GiroCard(owner, "DE12345675898453933", expirationDate.getMonthValue(), expirationDate.getYear());
        client.newSetVariablesCommand(activatedJob.getProcessInstanceKey())
                .variables(Map.of("giroCard", giroCard))
                .send()
                .join();
        log.info("GiroCard created successfully.");
    }

    @JobWorker(type="onboarding-process-credit-card-creation")
    public void createCreditCard(ActivatedJob activatedJob) {
        var creditCardMutliInstanceVariables = activatedJob.getVariablesAsType(CreditCardMutliInstanceVariables.class);
        var owner = creditCardMutliInstanceVariables.creditCardInput().owner();
        var expirationDate = LocalDate.now().plusYears(10);
        var number = "1234-5678-9876-000" + creditCardMutliInstanceVariables.loopCounter();
        var creditCardOutput = new CreditCard(owner, number, expirationDate.getMonthValue(), expirationDate.getYear(), "692", "SUCCESS");
        if(owner.contains("Bad")) {
            log.error("Do business validation: Person is suspicious. Delegate to backoffice.");
            client.newThrowErrorCommand(activatedJob).errorCode("customer-onboarding-error-credit-card-creation-failed")
                    .errorMessage("Person is suspicious. Delegate to backoffice.")
                    .variables(Map.of("creditCardOutput", creditCardOutput))
                    .send()
                    .join();
        } else {
            client.newCompleteCommand(activatedJob.getKey())
                    .variables(Map.of("creditCardOutput", creditCardOutput))
                    .send()
                    .join();
            log.info("CreditCard created successfully.");
        }
    }

    @JobWorker(type="onboarding-process-error-handling")
    public void handleError(ActivatedJob activatedJob) {
        var errorCode = activatedJob.getVariablesAsType(CamundaProcessVariables.class)
                .errorCode();
        log.error("Error '{}' is triggered and handled", errorCode);
    }
}
