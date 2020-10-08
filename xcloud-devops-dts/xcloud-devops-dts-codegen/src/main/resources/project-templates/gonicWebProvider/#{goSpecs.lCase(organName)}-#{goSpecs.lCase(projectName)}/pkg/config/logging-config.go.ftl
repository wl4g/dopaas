// ${watermark}

${copyright}
package config

// ---------------------
// Logging properties
// ---------------------

// LoggingProperties Multi log properties.
type LoggingProperties struct {
	DateFormatPattern string                       `yaml:"date-format-pattern"`
	LogItems          map[string]LogItemProperties `yaml:"items"`
}

// LogItemProperties LogItem properties.
type LogItemProperties struct {
	FileName string           `yaml:"file"`
	Level    string           `yaml:"level"`
	Policy   PolicyProperties `yaml:"policy"`
}

// PolicyProperties Logging archive policy.
type PolicyProperties struct {
	RetentionDays int `yaml:"retention-days"`
	MaxBackups    int `yaml:"max-backups"`
	MaxSize       int `yaml:"max-size"`
}

const (
	// -------------------------------
	// Log constants.
	// -------------------------------

	// DefaultLogMain Default log service 'main'.
	DefaultLogMain = "main"

	// DefaultLogReceive Default log service 'receive'.
	DefaultLogReceive = "receive"
)
