package demo

import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileStatic
import org.springframework.core.env.PropertyResolver

@CompileStatic
class DatabaseProvisioningService {

    @ReadOnly
    List<DatabaseConfiguration> findAllDatabaseConfiguration() {
        Role roleVillain = Role.where { authority == UserService.ROLE_VILLAIN }.get()
        UserRole.where { role == roleVillain }.list().collect { UserRole ur ->
            findDatabaseConfigurationByUsername(ur.user.username)
        } as List<DatabaseConfiguration>
    }

    static DatabaseConfiguration findDatabaseConfigurationByUsername(String username) {
        Map<String, Object> configuration = [
                'hibernate.hbm2ddl.auto':'none',
                'username': 'root',
                'password': 'root',
                'driverClassName': 'com.mysql.jdbc.Driver',
                'dialect': 'org.hibernate.dialect.MySQL5InnoDBDialect',
                'url':"jdbc:mysql://127.0.0.1:8889/$username"
        ] as Map<String, Object>
        new DatabaseConfiguration(dataSourceName: username, configuration: configuration)
    }
}

