package fr.libraventura.common


import org.apache.ofbiz.base.start.Start
import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.LocalDispatcher
import org.junit.Assert

import java.nio.file.Path
import java.sql.Timestamp

import static fr.libraventura.trail.TrailHelper.*

class LibraVenturaMocker extends Assert {

    public static final String module = LibraVenturaMocker.class.getName()
    private static Delegator delegator
    private static LocalDispatcher dispatcher
    public static GenericValue userSystem
    public static Map<String, Object> defaultServiceContextMap
    public static final String LIBRA_VENTURA_ADMIN_SECURITY_GROUP = 'LIBRA_VENTURA_ADMIN'

    private static Timestamp getTestValidDate() {
        return UtilDateTime.adjustTimestamp(UtilDateTime.nowTimestamp(), Calendar.HOUR, -1)
    }

    LibraVenturaMocker(LocalDispatcher ds) {
        dispatcher = ds
        delegator = ds.getDelegator()
        GenericValue localUserSystem = null
        try {
            localUserSystem = delegator.findOne('UserLogin', true, 'userLoginId', 'system')
        } catch (GenericEntityException ignored) {
        }
        userSystem = localUserSystem
        defaultServiceContextMap = [
                locale    : Locale.getDefault(),
                timeZone  : TimeZone.getDefault(),
                delegator : delegator,
                dispatcher: dispatcher]
    }

    LibraVenturaMocker(Delegator dg, LocalDispatcher ds) {
        delegator = dg
        dispatcher = ds
        GenericValue localUserSystem = null
        try {
            localUserSystem = delegator.findOne('UserLogin', true, 'userLoginId', 'system')
        } catch (GenericEntityException e) {
            Debug.logError(e, module)
            assert false
        }
        userSystem = localUserSystem;
        defaultServiceContextMap = [
                userLogin : userSystem,
                locale    : Locale.getDefault(),
                timeZone  : TimeZone.getDefault(),
                delegator : delegator,
                dispatcher: dispatcher]
    }

    static GenericValue safeCreateAndGet(String entityName, Object... fields) {
        try {
            return delegator.createOrStore(delegator.makeValue(entityName, fields));
        } catch (GenericEntityException e) {
            Debug.logError(e, module)
            fail('Error in safeCreate: ' + e.getMessage())
        }
        return null
    }

    static void safeCreate(String entityName, Object... fields) {
        try {
            delegator.createOrStore(delegator.makeValue(entityName, fields));
        } catch (GenericEntityException e) {
            Debug.logError(e, module)
            fail('Error in safeCreate: ' + e.getMessage())
        }
    }

    static void initTrailStaticData() {
        Path home = Start.getInstance().getConfig().getOfbizHome()

        List<String> standardOfbizPerm = [
                'PARTYMGR_ADMIN',
                'CONTENTMGR_ADMIN',
                'CONTENTMGR_ROLE_ADMIN',
                'PARTYMGR_CME_ADMIN',
                'WORKEFFORTMGR_ADMIN',
                'COMMON_ADMIN'
        ]
        List<String> libraVenturaSpecific = [
                LIBRA_VENTURA_ADMIN_SECURITY_GROUP
        ]

        String securityGroupId = LIBRA_VENTURA_ADMIN_SECURITY_GROUP
        safeCreate('SecurityGroup', 'groupId', securityGroupId)
        List<String> createAndAddPerms = []
        createAndAddPerms.addAll(standardOfbizPerm)
        createAndAddPerms.addAll(libraVenturaSpecific)
        for (String perm : createAndAddPerms) {
            safeCreate('SecurityPermission', 'permissionId', perm)
            safeCreate('SecurityGroupPermission', 'fromDate', getTestValidDate(), 'groupId', securityGroupId, 'permissionId', perm)
        }

        safeCreate('WorkEffortType', 'workEffortTypeId', TRAIL_TEMPLATE_WE_TYPE, 'hasTable', 'N',
                'description', 'Parent of work effort templates associated with trail',)
        safeCreate('WorkEffortType', 'workEffortTypeId', TRAIL_TEMPLATE_HEADER_WE_TYPE, 'hasTable', 'N',
                'description', 'Trail template header', 'parentTypeId', TRAIL_TEMPLATE_WE_TYPE)
        safeCreate('WorkEffortType', 'workEffortTypeId', TRAIL_TEMPLATE_STEP_WE_TYPE, 'hasTable', 'N',
                'description', 'Step of a trail template', 'parentTypeId', TRAIL_TEMPLATE_HEADER_WE_TYPE)
        for (def i = 0; i <= 30; i++) {
            safeCreate('WorkEffortType', 'workEffortTypeId', TRAIL_TEMPLATE_STEP_WE_TYPE + '_' + i, 'hasTable', 'N',
                    'description', 'Step of a trail template', 'parentTypeId', TRAIL_TEMPLATE_STEP_WE_TYPE)
        }
        safeCreate('GeoPurposeType', 'geoPurposeTypeId', TRAIL_HEAD_START_GEO_POINT_TYPE, 'hasTable', 'N',
                'description', 'Starting point of a trail')
        safeCreate('GeoPurposeType', 'geoPurposeTypeId', TRAIL_HEAD_PARKING_GEO_POINT_TYPE, 'hasTable', 'N',
                'description', 'Parking point of a trail')
        safeCreate('GeoPurposeType', 'geoPurposeTypeId', TRAIL_STEP_GEO_POINT_TYPE, 'hasTable', 'N',
                'description', 'Geo point of a trail step')

        safeCreate('DataSource', 'dataSourceId', TRAIL_USER_IMPORT_SOURCE, 'dataSourceTypeId', 'LEAD_SOURCE', 'description', 'User file import source')
    }

    /**
     * Libra standard user mocker
     * @return
     */
    static GenericValue createLibraVenturaUser(String partyId, String firstName, String lastName, String userLoginId) {
        GenericValue user
        safeCreate('Party', 'partyId', partyId, 'partyTypeId', 'PERSON')
        safeCreate('Person', 'partyId', partyId, 'firstName', firstName, 'lastName', lastName)
        user = safeCreateAndGet('UserLogin', 'partyId', partyId, 'userLoginId', userLoginId,
                'currentPassword', '$SHA$DwO3YNl$Kr0Dvet_B7oZjrec9UiOB2sBUY8', 'requirePasswordChange', 'N');
        safeCreate("UserLoginSecurityGroup", "userLoginId", userLoginId, "groupId", LIBRA_VENTURA_ADMIN_SECURITY_GROUP,
                "fromDate", getTestValidDate())
        return user
    }

}
