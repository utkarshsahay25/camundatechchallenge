package com.techchallenge.customapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication("customapp")
public class CamundaApplication {
	
	    private static final String ZEEBE_ADDRESS = "4f2de3b5-9d69-4aff-a775-e47eef445126.syd-1.zeebe.camunda.io:443";
	    private static final String ZEEBE_CLIENT_ID = "63o~ocm8Vr9U_FlicsZHBxn1PdHKdp1h";
	    private static final String ZEEBE_CLIENT_SECRET = "~v5pWlW5REYAzxayKSuYD~8bAv5cU_9z9JnbK8x8w.jVHTNFmcAv7PEMclV6MRdI";
	    private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
	    private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";
	    private static final String BPMN_PROCESS_ID = "AnimalPictureProcessPID";

	    private final ZeebeClient client;
	    
	    public CamundaApplication() {
	        final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
	                .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL).audience(ZEEBE_TOKEN_AUDIENCE)
	                .clientId(ZEEBE_CLIENT_ID).clientSecret(ZEEBE_CLIENT_SECRET).build();

	        this.client = ZeebeClient.newClientBuilder().gatewayAddress(ZEEBE_ADDRESS)
	                .credentialsProvider(credentialsProvider).build();
	    }

  public static void main(String... args) {
    SpringApplication.run(CamundaApplication.class, args);
  }

  @RestController
  @RequestMapping("/api")
  public class AnimalController {

      @PostMapping("/deploy")
      public String deployWorkflow() {
          final DeploymentEvent deploymentEvent = client.newDeployResourceCommand()
                  .addResourceFromClasspath("AnimalPictureProcess.bpmn").send().join();
          return "Deployment created with key: " + deploymentEvent.getKey();
      }

      @PostMapping("/start")
      public String startProcessInstance() {
          final ProcessInstanceEvent processInstanceEvent = client.newCreateInstanceCommand()
                  .bpmnProcessId(BPMN_PROCESS_ID).latestVersion().send().join();
          return "Process instance created with key: " + processInstanceEvent.getProcessInstanceKey();
      }
  } 
}
