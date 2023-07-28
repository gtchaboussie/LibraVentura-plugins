package fr.libraventura.trail

import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.service.ServiceUtil

Map libraCreateTrailTemplateFromImport() {
    Map result = ServiceUtil.returnSuccess()

    Map trailData = parameters.trailData as Map

    // TODO : validation

    // create header
    Map trailHeaderResult = run service: 'createWorkEffort', with: [
            workEffortTypeId  : TrailHelper.TRAIL_TEMPLATE_HEADER_WE_TYPE,
            currentStatusId   : 'WERV_PENDING', // waiting approval
            workEffortName    : trailData.name,
            createdDate       : UtilDateTime.nowTimestamp(),
            createdByUserLogin: parameters.userLogin.userLoginId
    ]

    String mainWEId = trailHeaderResult.workEffortId

    Map parking = trailData.parking as Map
    Map parkinAddress = parking.address as Map
    Map parkingResult = run service: 'createPostalAddress', with: [
            address1          : parkinAddress.address1,
            address2          : parkinAddress.address2,
            city              : parkinAddress.city,
            postalCode        : parkinAddress.postalCode,
            countryGeoId      : parkinAddress.contryGeoId,
            stateProvinceGeoId: parkinAddress.stateProvinceGeoId
    ]

    Map parkingPoint = parking.coordinates as Map
    Map parkingGeoResult = run service: 'createGeoPoint', with: [
            dataSourceId: TrailHelper.TRAIL_USER_IMPORT_SOURCE,
            latitude    : parkingPoint.latitude,
            longitude   : parkingPoint.longitude,
    ]

    run service: 'createWorkEffortGeoPoint', with: [
            workEffortId    : mainWEId,
            geoPurposeTypeId: TrailHelper.TRAIL_HEAD_PARKING_GEO_POINT_TYPE,
            geoPointId      : parkingGeoResult.geoPointId
    ]

    run service: 'createWorkEffortAttribute', with: [
            workEffortId   : mainWEId,
            attrName       : TrailHelper.TRAIL_SPORT_DIFFICULTY,
            attrValue      : trailData.sportive_difficulty,
            attrDescription: ''
    ]

    run service: 'createWorkEffortAttribute', with: [
            workEffortId   : mainWEId,
            attrName       : TrailHelper.TRAIL_ENIGMA_DIFFICULTY,
            attrValue      : trailData.enigma_difficulty,
            attrDescription: ''
    ]

    // TODO : add validation question

    (trailData.points as List<Map>).eachWithIndex { point, i ->
        String templTypeId = TrailHelper.TRAIL_TEMPLATE_STEP_WE_TYPE + '_' + i

        Map stepResult = run service: 'createWorkEffort', with: [
                workEffortTypeId  : templTypeId,
                workEffortParentId: mainWEId,
                workEffortName    : (trailData.name + i) as String,
                currentStatusId   : 'WERV_PENDING', // waiting approval
                createdDate       : UtilDateTime.nowTimestamp(),
                createdByUserLogin: parameters.userLogin.userLoginId
        ]

        Map pointResult = run service: 'createGeoPoint', with: [
                dataSourceId: TrailHelper.TRAIL_USER_IMPORT_SOURCE,
                latitude    : point.latitude,
                longitude   : point.longitude,
        ]

        run service: 'createWorkEffortGeoPoint', with: [
                workEffortId    : stepResult.workEffortId,
                geoPurposeTypeId: TrailHelper.TRAIL_STEP_GEO_POINT_TYPE,
                geoPointId      : pointResult.geoPointId
        ]
    }
    result.workEffortId = mainWEId
    return result
}
