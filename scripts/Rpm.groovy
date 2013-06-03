includeTargets << grailsScript('_GrailsBootstrap')

target(rpmMain: "Build the application RPM") {
    depends(configureProxy, classpath, loadApp)

    //have to invoke this via reflection to work around gant classpathing issues
    def rpmBuilder = classLoader.loadClass('grails.plugin.rpm.RpmBuilder').newInstance(buildSettings.config.rpm, rpmName, argsMap.release ?: "")
    rpmBuilder.build()

    println "Complete"
}

def getRpmName() {
    def rpmName = argsMap.name
    if (!rpmName) {
        String appName = metadata['app.name']
        String appVersion = metadata['app.version']
        String appRelease = new Date().format("yyyy.MM.dd")
        String appBuildNumber = argsMap.release
        if (appBuildNumber) {
            appRelease += "_" + appBuildNumber
        }
        rpmName = "$appName-$appVersion-$appRelease"
    }
    rpmName
}

setDefaultTarget(rpmMain)
