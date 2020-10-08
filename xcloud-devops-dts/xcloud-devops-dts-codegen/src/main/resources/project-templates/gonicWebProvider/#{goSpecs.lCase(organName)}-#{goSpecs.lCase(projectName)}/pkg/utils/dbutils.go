/**
 * Copyright 2017 ~ 2025 the original author or authors<Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils

import (
	"database/sql"
	"time"

	_ "github.com/go-sql-driver/mysql"
)

// OpenMysqlConnection ...
func OpenMysqlConnection(dbConnectStr string,
	maxOpenConns int, maxIdleConns int,
	connMaxLifetime time.Duration) (*sql.DB, error) {
	//fmt.Print("Connecting to mysql with dbConnectStr: %s, maxOpenConns: %s, maxIdleConns: %s, connMaxLifetime: %s", dbConnectStr, maxOpenConns, maxIdleConns, connMaxLifetime)

	mysqlDB, err1 := sql.Open("mysql", dbConnectStr)
	//defer mysqlDB.Close();
	if err1 != nil {
		// panic("Cannot connect to mysql. " + err1.Error())
		return nil, err1
	}

	mysqlDB.SetMaxOpenConns(maxOpenConns)
	mysqlDB.SetMaxIdleConns(maxIdleConns)
	mysqlDB.SetConnMaxLifetime(time.Duration(connMaxLifetime))

	if err2 := mysqlDB.Ping(); nil != err2 {
		// panic("Cannot connect to mysql. " + err2.Error())
		return nil, err2
	}

	return mysqlDB, nil
}
