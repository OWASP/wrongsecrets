package test

import (
	"fmt"
	"log"
	"os"
	"os/exec"
	"strings"
	"testing"
	"time"

	http_helper "github.com/gruntwork-io/terratest/modules/http-helper"

	"github.com/gruntwork-io/terratest/modules/terraform"
)

var START_SCRIPT = "./k8s-vault-azure-start.sh"

func TestTerraformWrongSecretsAzure(t *testing.T) {
	t.Parallel()

	// Construct the terraform options with default retryable errors to handle the most common
	// retryable errors in terraform testing.
	terraformOptions := terraform.WithDefaultRetryableErrors(t, &terraform.Options{
		// The path to where our Terraform code is located
		TerraformDir: "../azure",
		Vars: map[string]interface{}{
			"region": "East US",
		},
	})

	// At the end of the test, run `terraform destroy` to clean up any resources that were created.
	defer terraform.Destroy(t, terraformOptions)

	// Run `terraform init` and `terraform apply`. Fail the test if there are any errors.
	terraform.InitAndApply(t, terraformOptions)

	// Run `terraform output` to get the endpoint of the cluster
	// wrongsecretsClusterEndpoint := terraform.Output(t, terraformOptions, "cluster_endpoint")

	command := []string{
		START_SCRIPT,
	}
	execute(START_SCRIPT, command)
	log.Printf("Setup completed, now waiting 40 seconds to get all settled in")
	time.Sleep(time.Duration(40) * time.Second)

	// Make an HTTP request to the instance and make sure we get back a 200 OK with the body "Hello, World!"
	challenge7 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil/challenge-7")
	challenge9 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil/challenge-9")
	challenge10 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil/challenge-10")
	challenge11 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil/challenge-11")
	challenge47 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil/challenge-47")
	main := fmt.Sprintf("http://%s:8080", "localhost")

	// Make sure all challenges are enabled
	http_helper.HttpGetWithCustomValidation(t, main, nil, validateChallengesEnabled)

	// Make an HTTP request to the instance and make sure we get back a 200 OK and don't see expected string. Retries for a while.
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge7, nil, 30, 5*time.Second, validateCloudChallengeResponse)
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge9, nil, 30, 5*time.Second, validateCloudChallengeResponse)
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge10, nil, 30, 5*time.Second, validateCloudChallengeResponse)
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge11, nil, 30, 5*time.Second, validateCloudChallengeResponse)
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge47, nil, 30, 5*time.Second, validateCloudChallengeResponse)
}

func validateCloudChallengeResponse(statusCode int, body string) bool {
	// body should not contain if_you_see_this_please_use_AWS_Setup
	if strings.Contains(body, "if_you_see_this_please_use_AWS_Setup") {
		log.Printf("Found if_you_see_this_please_use_AWS_Setup in response body")
		return false
	}
	// body should not contain please_use_supported_cloud_env
	if strings.Contains(body, "please_use_supported_cloud_env") {
		log.Printf("Found please_use_supported_cloud_env in response body")
		return false
	}
	// body should not contain if_you_see_this_please_use_k8s
	if strings.Contains(body, "if_you_see_this_please_use_k8s") {
		log.Printf("Found if_you_see_this_please_use_k8s in response body")
		return false
	}
	// body should not contain if_you_see_this_please_use_K8S_and_Vault
	if strings.Contains(body, "if_you_see_this_please_use_K8S_and_Vault") {
		log.Printf("Found if_you_see_this_please_use_K8S_and_Vault in response body")
		return false
	}
	return statusCode == 200
}

func validateChallengesEnabled(statusCode int, body string) bool {
	// body should not contain class="disabled"
	if strings.Contains(body, "class=\"disabled\"") {
		log.Printf("Found class=\"disabled\" in response body")
		return false
	}

	return statusCode == 200
}

func execute(script string, command []string) (bool, error) {

	cmd := &exec.Cmd{
		Path:   script,
		Args:   command,
		Stdout: os.Stdout,
		Stderr: os.Stderr,
	}

	err := cmd.Start()
	if err != nil {
		return false, err
	}

	err = cmd.Wait()
	if err != nil {
		return false, err
	}

	return true, nil
}
