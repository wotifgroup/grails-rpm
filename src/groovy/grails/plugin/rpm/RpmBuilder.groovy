package grails.plugin.rpm

import org.apache.commons.io.filefilter.WildcardFileFilter
import org.freecompany.redline.Builder
import org.freecompany.redline.header.Architecture
import org.freecompany.redline.header.Os
import org.freecompany.redline.header.RpmType
import org.freecompany.redline.payload.Directive

class RpmBuilder {

    def config
    String rpmName
    String rpmRelease
    Builder builder

    public RpmBuilder(def config, String rpmName, String rpmRelease) {
        this.config = config
        this.rpmName = rpmName
        this.rpmRelease = rpmRelease
        this.builder = new Builder()
    }

    public void build() {
        init()

        addScripts()

        addContent()

        writeRpm()
    }

    protected void addContent() {
        config.structure.each { nextDirectoryName, nextDirectoryStructure ->
            addDirectory("/$nextDirectoryName", nextDirectoryStructure)
        }
    }

    protected void addDirectory(String directoryPath, def directory) {
        int permissions = 0775
        String user = "root"
        String group = "root"
        Directive directoryDirective = Directive.NONE
        boolean hasAttributes = false
        directory.each {key, value ->
            if (key == "permissions") {
                permissions = value
                hasAttributes = true
            } else if (key == "user") {
                user = value
                hasAttributes = true
            } else if (key == "group") {
                group = value
                hasAttributes = true
            } else if (key == "directive") {
                directoryDirective = Directive[value]
                hasAttributes = true
            } else if (key == "files") {
                //ignore, will handle after
            } else {
                //must be another directory
                addDirectory("$directoryPath/$key", value)
            }
        }

        directory.files.each { fileName, fileInfo ->
            int filePermissions = fileInfo.permissions ?: 0775
            Directive fileDirective = fileInfo.directive ? Directive[fileInfo.directive] : Directive.NONE
            String fileUser = fileInfo.user ?: "root"
            String fileGroup = fileInfo.group ?: "group"
            File file = new File(".", fileName)
            println "Looking for $fileName"
            file.parentFile.listFiles((FilenameFilter) new WildcardFileFilter(file.name)).each { nextFile ->
                println "Adding file $nextFile.absolutePath"
                builder.addFile("$directoryPath/$nextFile.name",  nextFile, filePermissions, fileDirective, fileUser, fileGroup)
            }
        }

        if (hasAttributes) {
            println "Adding directory $directoryPath"
            builder.addDirectory(directoryPath,
                    permissions,
                    directoryDirective,
                    user,
                    group,
                    false)
        }
    }

    protected void addScripts() {
        if (config.preRemove) {
            builder.setPreUninstallScript(new File(config.preRemove).text)
        }
        if (config.postInstall) {
            builder.setPostInstallScript(new File(config.postInstall).text)
        }
    }

    protected void init() {
        initMetaData()

        if (!config.packageInfo) {
            throw new IllegalArgumentException("packageInfo must be specified")
        }
        builder.setPackage(config.packageInfo.name, config.packageInfo.version, this.rpmRelease)

        if (!config.platform) {
            throw new IllegalArgumentException("platform must be specified")
        }
        builder.setPlatform(Architecture.valueOf(config.platform.arch),
                            Os.valueOf(config.platform.osName))

        config.dependencies.each { nextDependency ->
            builder.addDependencyMore(nextDependency.key, nextDependency.value)
        }
    }

    protected void initMetaData() {
        if (!config.metaData) {
            config.metaData = [:]
        }

        //convert String constants to Enums
        if (config.metaData.type) {
            config.metaData.type = RpmType.valueOf(config.metaData.type)
        }

        //default src rpm
        if (!config.metaData.sourceRpm) {
            config.metaData.sourceRpm = "${rpmName}.src.rpm"
        }
        config.metaData.each { nextPropertyName, nextPropertyValue ->
            builder."$nextPropertyName" = nextPropertyValue
        }
    }

    protected void writeRpm() {
        String rpmFileName = "${rpmName}.noarch.rpm"
        String path = "target/${rpmFileName}"
        println "Building RPM: $path"
        File rpmTarget = new File("$path")
        def fos = new FileOutputStream(rpmTarget, false)
        builder.build(fos.getChannel())
        fos.close()
    }
}
