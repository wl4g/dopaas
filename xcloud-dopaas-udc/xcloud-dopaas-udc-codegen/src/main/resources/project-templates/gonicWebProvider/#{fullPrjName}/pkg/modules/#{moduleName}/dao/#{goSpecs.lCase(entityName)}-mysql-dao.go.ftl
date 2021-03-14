// ${watermark}

${copyright}
package service

import (
	"database/sql"
	"errors"
	"fmt"
	"strings"
	"time"
	"${organName}-${projectName}/pkg/config"
	"${organName}-${projectName}/pkg/logging"
	utils "${organName}-${projectName}/pkg/utils"

	"go.uber.org/zap"

	_ "github.com/go-sql-driver/mysql"
)

// MysqlStore ...
type MysqlStore struct {
	mysqlDB *sql.DB
}

// NewMysqlStore ...
func NewMysqlStore() (*MysqlStore, error) {
	mysqlConfig := config.GlobalConfig.DataSource.Mysql
	logging.Receive.Info("Connecting to MySQL of configuration: %s",
		zap.String("dbconnectstr", getDesensitizeWithMysqlConnectStr()),
		zap.Uint32("connMaxLifetimeSec", mysqlConfig.ConnMaxLifetimeSec),
		zap.Int("maxOpenConns", mysqlConfig.MaxOpenConns),
		zap.Int("maxIdleConns", mysqlConfig.MaxIdleConns))

	mydb, err := utils.OpenMysqlConnection(
		mysqlConfig.DbConnectStr,
		mysqlConfig.MaxOpenConns,
		mysqlConfig.MaxIdleConns,
		time.Duration(mysqlConfig.ConnMaxLifetimeSec)*time.Second,
	)
	//defer mysqlDB.Close(); // @see #Close()

	if err != nil {
		return nil, errors.New("Cannot connect to mysql. " + err.Error())
	}

	return &MysqlStore{
		mysqlDB: mydb,
	}, nil
}

// GetSessionByID find session info by id
func (that MysqlStore) GetSessionByID(id int64) *SessionBean {
	session := new(SessionBean)
	row := that.mysqlDB.QueryRow("select id,name,address,username,IFNULL(password, ''),IFNULL(ssh_key, '') from webconsole_session where id=?", id)
	if err := row.Scan(&session.ID, &session.Name, &session.Address, &session.Username, &session.Password, &session.SSHPrivateKey); err != nil {
		logging.Receive.Fatal("GetSessionById", zap.Error(err))
	}
	fmt.Println(session.ID, session.Name, session.Username)
	return session
}

// QuerySessionList ...
func (that MysqlStore) QuerySessionList() []SessionBean {
	// 通过切片存储
	sessions := make([]SessionBean, 0)
	rows, _ := that.mysqlDB.Query("SELECT id,name,address,username,IFNULL(password, ''),IFNULL(ssh_key, '') FROM `webconsole_session` limit ?", 100)

	// 遍历
	var session SessionBean
	for rows.Next() {
		rows.Scan(&session.ID, &session.Name, &session.Address, &session.Username, &session.Password, &session.SSHPrivateKey)
		sessions = append(sessions, session)
	}

	fmt.Println(sessions)
	return sessions
}

// SaveSession ...
func (that MysqlStore) SaveSession(session *SessionBean) int64 {
	ret, e := that.mysqlDB.Exec("insert INTO webconsole_session(name,address,username,password,ssh_key) values(?,?,?,?,?)", session.Name, session.Address, session.Username, session.Password, session.SSHPrivateKey)
	if nil != e {
		logging.Receive.Info("add fail", zap.Error(e))
		return 0
	}
	//影响行数
	rowsaffected, _ := ret.RowsAffected()
	id, _ := ret.LastInsertId()
	logging.Receive.Info("RowsAffected: %d", zap.Int64("RowsAffected", rowsaffected))
	return id
}

// DeleteSession ...
func (that MysqlStore) DeleteSession(ID int64) int64 {
	result, _ := that.mysqlDB.Exec("delete from webconsole_session where id=?", ID)
	rowsaffected, _ := result.RowsAffected()
	logging.Receive.Info("RowsAffected: %d", zap.Int64("RowsAffected", rowsaffected))

	return rowsaffected
}

// Close ...
func (that MysqlStore) Close() error {
	return that.mysqlDB.Close()
}

// Stat ...
func (that *MysqlStore) Stat() *Stat {
	return &Stat{
		DbConnectStr: getDesensitizeWithMysqlConnectStr(),
		ActiveConns:  that.mysqlDB.Stats().OpenConnections,
	}
}

func getDesensitizeWithMysqlConnectStr() string {
	connStr := config.GlobalConfig.DataSource.Mysql.DbConnectStr
	// Security key(password) desensitization
	colonIdx := strings.Index(connStr, ":")
	atIdx := strings.Index(connStr, "@tcp")
	connStr = connStr[0:colonIdx+1] + "******" + connStr[atIdx:len(connStr)]
	return connStr
}
