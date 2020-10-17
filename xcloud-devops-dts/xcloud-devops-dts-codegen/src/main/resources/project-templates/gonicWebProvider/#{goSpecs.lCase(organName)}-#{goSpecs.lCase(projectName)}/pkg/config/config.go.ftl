// ${watermark}

${copyright}
package config

import (
	"fmt"

	utils "${organName}-${projectName}/pkg/utils"
)

// GlobalProperties ...
type GlobalProperties struct {
	Admin      AdminProperties      `yaml:"admin"`
	Server     ServerProperties     `yaml:"server"`
	DataSource DataSourceProperties `yaml:"datasource"`
	Logging    LoggingProperties    `yaml:"logging"`
}

var (
	// GlobalConfig ...
	GlobalConfig GlobalProperties
)

// InitGlobalConfig global config properties.
func InitGlobalConfig(path string) {
	// Check configuraion path
	if !utils.ExistsFileOrDir(path) {
		path = defaultConfPath // fallback use default
		if !utils.ExistsFileOrDir(path) {
			panic(fmt.Sprintf("No such configuration file '%s'", path))
		}
	}
	fmt.Printf("Load configuration file '%s'", path)

	// Create default config.
	globalConfig := createDefaultProperties()

	c := utils.NewViperConfigurer()
	c.SetConfig(defaultWebConsoleYamlConfigContent, path)
	c.SetEnvPrefix("WEBCONSOLE") // Sets auto use env variables prefix

	// Load & parse
	if err := c.Parse(globalConfig); err != nil {
		panic(err)
	}

	// conf, err := ioutil.ReadFile(path)
	// if err != nil {
	// 	fmt.Printf("Read config '%s' error! %s", path, err)
	// 	panic(err)
	// }

	// err = yaml.Unmarshal(conf, globalConfig)
	// if err != nil {
	// 	fmt.Printf("Unmarshal config '%s' error! %s", path, err)
	// 	panic(err)
	// }

	// Post properties.
	afterPropertiesSet(globalConfig)

	// Sets Global configuration
	GlobalConfig = *globalConfig
}

// Create default configuration properties.
func createDefaultProperties() *GlobalProperties {
	return &GlobalProperties{}
}

// MetricExclude settings after initialization
func afterPropertiesSet(globalConfig *GlobalProperties) {
}

// RefreshConfig Refresh global config.
func RefreshConfig(config *GlobalProperties) {
	utils.CopyProperties(&config, &GlobalConfig)
}

const (
	defaultConfPath = "/etc/webconsole.yml"
)
