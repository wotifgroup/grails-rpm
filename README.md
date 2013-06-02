grails-rpm
==========

Create a configurable rpm from your grails artifacts.

## Quick start
Here's a sample rpm configuration that you can drop into Config.groovy:
```
rpm {
    appUser = "testApp"
    appGroup = "sg_testApp"
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
        prefixes: "/apps/test"
    ]
    preRemove = "rpm/scripts/preremove.sh"
    postInstall = "rpm/scripts/postinstall.sh"
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
Tweak it as needed. Then run:
```
grails rpm
```
to produce your rpm.

## Configuration
The rpm plugin works by reading from your grails configuration the property "rpm", which is an object defining the structure of the rpm you
wish to build. The rpm property is broken into the following sections:

### metaData
The metaData section is a map, allowing you to set any of the bean properties on [redline's Builder class](http://redline-rpm.org/apidocs/org/freecompany/redline/Builder.html).

### Pre/Post Scripts
You can specify shell scripts inside your grails project to run on post-install or pre-uninstall of the rpm:
```
preRemove = "rpm/scripts/preremove.sh"
postInstall = "rpm/scripts/postinstall.sh"
```

### packageInfo
packageInfo is a double which lets you specify the name and version of the installed package. See "Command-Line Arguments" below for how to set the release.

### platform
platform lets you specify the intended architecture and OS for your rpm.

### dependencies
dependencies is map of the installed packages (and their versions) that your rpm depends on.

### structure
structure is the actual layout of the content of the rpm. It specifies the files (and associated metadata) that the rpm will install onto the box as
a tree structure. At each node (either a file or a directory) in the tree, you can specify the permissions, user and group and [RPM directive](http://www.rpm.org/max-rpm/s1-rpm-inside-files-list-directives.html) for that
node. If you don't specify values for each node then it will assume the defaults:
- permissions: 775
- user: "root"
- group: "root"
- RPM directive: none

## Command-Line Arguments
By default, the rpm will be named with the following form: appName-appVersion-date.noarch.rpm. You can optionally add a release
to the end of the name, which can be useful for CI servers to add their build number to:
```
grails rpm --release=123
```
would produce an rpm something like "testapp-1.0-2013.01.01_123.noarch.rpm".

Furthermore, you can completely override the name of the rpm like this:
```
grails rpm --name=hello
```
would produce the rpm: hello.noarch.rpm
