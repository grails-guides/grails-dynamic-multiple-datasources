//tag::tenantResolverImport[]
import demo.CurrentUserByJwtTenantResolver
import demo.UserInsertedListener

//end::tenantResolverImport[]
//tag::passwordEncodingImport[]
import demo.UserPasswordEncoderListener
import demo.VilliansConnectionSources

//end::passwordEncodingImport[]
// Place your Spring DSL code here
//tag::beans[]
beans = {
//end::beans[]
//tag::passwordEncodingBean[]
    userPasswordEncoderListener(UserPasswordEncoderListener)
//end::passwordEncodingBean[]
//tag::tenantResolverBean[]
    currentUserByJwtTenantResolver(CurrentUserByJwtTenantResolver)
//end::tenantResolverBean[]
//tag::userInsertedListenerBean[]
    userInsertedListener(UserInsertedListener)
//end::userInsertedListenerBean[]
}
