package fr.libraventura.trail

import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.service.ServiceUtil

Map libraCreateTrailTemplateFromImport() {
    Map result = ServiceUtil.returnSuccess()

    Map trailData = parameters.trailData as Map

    // create header
    Map trailHeaderResult = run service: 'createWorkEffort', with: [
            workEffortTypeId       : TrailHelper.TRAIL_TEMPLATE_HEADER_WE_TYPE,
//            currentStatusId        : '',
//            workEffortPurposeTypeId: '',
            workEffortName: trailData.name,
            createdDate: UtilDateTime.nowTimestamp(),
            createdByUserLogin: parameters.userLogin.userLoginId
    ]

    def foo = 'bar'
    // assoc parking address

    // assoc parking point

    // assoc starting point

    // assoc difficulty

    // assoc accessibility




    return result
}