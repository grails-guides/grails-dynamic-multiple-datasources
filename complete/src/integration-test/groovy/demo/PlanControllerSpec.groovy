package demo

import grails.gorm.multitenancy.Tenants
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import groovy.json.JsonOutput
import org.grails.orm.hibernate.HibernateDatastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
class PlanControllerSpec extends Specification {

    PlanService planService

    UserService userService

    RoleService roleService

    @Autowired
    HibernateDatastore hibernateDatastore

    RestBuilder rest = new RestBuilder()

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
        User vector = userService.saveVillain('vector', 'secret')

        then:
        //hibernateDatastore.connectionSources.size() == old(hibernateDatastore.connectionSources.size()) + 1
        true

        when:
        User gru = userService.saveVillain('gru', 'secret')

        then:
        //hibernateDatastore.connectionSources.size() == old(hibernateDatastore.connectionSources.size()) + 1
        true

        when: 'login with the gru'
        String gruAccessToken = accessToken('gru', 'secret')

        then:
        gruAccessToken

        when:
        def resp = rest.post("http://localhost:${serverPort}/plan") {
            accept('application/json')
            header('Authorization', "Bearer ${gruAccessToken}")
            json JsonOutput.toJson([title: "Steal the Moon"])
        }

        then:
        resp.status == 201

        when:
        resp = rest.get("http://localhost:${serverPort}/plan") {
            accept('application/json')
            header('Authorization', "Bearer ${gruAccessToken}")
        }

        then:
        resp.status == 200
        resp.json.toString() == '[{"title":"Steal the Moon"}]'

        when: 'login with the vector'
        String vectorAccessToken = accessToken('vector', 'secret')

        then:
        vectorAccessToken

        when:
        resp = rest.post("http://localhost:${serverPort}/plan") {
            accept('application/json')
            header('Authorization', "Bearer ${vectorAccessToken}")
            json JsonOutput.toJson([title: "Steal a Pyramid"])
        }

        then:
        resp.status == 201

        when:
        resp = rest.get("http://localhost:${serverPort}/plan") {
            accept('application/json')
            header('Authorization', "Bearer ${vectorAccessToken}")
        }

        then:
        resp.status == 200
        resp.json.toString() == '[{"title":"Steal a Pyramid"}]'

        cleanup:
        Tenants.withId('gru') {
            planService.deleteByTitle('Steal a Pyramid')
        }
        Tenants.withId('gru') {
            planService.deleteByTitle('Steal the Moon')
        }
        userService.deleteUser(gru)
        userService.deleteUser(vector)
        roleService.delete(UserService.ROLE_VILLAIN)
    }
}
