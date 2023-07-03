package fr.libraventura.trail

import fr.libraventura.common.LibraVenturaTestCase
import groovy.json.JsonSlurper

import static org.apache.ofbiz.service.ServiceUtil.isSuccess

/**
 * ./gradlew "ofbiz -t component=libra-ventura -t suitename=LibraVenturatests -t case=trail-import-test"
 * ./gradlew "ofbiz -t component=libra-ventura -t suitename=LibraVenturatests -t case=trail-import-test" --debug-jvm
 */
class TrailImportTest extends LibraVenturaTestCase {

    TrailImportTest(String name) {
        super(name)
    }

    void testImportTrailFromJson() {
        mock.initTrailStaticData()
        String testName = this.name.replaceAll('test', '')
        String testFileLocation = System.getProperty('ofbiz.home') + '/plugins/libra-ventura/testdef/data/' + testName + '.json'
        JsonSlurper jsonSlurper = new JsonSlurper()
        Map trailData = jsonSlurper.parseText(new File(testFileLocation).text) as Map

        Map importResult = dispatcher.runSync('libraCreateTrailTemplateFromImport', [
                *          : mock.defaultServiceContextMap,
                'trailData': trailData]
        )
        assert isSuccess(importResult)

        // assert WorkEffort trail header
        // assert header a bien une addresse et un point d'interet


        // pour chaque point :
        // assert workEffort

        // assert geo
    }

}