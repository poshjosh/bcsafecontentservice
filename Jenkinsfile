#!/usr/bin/env groovy
/**
 * https://github.com/poshjosh/bcsafecontentservice
 */
library(
    identifier: 'jenkins-shared-library@master',
    retriever: modernSCM(
        [
            $class: 'GitSCMSource',
            remote: 'https://github.com/poshjosh/jenkins-shared-library.git'
        ]
    )
)

pipelineForJavaSpringBoot(
        appPort : '8093',
        appEndpoint : '/actuator/health',
        mainClass : 'com.bc.safecontent.SafeContentApplication'
)
