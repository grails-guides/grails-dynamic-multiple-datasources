package demo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class PlanController {
    static allowedMethods = [index: 'GET', save: 'POST', delete: 'DELETE']

    PlanService planService

    def index() {
        List<Plan> planList = planService.findAll()
        [planList: planList]
    }

    def save(SavePlanCommand cmd) {
        if ( cmd.hasErrors() ) {
            render status: 422
            return
        }

        planService.save(cmd.title)
        render status: 201
    }

    def delete(String title) {
        planService.deleteByTitle(title)
        render status: 204
    }
}
