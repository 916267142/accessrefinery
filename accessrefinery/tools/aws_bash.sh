aws s3api put-bucket-policy --bucket antsknmath --policy file://policy.json
aws accessanalyzer start-resource-scan --analyzer-arn arn:aws:access-analyzer:ap-northeast-1:222634363387:analyzer/ExternalAccess --resource-arn arn:aws:s3:::antsknmath
aws accessanalyzer get-analyzed-resource --analyzer-arn arn:aws:access-analyzer:ap-northeast-1:222634363387:analyzer/ExternalAccess --resource-arn arn:aws:s3:::antsknmath
aws accessanalyzer list-findings --analyzer-arn "arn:aws:access-analyzer:ap-northeast-1:222634363387:analyzer/ExternalAccess" --filter '{"status": {"eq":["ACTIVE"]}}' > test.json