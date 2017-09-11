package demo

import grails.testing.mixin.integration.Integration
import org.grails.datastore.mapping.multitenancy.exceptions.TenantNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification

@Integration
class CurrentUserByJwtTenantResolverSpec extends Specification {

    @Autowired
    CurrentUserByJwtTenantResolver currentUserTenantResolver


    void "Test HttpHeader resolver throws an exception outside a web request"() {
        when:
        currentUserTenantResolver.resolveTenantIdentifier()

        then:
        def e = thrown(TenantNotFoundException)
        e.message == "Tenant could not be resolved outside a web request"
    }


    void "Test not tenant id found"() {
        setup:
        def request = new MockHttpServletRequest("GET", "/foo")
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request))

        when:
        currentUserTenantResolver.resolveTenantIdentifier()

        then:
        def e = thrown(TenantNotFoundException)
        e.message == "Tenant could not be resolved from HTTP Header: ${CurrentUserByJwtTenantResolver.HEADER_NAME}"

        cleanup:
        RequestContextHolder.setRequestAttributes(null)
    }

    void "Test HttpHeader value is the tenant id when a request is present"() {

        setup:
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo")
        request.addHeader('Authorization', 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiJINHNJQUFBQUFBQUFBSlZTdjA4VVFSUitleHhDZ2xFd2tjUUNHN0V6ZXdtV1Z3RkJBcGtjeHZNc01OSE03VDZXZ2RtWmRXYjJ1R3ZJVlZKUVFGUVNFbHBMXC9oTnRcL0FPTUZMVFV0THhaT1BhMElVNjFlZlB0OSt2TjZRV01XZ012RThPRnRHRW04MFNvMEdaR3FNUmlsQnZoZW1GdTBjVG9Dc1J5QVd6UkJLNVBVSUdBUVVYRURoNnhMZDdoTmNsVlVsdHJiMkhrNmwwRGM5b2tONHdiaHFlNG84MTJlTXNkYVlOXC9DWlRVd2ZjS2pLM0RGSThpblN2WDBHcXBtd21EOFRwTWxqT21vMjBcL2VoelJEU29udUxURDBERlV2QzB4WmpEQmM3ZXBTVldnZGZEdzJtenVoS3cxMGRVWmpHZmNXbkwzVDVLbTg5Yjl2YmVwS01FbjJJVnFOd3ZvVUhmUFBUVDBQT0dpbHBKU0M2M3NiRXVsT2hZYndvc1RmM1wvbXk2K0RrMzZyQWtDZHZMajduM0wrWkFINlB6NWNQaTJLRGlJSDAwUFdTMWk5bTVHYnFaTDVyVUd2XC9QdjQ5ZGVqaTczM0k2VHNFYVwvK2Z4K3o4emZOOVJaMW1uSERuUjdhRWRIdVZQMDNrU1wvY1RUN1lRaTlzaWpTVFNDOUtPWXh2SlVwaWlsczFXZzc2ZG5EXC96UnBiK3ZodWhiSDVsWVlmM090UWRtMUkrRUdSMnk4c1pKcld0WDkrK1BQZzJSOGlXWVhSRHBjNVV1MlRKYWlScDIwMG4wK1BaaWErbmUwWElRWVArZ3JPZUdRVkZBTUFBQT09Iiwic3ViIjoidmVjdG9yIiwicm9sZXMiOlsiUk9MRV9WSUxMQUlOIl0sImlhdCI6MTUwNDc4MTEyNX0.cf5rGNrNolchQ3QyMsPB544fwzYGiihBkRF8KU6soxc')
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request))


        when:
        def tenantId = currentUserTenantResolver.resolveTenantIdentifier()

        then:
        tenantId == "vector"

        cleanup:
        RequestContextHolder.setRequestAttributes(null)
    }
}

