package demo

import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@Service(Plan) // <1>
@CurrentTenant // <2>
@CompileStatic
interface PlanService {
    List<Plan> findAll()
    Plan save(String title)
    void deleteByTitle(String title)
}
