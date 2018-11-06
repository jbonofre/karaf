/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.main.lock;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.felix.utils.properties.Properties;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public class OracleJDBCLockIntegrationTest extends BaseJDBCLockIntegrationTest {

    @Before
    @Override
    public void setUp() throws Exception {
        password = "root";
        driver = "oracle.jdbc.driver.OracleDriver";
        url = "jdbc:oracle:thin:@172.16.16.133:1521:XE";
        momentDatatype = "NUMBER(20)";

        super.setUp();
    }

    OracleJDBCLock createLock(Properties props) {
        return new OracleJDBCLock(props);
    }

    @Override
    Connection lock(String table, String node) throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConnection(url, user, password);
            statement = connection.createStatement();
            statement.execute("SELECT * FROM " + table + " FOR UPDATE");
        } finally {
            close(statement);
            // connection must not be closed!
        }

        return connection;
    }
}
