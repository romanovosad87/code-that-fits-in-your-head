terraform {
  backend "s3" {
    bucket         = "code-that-fits-remote-state-terraform-bucket"
    key            = "default/terraform.tfstate"
    region         = "eu-central-1"
    use_lockfile   = true
  }
}
