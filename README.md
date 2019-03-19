# AWS BATCH  EXAMPLE


## Prerequisites:

	Install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html)
	Install [Docker](https://docs.docker.com/install/)
	Install [maven](Please follow instructions in this link: https://maven.apache.org/download.cgi)
	Setup either [Docker hub](https://hub.docker.com/signup)/[AWS ECR](https://docs.aws.amazon.com/AmazonECR/latest/userguide/get-set-up-for-amazon-ecr.html)
## Run the code

### Step 1: 
Download Code -> Open command Line -> go to the folder where you downloaded the code
Run command
```
 mvn clean package
 mkdir temp
 cp target/etl-1.0-SNAPSHOT.jar temp/
 cp Dockerfile temp/
 cd temp
 ```

### Step 2:
 1. Login yo your docker hub or [AWS ECR](https://docs.aws.amazon.com/AmazonECR/latest/userguide/ECR_AWSCLI.html)

docker build -t sentiment .
docker tag sentiment:latest your-ecr-repository
docker push your-ecr-repository

### Step3:

#### Build a compute Environment:
```
 1. Run aws batch create-compute-environment --generate-cli-skeleton  >> computeEnv.json 
 2. Fill in the values in computeEnv.json
  computeEnv.json should look like:

 {"computeEnvironmentName": "C4OnDemand",
    "type": "MANAGED",
    "state": "ENABLED",
    "computeResources": {
        "type": "EC2",
        "minvCpus": 0,
        "maxvCpus": 2,
        "desiredvCpus": 0,
        "instanceTypes": [
            "c4.large"
        ],
        "imageId": "",
        "subnets": [
            "subnet-0c96cc3be30e80b43"
        ],
        "securityGroupIds": [
            "sg-00a45f7d1b790586f"
        ],
        "ec2KeyPair": "XYZ",
        "instanceRole": "arn:aws:iam::XXX:instance-profile/ecs-instance-role",
        "tags": {
            "owner": "sunitm"
        }
    },
    "serviceRole": "arn:aws:iam::XXX:role/aws-batch-service-role"
}

 3. Runaws aws batch create-compute-environment --cli-input-json file://computeEnv.json --region ap-southeast-2
```
#### Build a Job Queue:
```
 1. Run aws batch create-job-queue --generate-cli-skeleton  >> queue.json 
 2. Fill in the values in queue.json

 queue.json should look like: 
 {
  "jobQueueName": "HighPriority",
  "state": "ENABLED",
  "priority": 10,
  "computeEnvironmentOrder": [
    {
      "order": 10,
      "computeEnvironment": "C4OnDemand"
    }
  ]
}

 3. Run aws batch create-job-queue --cli-input-json file://queue.json --region <aws-region>
```

#### Register a Job definition
```
 1. Run aws batch register-job-definition --generate-cli-skeleton  >> jobDef.json 
 2. Fill in the values in jobDef.json
 jobDef.json show look like:
 {
    "jobDefinitionName": "senti2",
    "type": "container",
    "parameters": {},
    "containerProperties": {
        "image": "00099.dkr.ecr.ap-southeast-2.amazonaws.com/sentiment",
        "vcpus": 1,
        "memory": 1024,
        "command": [
                    "java",
                    "-jar",
                    "batch.jar",
                    "AWS_BATCH_IP_SOURCE_BUCKET",
                    "source-ip-batch-cicd",
                    "AWS_BATCH_IP_SOURCE_BUCKET_KEY",
                    "22571a6aef63447b84bd812fcf155827.csv",
                    "AWS_BATCH_IP_DESTINATION_BUCKET",
                    "source-ip-batch-cicd",
                    "AWS_BATCH_IP_DESTINATION_KEY",
                    "sent.txt"
                    ],
        "jobRoleArn": "",
        "volumes": [],
        "environment": [],
        "mountPoints": [],
        "ulimits": []    
        },
    "retryStrategy": {
        "attempts": 1
    },
    "timeout": {
        "attemptDurationSeconds": 3000
    }
}    
3. aws batch register-job-definition --cli-input-json file://jobDef.json --region <aws-region>
```

#### Submit a Job
```
 aws batch submit-job --job-name example --job-queue HighPriority  --job-definition senti2   --region <aws-region>
```

## Note:
***Syntax : file://<Path to json file> In my case it is in the same directory.
"file://"  is necessary to avoid encoding issues that the awscli might give you.***









