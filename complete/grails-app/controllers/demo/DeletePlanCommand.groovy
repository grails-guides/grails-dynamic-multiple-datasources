package demo

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class SavePlanCommand implements Validateable {
    String title

    static constraints = {
        title nullable: false
    }
}