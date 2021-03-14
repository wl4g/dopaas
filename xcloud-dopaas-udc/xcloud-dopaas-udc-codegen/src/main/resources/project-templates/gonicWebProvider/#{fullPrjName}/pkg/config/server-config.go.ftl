// ${watermark}

${copyright}
package config

// ---------------------
// ${projectName?cap_first} server properties
// ---------------------

// ServerProperties ...
type ServerProperties struct {
	Listen   string             `yaml:"listen"`
	Cors     CorsProperties     `yaml:"cors"`
	// TODO ... Modules properties
}

// CorsProperties ...
type CorsProperties struct {
	AllowOrigins     string `yaml:"allow-origins"`
	AllowCredentials bool   `yaml:"allow-credentials"`
	AllowMethods     string `yaml:"allow-methods"`
	AllowHeaders     string `yaml:"allow-headers"`
	ExposeHeaders    string `yaml:"exposes-headers"`
	MaxAge           int    `yaml:"max-age"` // Seconds
}
