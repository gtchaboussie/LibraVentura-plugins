package fr.libraventura.trail

import fr.libraventura.common.LibraVenturaTestCase
import groovy.json.JsonSlurper
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.util.EntityQuery

import static fr.libraventura.trail.TrailHelper.*
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
        List<GenericValue> trails = EntityQuery.use(delegator).from("WorkEffort")
                .where('workEffortTypeId', TRAIL_TEMPLATE_HEADER_WE_TYPE)
                .queryList()
        assert trails.size() == 1
        GenericValue trail = trails[0]
        assert trail.workEffortId == importResult.workEffortId

        // assert parking

        // assert start point

        List<GenericValue> trailPoints = EntityQuery.use(delegator).from("WorkEffort")
                .where('workEffortParentId', trail.workEffortId, 'workEffortTypeId', TRAIL_TEMPLATE_STEP_WE_TYPE)
                .queryList()
        assert trailPoints.size() == 3

        // pour chaque point :
        // assert workEffort

        // assert geo
    }

}