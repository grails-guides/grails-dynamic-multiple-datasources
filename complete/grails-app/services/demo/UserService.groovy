package demo

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
class UserService {

    public static final ROLE_VILLAIN = 'ROLE_VILLAIN'

    @Transactional
    User saveVillain(String username, String password) {
        Role role = Role.where { authority == ROLE_VILLAIN }.get()
        if ( !role ) {
            role = new Role(authority: ROLE_VILLAIN)
            role.save(failOnError: true)
        }
        User user = new User(username: username, password: password)
        user.save(failOnError: true)
        UserRole userRole = new UserRole(user: user, role: role)
        userRole.save(failOnError: true)
        user
    }

    @Transactional
    void deleteUser(User u) {
        UserRole.where { user == u }.deleteAll()
        u?.delete()
    }
}
