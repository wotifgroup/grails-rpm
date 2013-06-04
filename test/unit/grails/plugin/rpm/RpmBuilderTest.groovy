package grails.plugin.rpm

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.freecompany.redline.Builder
import org.freecompany.redline.header.RpmType
import org.junit.Test

@TestMixin(GrailsUnitTestMixin)
class RpmBuilderTest {

    @Test
    void metaDataShouldBeSetFromConfig() {
        def rpmBuilder = rpmBuilder([
            metaData: [
                vendor: "Test",
                group: "Applications/Internet",
                description: "testApp RPM",
                packager: "Rpm Script",
                license: "Apache",
                summary: "testApp",
                url: "http://github.com/project",
                distribution: "(none)",
                buildHost: "localhost",
                type: "BINARY",
                prefixes: "/apps/test",
                sourceRpm: "special.src.rpm"
            ]
        ])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.setVendor(1) {vendor -> assertEquals("Test", vendor)}
        mockBuilder.demand.setGroup(1) {vendor -> assertEquals("Applications/Internet", vendor)}
        mockBuilder.demand.setDescription(1) {vendor -> assertEquals("testApp RPM", vendor)}
        mockBuilder.demand.setPackager(1) {vendor -> assertEquals("Rpm Script", vendor)}
        mockBuilder.demand.setLicense(1) {vendor -> assertEquals("Apache", vendor)}
        mockBuilder.demand.setSummary(1) {vendor -> assertEquals("testApp", vendor)}
        mockBuilder.demand.setUrl(1) {vendor -> assertEquals("http://github.com/project", vendor)}
        mockBuilder.demand.setDistribution(1) {vendor -> assertEquals("(none)", vendor)}
        mockBuilder.demand.setBuildHost(1) {vendor -> assertEquals("localhost", vendor)}
        mockBuilder.demand.setType(1) {vendor -> assertEquals(RpmType.BINARY, vendor)}
        mockBuilder.demand.setPrefixes(1) {vendor -> assertEquals("/apps/test", vendor)}
        mockBuilder.demand.setSourceRpm(1) {vendor -> assertEquals("special.src.rpm", vendor)}
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    @Test
    void postInstallScriptShouldBeReadFromDisk() {
        def rpmBuilder = rpmBuilder([
            postInstall: "test/unit/resources/postInstallTest.sh"
        ])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.setPostInstallScript(1) {vendor -> assertEquals("hello", vendor)}
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    @Test
    void preRemoveScriptShouldBeReadFromDisk() {
        def rpmBuilder = rpmBuilder([
            preRemove: "test/unit/resources/preRemoveTest.sh"
        ])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.setPreUninstallScript(1) {vendor -> assertEquals("goodbye", vendor)}
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    @Test
    void dependenciesShouldBeSpecifiedAsMoreDependencies() {
        def rpmBuilder = rpmBuilder([
            dependencies: [
                jdk: "1.7",
                curl: "7.0.0"
            ]
        ])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.addDependencyMore(2) { packageName, version ->
            if (packageName == "jdk") {
                assertEquals("1.7", version)
            } else if (packageName == "curl") {
                assertEquals("7.0.0", version)
            } else {
                throw new IllegalArgumentException("Invalid dependency: $packageName")
            }
        }
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    @Test
    void nodesInStructureShouldBeSpecifiedAsDirectories() {
        def rpmBuilder = rpmBuilder([
            structure: [
                apps: [
                    test: []
                ],
                etc: []
            ]
        ])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.addDirectory(3) {}
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    @Test
    void filesInStructureShouldBeSpecifiedAsFiles() {
        def rpmBuilder = rpmBuilder([
                structure: [
                        apps: [
                                test: [
                                    files: [
                                        "test/unit/resources/testFile.txt": [:]
                                    ]
                                ]
                        ],
                        etc: [ files: ["test/unit/resources/testFile.txt": [:]]]
                ]
        ])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.addDirectory(3) {}
        mockBuilder.demand.addFile(2) {}
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    @Test
    void sourceRpmShouldDefaultIfNotSpecified() {
        def rpmBuilder = rpmBuilder([:])

        def mockBuilder = mockBuilder()
        mockBuilder.demand.setSourceRpm(1) {vendor -> assertEquals("test.src.rpm", vendor)}
        rpmBuilder.builder = mockBuilder.createMock()
        rpmBuilder.build()
        mockBuilder.verify()
    }

    def rpmBuilder(def config, def name = "test", String release = "") {
        if (!config.packageInfo) {
            config.packageInfo = [name: "testApp", version: "1.0"]
        }
        if (!config.platform) {
            config.platform = [arch: "NOARCH", osName: "LINUX"]
        }
        new RpmBuilder(config, name, release)
    }

    def mockBuilder() {
        def mockBuilder = mockFor(Builder, true)
        //set base expectations
        mockBuilder.demand.setPackage(1) {}
        mockBuilder.demand.setPlatform(1) {}
        mockBuilder.demand.build(1) {}
        mockBuilder
    }

}
