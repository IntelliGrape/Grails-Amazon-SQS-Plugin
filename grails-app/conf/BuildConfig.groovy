grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
//        excludes "httpclient:4.0"
//        excludes "httpcore"
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

//        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //        mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //        mavenRepo "http://download.java.net/maven/2/"
        //        mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        build 'org.apache.httpcomponents:httpcore:4.1.2'
        build 'org.apache.httpcomponents:httpclient:4.1.2'
        runtime 'org.apache.httpcomponents:httpcore:4.1.2'
        runtime 'org.apache.httpcomponents:httpclient:4.1.2'

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
                compile 'com.amazonaws:aws-java-sdk:1.2.15'
        //        compile 'org.apache.httpcomponents:httpcore:4.0.1', 'org.apache.httpcomponents:httpclient:4.0.1'
        //        compile 'commons-httpclient:commons-httpclient:3.1'
        //        compile 'org.apache.httpcomponents:httpclient:4.1.1' // for Amazon SQS
        //        compile 'com.amazonaws:aws-java-sdk:1.2.15'
    }

    plugins {
        build(":tomcat:$grailsVersion",
                ":release:1.0.0") {
            export = false
        }
    }
}
