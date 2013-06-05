class RpmGrailsPlugin {
    def version = "0.10.0"
    def grailsVersion = "2.0 > *"

    def title = "Rpm Plugin"
    def description = '''\
Create an rpm from your grails artifacts, based configuration in Config.groovy.
'''

    def documentation = "https://github.com/aharwood/grails-rpm"


    def license = "APACHE"

    def developers = [ [ name: "Adam Harwood", email: "adamtroyh@gmail.com" ]]

    def issueManagement = [ system: "github", url: "https://github.com/aharwood/grails-rpm" ]

    def scm = [ url: "https://github.com/aharwood/grails-rpm" ]
}
