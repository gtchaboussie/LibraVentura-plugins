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

    EntityQuery query() {
        return EntityQuery.use(delegator)
    }

    void testImportTrailFromJson() {
        String partyId = 'SOME_USER', name = 'JHON', lastName = 'DOE', userLoginId = 'jhondoe@test.org'

        mock.initTrailStaticData()
        String testName = this.name.replaceAll('test', '')
        String testFileLocation = System.getProperty('ofbiz.home') + '/plugins/libra-ventura/testdef/data/' + testName + '.json'
        JsonSlurper jsonSlurper = new JsonSlurper()
        Map trailData = jsonSlurper.parseText(new File(testFileLocation).text) as Map
        GenericValue user = mock.createLibraVenturaUser(partyId, name, lastName, userLoginId)

        Map importResult = dispatcher.runSync('libraCreateTrailTemplateFromImport', [
                userLogin  : user,
                *          : mock.defaultServiceContextMap,
                'trailData': trailData]
        )
        assert isSuccess(importResult)
        String headerId = importResult.workEffortId
        GenericValue templateHeader = query().from('WorkEffort')
                .where('workEffortId', headerId)
                .queryOne()

        assert 'No header found after import', templateHeader
        // assert parking point and address
        List<GenericValue> workEffParkingPoints = query().from('WorkEffortGeoPoint')
                .where('workEffortId', headerId, 'geoPurposeTypeId', TRAIL_HEAD_PARKING_GEO_POINT_TYPE)
                .queryList()
        assert workEffParkingPoints.size() == 1

        GenericValue workEffortParkingPoint = workEffParkingPoints[0]
        List<GenericValue> parkingPointsGeo = query().from('GeoPoint')
                .where('geoPointId', workEffortParkingPoint.geoPointId)
                .queryList()
        assert parkingPointsGeo.size() == 1

        GenericValue parkingGeo = parkingPointsGeo[0]
        assert parkingGeo.latitude
        assert parkingGeo.longitude

        // assert sportive and enigma difficulty

        // assert points

    }

}
