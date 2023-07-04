package fr.libraventura.trail

import org.apache.ofbiz.service.ServiceUtil

Map libraCreateTrailTemplateFromImport() {
    Map result = ServiceUtil.returnSuccess()
    result.workEffortId = '12345'
    return result
}