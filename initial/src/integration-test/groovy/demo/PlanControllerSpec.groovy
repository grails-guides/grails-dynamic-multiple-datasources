package demo

import grails.gorm.multitenancy.Tenants
import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import org.grails.orm.hibernate.HibernateDatastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf( { System.getenv('TRAVIS') as boolean } )
@Integration
class PlanControllerSpec extends Specification {
    PlanService planService
    UserService userService
    VillainService villainService
    RoleService roleService

    @Autowired
    HibernateDatastore hibernateDatastore

    @Shared HttpClient client

    @OnceBefore
    void init() {
        String baseUrl = "http://localhost:$serverPort"
        this.client  = HttpClient.create(baseUrl.toURL())
    }

    String accessToken(String u, String p) {
        def resp = rest.post("http://localhost:${serverPort}/api/login") {
            accept('application/json')
            contentType('application/json')
            json {
                username = u
                password = p
            }
        }
        if ( resp.status == 200 ) {
            return resp.json.access_token
        }
        null
    }

    def "Plans for current logged user are retrieved"() {
        given:
        User vector = villainService.saveVillain('vector', 'secret')
        User gru = villainService.saveVillain('gru', 'secret')
        Tenants.withId("gru") { // <1>
            planService.save('Steal the Moon')
        }
        Tenants.withId("vector") {
            planService.save('Steal a Pyramid')
        }

        when: 'login with the gru'
        String gruAccessToken = accessToken('gru', 'secret')

        then:
        gruAccessToken

        when:
        HttpRequest request = HttpRequest.GET('/plan').bearerAuth(gruAccessToken)
        HttpResponse<String> resp = client.toBlocking().exchange(request, String)

        then:
        resp.status == 200
        resp.body() == '[{"title":"Steal the Moon"}]'

        when: 'login with the vector'
        String vectorAccessToken = accessToken('vector', 'secret')

        then:
        vectorAccessToken

        when:
        request = HttpRequest.GET('/plan').bearerAuth(vectorAccessToken)
        resp = client.toBlocking().exchange(request, String)


        then:
        resp.status == 200
        resp.body() == '[{"title":"Steal a Pyramid"}]'

        cleanup:
        Tenants.withId("gru") { // <1>
            planService.deleteByTitle('Steal the Moon')
        }
        Tenants.withId("vector") {
            planService.deleteByTitle('Steal the Pyramid')
        }
        userService.deleteUser(gru)
        userService.deleteUser(vector)
        roleService.delete(VillainService.ROLE_VILLAIN)
    }
}

