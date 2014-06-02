package org.grails.plugins.amazonSqs

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.*
import com.amazonaws.auth.BasicAWSCredentials
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SqsService {

    static transactional = false;
    public static AmazonSQS sqs
    public static String sqsEndPointUrl = "sqs.##REGION##.amazonaws.com"

    public SqsService() {
        //todo: remove configuration holder
        def config = ConfigurationHolder.config.grails.amazon
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(config.accessKey ?: '', config.secretKey ?: '')
        sqs = new AmazonSQSClient(basicAWSCredentials);
    }

    void setSqsEndPoint(String region) {
        sqs.setEndpoint(sqsEndPointUrl.replaceAll("##REGION##", region))
    }

    //todo  add one more parameer name region and  default value null
    String getQueueUrl(String queueName) throws AmazonServiceException, AmazonClientException {
        String queueUrl = null;
        try {
            List queueUrls = sqs.listQueues().getQueueUrls()
            if (queueUrls.find {it.contains(queueName)}) {
                queueUrl = queueUrls.find {it.contains(queueName)}
            }
        }
        catch (AmazonServiceException ase) {
            printLogsAndThrowAmazonServiceException(ase)
        } catch (AmazonClientException ace) {
            printLogsAndThrowAmazonClientException(ace)
        }
        return queueUrl
    }

    List<String> getAllQueueForRegion(String region) {
        setSqsEndPoint(region)
        List<String> queueUrl = null;
        try {
            List queueUrls = sqs.listQueues().getQueueUrls()
        }
        catch (AmazonServiceException ase) {
            printLogsAndThrowAmazonServiceException(ase)
        } catch (AmazonClientException ace) {
            printLogsAndThrowAmazonClientException(ace)
        }
        return queueUrl
    }

    String createQueue(String region, String queueName, Long timeoutInSeconds = ConfigurationHolder.config.grails.amazon.defaultQueueTimeOut) throws AmazonServiceException, AmazonClientException {
        setSqsEndPoint(region)
        String queueUrl = getQueueUrl(queueName);
        try {
            if (queueUrl) {
                log.info("Queue Already Exists with the name ${queueName}")
            } else {
                // Create a queue
                log.info("Creating a new SQS queue with the name ${queueName} endPoint:${region}")
                CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName)
                queueUrl = sqs.createQueue(createQueueRequest).getQueueUrl()
            }
            SetQueueAttributesRequest setQueueAttributeRequest = new SetQueueAttributesRequest(queueUrl, ['VisibilityTimeout': timeoutInSeconds.toString()])
            sqs.setQueueAttributes(setQueueAttributeRequest)
        }
        catch (AmazonServiceException ase) {
            printLogsAndThrowAmazonServiceException(ase)
        } catch (AmazonClientException ace) {
            printLogsAndThrowAmazonClientException(ace)
        }
        return queueUrl
    }

    void deleteQueue(String region, String queueName) {
        try {
            setSqsEndPoint(region)
            DeleteQueueRequest deleteQueueRequest = new DeleteQueueRequest(getQueueUrl(queueName));
            sqs.deleteQueue(deleteQueueRequest);
        }
        catch (AmazonServiceException ase) {
            printLogsAndThrowAmazonServiceException(ase)
        } catch (AmazonClientException ace) {
            printLogsAndThrowAmazonClientException(ace)
        }
    }

    private void printLogsAndThrowAmazonServiceException(AmazonServiceException ase) throws AmazonServiceException {
        log.error("Caught an AmazonServiceException, which means your request made it " +
                "to Amazon SQS, but was rejected with an error response for some reason.");
        log.error("Error Message:    " + ase.getMessage());
        log.error("HTTP Status Code: " + ase.getStatusCode());
        log.error("AWS Error Code:   " + ase.getErrorCode());
        log.error("Error Type:       " + ase.getErrorType());
        log.error("Request ID:       " + ase.getRequestId());
        throw ase
    }

    private void printLogsAndThrowAmazonClientException(AmazonClientException ace) throws AmazonClientException {
        log.error("Caught an AmazonClientException, which means the client encountered " +
                "a serious internal problem while trying to communicate with SQS, such as not " +
                "being able to access the network.");
        log.error("Error Message: " + ace.getMessage());
        throw ace
    }

    void sendMessage(String region, String message, String queueName) throws AmazonServiceException, AmazonClientException {
        try {
            setSqsEndPoint(region)
            String queueUrl = getQueueUrl(queueName)
            sqs.sendMessage(new SendMessageRequest(queueUrl, message))
        } catch (AmazonServiceException ase) {
            printLogsAndThrowAmazonServiceException(ase)
        } catch (AmazonClientException ace) {
            printLogsAndThrowAmazonClientException(ace)
        }
    }

    List<Message> getMessages(String region, String queueName) throws Exception {
        setSqsEndPoint(region)
        String queueUrl = getQueueUrl(queueName)
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(1)
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    Message getMessage(String region, String queueName) throws Exception {
        return getMessages(region, queueName)?.first()
    }

    List<Message> getMessagesFromQueueURL(String region, String queueUrl) {
        setSqsEndPoint(region)
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(1)
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages
    }

    Message getMessageFromQueueURL(String region, String queueUrl) throws Exception {
        return getMessages(region, queueUrl)?.first()
    }

    void deleteMessage(Message message, String queueName, String region) {
        try {
            if (message) {
                setSqsEndPoint(region)
                String queueUrl = getQueueUrl(queueName)
                String messageReceiptHandle = message.getReceiptHandle();
                sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
            }
            else {
                log.error "NO MESSAGE FOUND TO DELETE"
            }
        } catch (Throwable t) {
            log.error("CAUGHT AN EXCEPTION IN DELETING MESSAGE " + t.message);
//            t.printStackTrace();
        }
    }
}