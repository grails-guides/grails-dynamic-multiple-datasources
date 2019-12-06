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
        when:
        User vector = villainService.saveVillain('vector', 'secret')

        then:
        hibernateDatastore.connectionSources.size() == old(hibernateDatastore.connectionSources.size()) + 1 // <1>

        when:
        User gru = villainService.saveVillain('gru', 'secret')

        then:
        hibernateDatastore.connectionSources.size() == old(hibernateDatastore.connectionSources.size()) + 1 // <1>

        Tenants.withId("gru") {
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
        Tenants.withId("gru") {
            planService.deleteByTitle('Steal the Moon')
        }
        Tenants.withId("vector") {
            planService.deleteByTitle('Steal a Pyramid')
        }
        userService.deleteUser(gru)
        userService.deleteUser(vector)
        roleService.delete(VillainService.ROLE_VILLAIN)
    }
}
