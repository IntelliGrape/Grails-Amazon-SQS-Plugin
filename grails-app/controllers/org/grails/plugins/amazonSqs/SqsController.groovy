package org.grails.plugins.amazonSqs

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SqsController {

    def sqsService


    def test = {
        println ConfigurationHolder.config.grails.amazon.accessKey
        println ConfigurationHolder.config.grails.amazon.secretKey
        sqsService.setSqsEndPoint("us-west-2")
        render "Success"
    }

    def createQueue() {
        String region = "us-west-2"
        String queueName = "Amazon-Sqs-Test-2"
        Long timeOut = 60 * 40
        String queueUrl = sqsService.createQueue(region, queueName + "40min-time-out", timeOut)
        println queueUrl
        queueUrl = sqsService.createQueue(region, queueName + "default30-time-out")
        println queueUrl
        render "Success"
    }

    def deleteQueue() {
        String region = "us-west-2"
        String queueName = "Amazon-Sqs-Test-Queue2"
        sqsService.deleteQueue(region, queueName)
        render "Success"
    }

    def sendMessage() {
        String region = "us-west-2"
        String queueName = "Amazon-Sqs-Test-Queue2"
        String message = "This is a test message"
        sqsService.sendMessage(region, message, queueName)
        render "Success"
    }


    def getMessages() {
        String region = "us-west-2"
        String queueName = "Amazon-Sqs-Test-Queue2"
        def message = sqsService.getMessages(region, queueName)
        println message
        render "Success"
    }

    def deleteMessage() {
        String region = "us-west-2"
        String queueName = "Amazon-Sqs-Test-Queue2"
        def message = sqsService.getMessage(region, queueName)
        sqsService.deleteMessage(message, region, queueName)
        render "Success"
    }
}
