package demo

import groovy.transform.CompileStatic

@CompileStatic
class PlanController {
    PlanService planService

    def index() {
        [planList: planService.findAll()]
    }
}
