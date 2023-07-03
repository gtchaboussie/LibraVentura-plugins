package fr.libraventura.common

import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.entity.Delegator
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.LocalDispatcher
import org.junit.Assert

class LibraVenturaMocker extends Assert {

    public static final String module = LibraVenturaMocker.class.getName()
    private static Delegator delegator
    private static LocalDispatcher dispatcher
    public static GenericValue userSystem
    public static Map<String, Object> defaultServiceContextMap

    LibraVenturaMocker(LocalDispatcher ds) {
        dispatcher = ds
        delegator = ds.getDelegator()
        GenericValue localUserSystem = null
        try {
            localUserSystem = delegator.findOne("UserLogin", true, "userLoginId", "system")
        } catch (GenericEntityException ignored) {
        }
        userSystem = localUserSystem
        defaultServiceContextMap = [
                userLogin : userSystem,
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
            localUserSystem = delegator.findOne("UserLogin", true, "userLoginId", "system")
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


}