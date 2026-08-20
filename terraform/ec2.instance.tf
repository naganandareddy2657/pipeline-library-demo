provider "aws" {
  region = "ap-south-2" # Set your desired AWS region
}

resource "aws_instance" "example" {
  ami           = "ami-024ebedf48d280810" # Specify an appropriate AMI ID
  instance_type = "t3.micro"
  tags = {
    Name = "Naga_k8s_cluster_demo"
  }
}

# Create an S3 bucket
# resource "aws_s3_bucket" "example" {
#   bucket = "example-bucket-ap-south-1-naga-demo"
# }

# resource "aws_s3_bucket_versioning" "versioning_example" {
#   bucket = aws_s3_bucket.example.id

#   versioning_configuration {
#     status = "Enabled"
#   }
# }
