grails.project.work.dir = "target"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global")
    log "warn"
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile("org.redline-rpm:redline:1.1.12") {
            exclude "ant"
        }
        compile("commons-io:commons-io:2.4")
    }

    plugins {
        build(':release:3.0.1', ':rest-client-builder:2.0.3') {
            export = false
        }
    }
}
