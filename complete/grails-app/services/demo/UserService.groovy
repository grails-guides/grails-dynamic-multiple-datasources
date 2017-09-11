package demo

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
interface IUserService {
    User save(String username, String password)
}

@Service(User)
@CompileStatic
abstract class UserService implements IUserService {

    @Transactional
    void deleteUser(User userParam) {
        UserRole.where { user == userParam }.deleteAll()
        userParam.delete()
    }
}
