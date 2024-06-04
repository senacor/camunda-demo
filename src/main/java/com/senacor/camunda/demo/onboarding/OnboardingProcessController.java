package com.senacor.camunda.demo.onboarding;

import com.senacor.camunda.demo.onboarding.generated.api.OnboardingProcessApi;
import com.senacor.camunda.demo.onboarding.generated.api.model.ApiMessage;
import com.senacor.camunda.demo.onboarding.generated.api.model.ApiOnboarding;
import com.senacor.camunda.demo.onboarding.generated.api.model.ApiStartOnboarding200Response;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

import static com.senacor.camunda.demo.onboarding.CamundaConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OnboardingProcessController implements OnboardingProcessApi {

    private final ZeebeClient client;

    @Override
    public ResponseEntity<ApiStartOnboarding200Response> startOnboarding(ApiOnboarding apiOnboarding) {
        log.info("Receive request to start main onboarding process");
        var onboardingId = UUID.randomUUID();
        var variables = Map.of(
                CAMUNDA_VARIABLES_ONBOARDING_ID, onboardingId,
                CAMUNDA_VARIABLES_ONBOARDING_REQUEST, apiOnboarding
        );
        var processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId(CAMUNDA_PROCESS_ID_ONBOARDING)
                .latestVersion()
                .variables(variables)
                .send()
                .join();
        var body = new ApiStartOnboarding200Response()
                .onboardingId(onboardingId)
                .processId(processInstance.getProcessInstanceKey());
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<Void> sendMessage(UUID onboardingId, String messageId, ApiMessage apiMessage) {
        log.info("Receive request to send a message to main onboarding process: correlationKey={}, messageId={}",
                onboardingId, messageId);
        client.newPublishMessageCommand()
                .messageName(messageId)
                .correlationKey(onboardingId.toString())
                .variable(CAMUNDA_VARIABLES_ONBOARDING_MESSAGE_RESULT, apiMessage)
                .send()
                .join();
        return ResponseEntity.noContent().build();
    }
}
