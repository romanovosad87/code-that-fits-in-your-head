<h1 align="center">📖 ➡️ 👨‍💻</h1>

This project is a practical implementation of the principles from Robert C. Martin's book ***"Code That Fits in Your Head"*** and will evolve as I continue reading.

This repository contains a serverless REST API built with **Java 21** and **Spring Boot 3**, deployed on **AWS Lambda** and triggered by **API Gateway**. 
The project was created with [aws-serverless-java-container](https://github.com/aws/serverless-java-container).

---

## Technology Stack 💻

* **Application**: Java `21`, Spring Boot `3`, Maven
* **AWS Cloud**: AWS Lambda, Amazon API Gateway, IAM
* **Infrastructure as Code**: Terraform
* **CI/CD Automation**: GitHub Actions
* **Code Quality**: SonarCloud

---

### 1. Infrastructure as Code (Terraform)

All AWS resources are managed declaratively in the `/terraform` directory.

* **Remote State**: Terraform state is stored in an S3 backend for team collaboration and use in the CI/CD pipeline.
* **Decoupled Lambda Code**: The `aws_lambda_function` resource in Terraform is configured to ignore changes to the application package. This allows a separate CI/CD pipeline to manage application code deployments without causing conflicts with Terraform.
    ```terraform
    # terraform/main.tf
    resource "aws_lambda_function" "codethatfits_lambda" {
      # ... other configurations
      lifecycle {
        ignore_changes = [
          filename,
          source_code_hash,
        ]
      }
    }
    ```

### 2. Dual CI/CD Pipelines 🤖

The repository uses two separate, trigger-specific GitHub Actions workflows to cleanly separate infrastructure and application concerns.

#### **Application Pipeline (`.github/workflows/ci_cd_pipeline.yaml`)**

This pipeline manages the Java application's lifecycle. It is triggered by pushes to `master` that are **not** in the `terraform/` directory.

1.  **Build & Test**: Compiles the code and runs all tests with `mvn clean verify`.
2.  **SonarCloud Analysis**: Performs static code analysis to check for bugs, vulnerabilities, and code smells.
3.  **Deploy to Lambda**: On a successful push to `master`, it packages the application into a shaded JAR and deploys it directly to the AWS Lambda function using the AWS CLI.

#### **Infrastructure Pipeline (`.github/workflows/terraform_pipeline.yaml`)**

This pipeline manages the AWS infrastructure. It is triggered by pushes or pull requests that modify files within the `terraform/` directory.

1.  **Terraform Plan**: On pull requests, a `terraform plan` is generated and posted as a PR comment for review.
2.  **Terraform Apply**: On a push to `master`, the pipeline automatically runs `terraform apply` to provision or update the infrastructure.

---

## Project Structure
```
.
├── .github/workflows/
│   ├── ci_cd_pipeline.yaml         # Application CI/CD Pipeline
│   └── terraform_pipeline.yaml     # Infrastructure CI/CD Pipeline
├── src/                            # Java application source code
├── terraform/
│   ├── backend.tf                  # Terraform S3 backend configuration
│   ├── main.tf                     # Core infrastructure definitions (Lambda, APIGW, IAM)
│   └── variables.tf                # Terraform variable definitions
└── pom.xml                         # Maven project configuration
```
---

## Setup and Deployment 🚀

### Prerequisites

* AWS Account.
* Terraform CLI (`~> 1.12.2`).
* Java 21.
* Apache Maven.

### Configuration

To enable the automated workflows, you must configure the following secrets in your GitHub repository settings under `Settings > Secrets and variables > Actions`:

* `AWS_ACCESS_KEY_ID`: Your AWS access key.
* `AWS_SECRET_ACCESS_KEY`: Your AWS secret key.
* `SONAR_TOKEN`: Your SonarCloud project token for analysis.
* `TF_VARS`: A multi-line secret containing the values for your Terraform variables.

  **Example `TF_VARS` content:**
    ```hcl
    aws_account_id                = "123456789012"
    lambda_role_name_suffix       = "your-unique-suffix"
    api_gateway_id                = "abcde12345"
    api_gateway_proxy_resource_id = "fghij6"
    api_gateway_deployment_id     = "klmno7"
    ```

### Deployment Workflow

The infrastructure must be created before the application can be deployed.

1.  **Deploy Infrastructure**: Make a commit and push to the `terraform/` directory on the `master` branch. The infrastructure pipeline (`terraform_pipeline.yaml`) will trigger and provision the necessary AWS resources.
2.  **Deploy Application**: Push a change to the Java application source code in `src/` to the `master` branch. The application pipeline (`ci_cd_pipeline.yaml`) will trigger, build the JAR, and deploy it to the Lambda function created in the previous step.

---

## Example API Endpoint 📡

The starter project defines a simple `/ping` resource that can accept `GET` requests with its tests. Once deployed, you can test it using a tool like `curl`:

```bash
$ curl https://<your-api-gateway-url>/default/ping

{
    "pong": "Hello, World!"
}
```
