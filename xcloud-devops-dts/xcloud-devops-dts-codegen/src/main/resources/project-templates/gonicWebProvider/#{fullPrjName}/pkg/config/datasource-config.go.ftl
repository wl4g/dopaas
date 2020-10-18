// ${watermark}

${copyright}
package config

// ---------------------
// Repository(datasource) DAO properties
// ---------------------

// DataSourceProperties ...
type DataSourceProperties struct {
	Mysql MysqlProperties `yaml:"mysql"`
	Csv   CsvProperties   `yaml:"csv"`
}

// MysqlProperties ...
type MysqlProperties struct {
	// e.g: user:password@tcp(host:port)/database?charset=utf-8
	DbConnectStr       string `yaml:"dbconnectstr"`
	MaxOpenConns       int    `yaml:"max-open-conns"`
	MaxIdleConns       int    `yaml:"max-idle-conns"`
	ConnMaxLifetimeSec uint32 `yaml:"conn-max-lifetime-sec"` // Seconds
}

// CsvProperties ...
type CsvProperties struct {
	DataDir string `yaml:"data-dir"`
}

const (
	// DefaultCsvDataFile ...
	DefaultCsvDataFile = "webconsole.db.csv"
)
