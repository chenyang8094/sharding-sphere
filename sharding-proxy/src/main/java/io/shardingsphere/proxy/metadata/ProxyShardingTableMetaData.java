/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.proxy.metadata;

import com.google.common.util.concurrent.ListeningExecutorService;
import io.shardingsphere.core.metadata.table.ColumnMetaData;
import io.shardingsphere.core.metadata.table.ShardingTableMetaData;
import io.shardingsphere.core.metadata.table.TableMetaData;
import io.shardingsphere.core.rule.DataNode;
import io.shardingsphere.proxy.backend.jdbc.datasource.JDBCBackendDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

/**
 * Sharding table meta data for proxy.
 *
 * @author panjuan
 */
public final class ProxyShardingTableMetaData extends ShardingTableMetaData {
    
    private final JDBCBackendDataSource backendDataSource;
    
    public ProxyShardingTableMetaData(final ListeningExecutorService executorService, final JDBCBackendDataSource backendDataSource) {
        super(executorService);
        this.backendDataSource = backendDataSource;
    }
    
    @Override
    protected Connection getConnection(final String dataSourceName) throws SQLException {
        return backendDataSource.getDataSource(dataSourceName).getConnection();
    }
    
    @Override
    public TableMetaData loadTableMetaData(final DataNode dataNode, final Map<String, Connection> connectionMap) throws SQLException {
        try (Connection connection = backendDataSource.getDataSource(dataNode.getDataSourceName()).getConnection()) {
            return new TableMetaData(isTableExist(connection, dataNode.getTableName()) ? getColumnMetaDataList(connection, dataNode.getTableName()) : Collections.<ColumnMetaData>emptyList());
        }
    }
}
