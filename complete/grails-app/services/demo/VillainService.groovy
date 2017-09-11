package demo

import groovy.transform.CompileStatic

@CompileStatic
class VillainService extends AbstractUserWithRoleService {

    public static final String ROLE_VILLAIN = 'ROLE_VILLAIN'

    @Override
    String getAuthority() {
        ROLE_VILLAIN
    }
}