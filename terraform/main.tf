# main.tf
terraform {
  backend "s3" {
    bucket         = "code-that-fits-remote-state-terraform-bucket"
    key            = "default/terraform.tfstate"
    region         = "eu-central-1"
    use_lockfile   = true
  }
}

provider "aws" {
  region = "eu-central-1"
}

data "aws_caller_identity" "current" {}

################################################################################
# IAM Role & Policy Attachments for Lambda
################################################################################

resource "aws_iam_role" "lambda_exec_role" {
  name = "code-that-fits-role-${var.lambda_role_name_suffix}"
  path = "/service-role/"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [{
      Action    = "sts:AssumeRole",
      Effect    = "Allow",
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })

  tags = {
    ManagedBy = "Terraform"
  }
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/service-role/AWSLambdaBasicExecutionRole-6f97154d-694e-41c8-9420-7fd413c83dcd"
}

resource "aws_iam_role_policy_attachment" "lambda_vpc_access" {
  role       = aws_iam_role.lambda_exec_role.name
  policy_arn = "arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/service-role/AWSLambdaVPCAccessExecutionRole-bfe81e66-e3d8-4825-b0d3-51fcb2d5ba44"
}

################################################################################
# Lambda Function and Permission
################################################################################

resource "aws_lambda_function" "codethatfits_lambda" {
  function_name = "code-that-fits"
  handler       = "org.example.StreamLambdaHandler::handleRequest"
  runtime       = "java21"
  role          = aws_iam_role.lambda_exec_role.arn
  memory_size   = 512
  timeout       = 15

  # Create an empty "dummy.zip" file in your directory
  filename         = "dummy.zip"
  source_code_hash = filebase64sha256("dummy.zip")


  # Tell Terraform to ignore changes to the code package itself.
  lifecycle {
    ignore_changes = [
      filename,
      source_code_hash,
    ]
  }
}

resource "aws_lambda_permission" "apigw_lambda_permission" {
  statement_id  = "d49b0717-a3dc-5f54-b135-59cad934d647"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.codethatfits_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "arn:aws:execute-api:eu-central-1:${data.aws_caller_identity.current.account_id}:${var.api_gateway_id}/*/*/*"
}

################################################################################
# API Gateway
################################################################################

resource "aws_api_gateway_rest_api" "code_that_fits_api" {
  name        = "code-that-fits-API"
  description = "Created by AWS Lambda"
}

resource "aws_api_gateway_resource" "proxy_resource" {
  rest_api_id = aws_api_gateway_rest_api.code_that_fits_api.id
  parent_id   = aws_api_gateway_rest_api.code_that_fits_api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy_any_method" {
  rest_api_id   = aws_api_gateway_rest_api.code_that_fits_api.id
  resource_id   = aws_api_gateway_resource.proxy_resource.id
  http_method   = "ANY"
  authorization = "NONE"
  request_parameters = {
    "method.request.path.proxy" = true
  }
}

resource "aws_api_gateway_integration" "lambda_integration" {
  rest_api_id             = aws_api_gateway_rest_api.code_that_fits_api.id
  resource_id             = aws_api_gateway_resource.proxy_resource.id
  http_method             = aws_api_gateway_method.proxy_any_method.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.codethatfits_lambda.invoke_arn
  content_handling        = "CONVERT_TO_TEXT"
  request_parameters = {
    "integration.request.path.proxy" = "method.request.path.proxy"
  }
}

resource "aws_api_gateway_method" "proxy_options_method" {
  rest_api_id   = aws_api_gateway_rest_api.code_that_fits_api.id
  resource_id   = aws_api_gateway_resource.proxy_resource.id
  http_method   = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "options_integration" {
  rest_api_id = aws_api_gateway_rest_api.code_that_fits_api.id
  resource_id = aws_api_gateway_resource.proxy_resource.id
  http_method = aws_api_gateway_method.proxy_options_method.http_method
  type        = "MOCK"
  request_templates = {
    "application/json" = "{\"statusCode\": 200}"
  }
}

resource "aws_api_gateway_deployment" "main_deployment" {
  rest_api_id = aws_api_gateway_rest_api.code_that_fits_api.id
  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "default_stage" {
  deployment_id = aws_api_gateway_deployment.main_deployment.id
  rest_api_id   = aws_api_gateway_rest_api.code_that_fits_api.id
  stage_name    = "default"
  description   = "Created by AWS Lambda"
}

################################################################################
# IMPORT BLOCKS
################################################################################

import {
  to = aws_iam_role.lambda_exec_role
  id = "code-that-fits-role-${var.lambda_role_name_suffix}"
}
import {
  to = aws_iam_role_policy_attachment.lambda_basic_execution
  id = "code-that-fits-role-${var.lambda_role_name_suffix}/arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/service-role/AWSLambdaBasicExecutionRole-6f97154d-694e-41c8-9420-7fd413c83dcd"
}
import {
  to = aws_iam_role_policy_attachment.lambda_vpc_access
  id = "code-that-fits-role-${var.lambda_role_name_suffix}/arn:aws:iam::${data.aws_caller_identity.current.account_id}:policy/service-role/AWSLambdaVPCAccessExecutionRole-bfe81e66-e3d8-4825-b0d3-51fcb2d5ba44"
}
import {
  to = aws_lambda_function.codethatfits_lambda
  id = "code-that-fits"
}
import {
  to = aws_api_gateway_rest_api.code_that_fits_api
  id = var.api_gateway_id
}
import {
  to = aws_api_gateway_resource.proxy_resource
  id = "${var.api_gateway_id}/${var.api_gateway_proxy_resource_id}"
}
import {
  to = aws_api_gateway_method.proxy_any_method
  id = "${var.api_gateway_id}/${var.api_gateway_proxy_resource_id}/ANY"
}
import {
  to = aws_api_gateway_integration.lambda_integration
  id = "${var.api_gateway_id}/${var.api_gateway_proxy_resource_id}/ANY"
}
import {
  to = aws_api_gateway_method.proxy_options_method
  id = "${var.api_gateway_id}/${var.api_gateway_proxy_resource_id}/OPTIONS"
}
import {
  to = aws_api_gateway_integration.options_integration
  id = "${var.api_gateway_id}/${var.api_gateway_proxy_resource_id}/OPTIONS"
}
import {
  to = aws_api_gateway_deployment.main_deployment
  id = "${var.api_gateway_id}/${var.api_gateway_deployment_id}"
}
import {
  to = aws_api_gateway_stage.default_stage
  id = "${var.api_gateway_id}/default"
}
import {
  to = aws_lambda_permission.apigw_lambda_permission
  id = "code-that-fits/d49b0717-a3dc-5f54-b135-59cad934d647"
}