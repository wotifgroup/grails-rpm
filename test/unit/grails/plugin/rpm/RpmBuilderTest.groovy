package grails.plugin.rpm

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.freecompany.redline.Builder
import org.freecompany.redline.header.Architecture
import org.freecompany.redline.header.Os
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
                type: RpmType.BINARY,
                prefixes: "/apps/test"
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

    def rpmBuilder(def config, def name = "test", String release = "") {
        if (!config.packageInfo) {
            config.packageInfo = [name: "testApp", version: "1.0"]
        }
        if (!config.platform) {
            config.platform = [arch: Architecture.NOARCH, osName: Os.LINUX]
        }
        new RpmBuilder([rpm: config], name, release)
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
