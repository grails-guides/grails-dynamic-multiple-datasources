package demo

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
abstract class AbstractUserWithRoleService {
    RoleService roleService

    UserRoleService userRoleService

    UserService userService

    abstract String getAuthority()

    @Transactional
    User saveVillain(String username, String password) {
        Role role = roleService.findByAuthority(getAuthority())
        if ( !role ) {
            role = roleService.saveByAuthority(getAuthority())
        }
        User user = userService.save(username, password)
        userRoleService.save(user, role)
        user
    }
}