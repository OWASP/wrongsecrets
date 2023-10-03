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

var START_SCRIPT = "./k8s-vault-gcp-start.sh"

func TestTerraformWrongSecretsGCP(t *testing.T) {
	t.Parallel()

	// Construct the terraform options with default retryable errors to handle the most common
	// retryable errors in terraform testing.
	terraformOptions := terraform.WithDefaultRetryableErrors(t, &terraform.Options{
		// The path to where our Terraform code is located
		TerraformDir: "../gcp",
		Vars: map[string]interface{}{
			"region":     "europe-west4",
			"project_id": "owasp-wrongsecrets",
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

	// Make an HTTP request to the instance and make sure we get back a 200 OK with the body "Hello, World!"
	challenge9 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil-9")
	challenge10 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil-10")
	challenge11 := fmt.Sprintf("http://%s:8080/%s", "localhost", "spoil-11")

	// Make an HTTP request to the instance and make sure we get back a 200 OK and don't see expected string. Retries for a while.
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge9, nil, 30, 5*time.Second, validateResponse)
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge10, nil, 30, 5*time.Second, validateResponse)
	http_helper.HttpGetWithRetryWithCustomValidation(t, challenge11, nil, 30, 5*time.Second, validateResponse)
}

func validateResponse(statusCode int, body string) bool {
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
