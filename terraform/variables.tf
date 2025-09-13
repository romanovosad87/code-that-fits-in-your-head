# --- Variables ---
variable "aws_account_id" {
  description = "The AWS Account ID to use for importing resources."
  type        = string
}

variable "lambda_role_name_suffix" {
  description = "The unique suffix for the Lambda execution role name."
  type        = string
}

variable "api_gateway_id" {
  description = "The ID of the API Gateway REST API."
  type        = string
}

variable "api_gateway_proxy_resource_id" {
  description = "The ID of the {proxy+} resource in API Gateway."
  type        = string
}

variable "api_gateway_deployment_id" {
  description = "The ID of the active API Gateway deployment."
  type        = string
}