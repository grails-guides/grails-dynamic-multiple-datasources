package demo

import groovy.transform.CompileStatic

@CompileStatic
class DatabaseProvisioningService {

    UserRoleService userRoleService

    List<DatabaseConfiguration> findAllDatabaseConfiguration() {
        List<String> usernames = userRoleService.findAllUsernameByAuthority(VillainService.ROLE_VILLAIN)
        usernames.collect { findDatabaseConfigurationByUsername(it) }
    }

    static DatabaseConfiguration findDatabaseConfigurationByUsername(String username) {
        new DatabaseConfiguration(dataSourceName: username, configuration: configurationByUsername(username))
    }

    static Map<String, Object> configurationByUsername(String username) {
        [
                'hibernate.hbm2ddl.auto':'none',
                'username': 'root',
                'password': 'root',
                'url':"jdbc:mysql://127.0.0.1:8889/$username"
        ] as Map<String, Object>
    }
}

