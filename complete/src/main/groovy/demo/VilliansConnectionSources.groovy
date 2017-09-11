package demo

import groovy.transform.CompileStatic
import org.grails.datastore.mapping.core.DatastoreUtils
import org.grails.datastore.mapping.core.connections.AbstractConnectionSources
import org.grails.datastore.mapping.core.connections.ConnectionSource
import org.grails.datastore.mapping.core.connections.ConnectionSourceFactory
import org.grails.datastore.mapping.core.connections.ConnectionSourceSettings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.PropertyResolver

import java.util.*
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the {@link ConnectionSources} interface. This implementation reads {@link ConnectionSource} implementations from configuration and stores them in-memory
 *
 * @author Sergio del Amo
 */
@CompileStatic
public class VilliansConnectionSources<T, S extends ConnectionSourceSettings> extends AbstractConnectionSources<T, S> {

    protected final Map<String, ConnectionSource<T, S>> connectionSourceMap = new ConcurrentHashMap<>()

    @Autowired
    DatabaseProvisioningService databaseProvisioningService

    public VilliansConnectionSources(ConnectionSource<T, S> defaultConnectionSource, ConnectionSourceFactory<T, S> connectionSourceFactory, PropertyResolver configuration) {
        super(defaultConnectionSource, connectionSourceFactory, configuration)
        this.connectionSourceMap.put(ConnectionSource.DEFAULT, defaultConnectionSource)

        for (DatabaseConfiguration databaseConfiguration : databaseProvisioningService.findAllDatabaseConfiguration() ) {
            if (databaseConfiguration.dataSourceName == "dataSource") continue // data source is reserved name for the default
            ConnectionSource<T, S> connectionSource = connectionSourceFactory.create(databaseConfiguration.dataSourceName,
                    DatastoreUtils.createPropertyResolver(databaseConfiguration.configuration),
                    defaultConnectionSource.getSettings())
            if(connectionSource != null) {
                this.connectionSourceMap.put(databaseConfiguration.dataSourceName, connectionSource)
            }
        }
    }

    @Override
    public Iterable<ConnectionSource<T, S>> getAllConnectionSources() {
        return Collections.unmodifiableCollection(this.connectionSourceMap.values())
    }

    @Override
    public ConnectionSource<T, S> getConnectionSource(String name) {
        return this.connectionSourceMap.get(name)
    }

    @Override
    public ConnectionSource<T, S> addConnectionSource(String name, PropertyResolver configuration) {
        if(name == null) {
            throw new IllegalArgumentException("Argument [name] cannot be null")
        }
        if(configuration == null) {
            throw new IllegalArgumentException("Argument [configuration] cannot be null")
        }

        ConnectionSource<T, S> connectionSource = connectionSourceFactory.createRuntime(name, configuration, (S)this.defaultConnectionSource.getSettings())
        if(connectionSource == null) {
            throw new IllegalStateException("ConnectionSource factory returned null")
        }
        this.connectionSourceMap.put(name, connectionSource)

        for(listener in listeners) {
            listener.newConnectionSource(connectionSource)
        }
        return connectionSource
    }


}
