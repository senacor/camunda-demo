package de.senacor.camundaDemo;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DoStuffWorker {

    private final ZeebeClient client;

    @GetMapping("/start")
    public void startProcess() {
        client.newCreateInstanceCommand()
            .bpmnProcessId("demoProcess")
            .latestVersion()
            .send().join();
    }

    @JobWorker
    public void doStuffWorker() {
        log.info("Doing stuff...");
    }
}
