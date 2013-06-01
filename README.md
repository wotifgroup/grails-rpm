grails-rpm
==========

Create a configurable rpm from your grails artifacts.

Example (in Config.groovy):
```
rpm {
    appUser = "testApp"
    appGroup = "sg_testApp"
    appDir = "/apps/test"
    preRemove = "rpm/scripts/preremove.sh"
    postInstall = "rpm/scripts/postinstall.sh"
    metaData = [
        vendor: "You",
        group: "Applications/Internet",
        description: "$appName RPM",
        packager: "Rpm Script",
        license: "Company. All rights reserved",
        summary: "$appName",
        url: "http://github.com/project?id=${argsMap.git_hash}",
        distribution: "(none)",
        buildHost: System.getProperty("HOSTNAME") ?: 'localhost',
        type: RpmType.BINARY,
        prefixes: rpm.appDir
    ]
    packageInfo = [name: appName, version: appVersion]
    platform = [arch: Architecture.NOARCH, osName: Os.LINUX]
    dependencies = [
        jdk: "1.7",
        curl: "7.0.0"
    ]
    structure = [
        apps: [
            test: [
                permissions: 775,
                user: rpm.appUser,
                group: rpm.appGroup,
                bin: [
                    permissions: 775,
                    files: [
                        "target/$appName*.jar": [
                            permissions: 744,
                            dirPermissions: 644
                        ]
                    ]
                ],
                etc: [
                    permissions: 775,
                    user: rpm.appUser,
                    group: rpm.appGroup,
                    files: [
                        "rpm/apps/test/etc/.keystorepassword": [
                            permissions: 644,
                            directive: org.freecompany.redline.payload.Directive.CONFIG
                        ]
                    ]
                ]
            ]
        ],
        etc: [
            logrotate: [
                files: [
                    "rpm/etc/logrotate.d/test": [
                        permissions: 644,
                        directive: org.freecompany.redline.payload.Directive.CONFIG
                    ]
                ]
            ]
        ]
    ]
}
```
