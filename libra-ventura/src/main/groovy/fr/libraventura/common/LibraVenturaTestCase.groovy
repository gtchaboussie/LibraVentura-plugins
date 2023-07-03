package fr.libraventura.common

import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.service.LocalDispatcher
import org.apache.ofbiz.service.testtools.OFBizTestCase

import java.sql.Timestamp

class LibraVenturaTestCase extends OFBizTestCase {

    public LibraVenturaMocker mock
    public final TimeZone timeZone = TimeZone.getDefault()
    public final Locale locale = Locale.getDefault()
    public final Timestamp now = UtilDateTime.nowTimestamp()

    LibraVenturaTestCase(String name) {
        super(name)
    }

    @Override
    void setDispatcher(LocalDispatcher dispatcher) {
        super.setDispatcher(dispatcher)
        mock = new LibraVenturaMocker(dispatcher)
    }
}
