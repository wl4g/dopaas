// ${watermark}

${copyright}
package service

import "testing"

func TestSessionSelect(t *testing.T) {
	// TODO
	// new(MysqlStore{}).QuerySessionList()
}

func TestGetSessionById(t *testing.T) {
	// TODO
	// new(MysqlStore{}).GetSessionByID(2)
}

func TestSaveSession(t *testing.T) {
	session := new(SessionBean)
	session.Name = "test1"
	session.Address = "10.0.0.160:30022"
	session.Username = "sshconsole"
	session.Password = "123456"
	session.SSHPrivateKey = ""

	// TODO
	// new(MysqlStore{}).SaveSession(session)
}
