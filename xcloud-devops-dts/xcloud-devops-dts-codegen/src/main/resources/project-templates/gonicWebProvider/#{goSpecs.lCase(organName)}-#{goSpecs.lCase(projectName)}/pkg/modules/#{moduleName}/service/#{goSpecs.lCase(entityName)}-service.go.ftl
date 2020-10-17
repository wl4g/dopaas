// ${watermark}

${copyright}
package service

import (
	"io"
	"${organName}-${projectName}/pkg/logging"

	"go.uber.org/zap"
)

// SSH2Store ...
type SSH2Store interface {
	GetSessionByID(sessionID int64) *SessionBean
	QuerySessionList() []SessionBean
	SaveSession(session *SessionBean) int64
	DeleteSession(sessionID int64) int64
	io.Closer
	Stat() *Stat
}

// SessionBean User terminal session info bean
type SessionBean struct {
	ID            int64  `db:"id"`
	Name          string `db:"name"`
	Address       string `db:"address"`
	Username      string `db:"username"`
	Password      string `db:"password"`
	SSHPrivateKey string `db:"ssh_key"`
}

// --- Delegate Store. ---

// DelegateSSH2Store ...
type DelegateSSH2Store struct {
	orgin *SSH2Store
}

// Stat ...
type Stat struct {
	DbConnectStr string
	ActiveConns  int
}

var (
	singletonDelegate *DelegateSSH2Store
)

// GetDelegate Gets or create real store instance with orgin.
func GetDelegate() *DelegateSSH2Store {
	var err error
	if singletonDelegate == nil {
		singletonDelegate, err = newDelegateSSH2Store()
	}
	if err != nil {
		logging.Main.Panic("Unable get or create delegate store", zap.String("err", err.Error()))
		return nil
	}
	return singletonDelegate
}

func newDelegateSSH2Store() (*DelegateSSH2Store, error) {
	var orginStore SSH2Store
	switch 1 {
	case 1:
		if mysql, err1 := NewMysqlStore(); err1 == nil {
			orginStore = mysql
		} else {
			return nil, err1
		}
	default:
		if csv, err2 := NewCsvStore(); err2 == nil {
			orginStore = csv
		} else {
			return nil, err2
		}
	}
	return &DelegateSSH2Store{
		orgin: &orginStore,
	}, nil
}

// GetSessionByID ...
func (store *DelegateSSH2Store) GetSessionByID(sessionID int64) *SessionBean {
	return (*store.orgin).GetSessionByID(sessionID)
}

// QuerySessionList ...
func (store *DelegateSSH2Store) QuerySessionList() []SessionBean {
	return (*store.orgin).QuerySessionList()
}

// SaveSession ...
func (store *DelegateSSH2Store) SaveSession(session *SessionBean) int64 {
	return (*store.orgin).SaveSession(session)
}

// DeleteSession ...
func (store *DelegateSSH2Store) DeleteSession(sessionID int64) int64 {
	return (*store.orgin).DeleteSession(sessionID)
}

// Close ...
func (store *DelegateSSH2Store) Close() error {
	return (*store.orgin).Close()
}

// Stat ...
func (store *DelegateSSH2Store) Stat() *Stat {
	return (*store.orgin).Stat()
}
