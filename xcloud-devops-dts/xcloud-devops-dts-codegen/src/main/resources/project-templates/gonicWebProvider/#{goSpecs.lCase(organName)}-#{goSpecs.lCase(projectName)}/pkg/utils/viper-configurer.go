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
	"errors"
	"fmt"
	"os"
	"reflect"
	"strings"

	"github.com/spf13/viper"
)

// FieldProcessor ...
type FieldProcessor func(fieldInfo reflect.StructField)

// ViperConfigurer ...
type ViperConfigurer struct {
	vp *viper.Viper
}

// NewViperConfigurer ...
func NewViperConfigurer() *ViperConfigurer {
	vp := viper.New()
	vp.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))
	vp.AllowEmptyEnv(false)

	// Enable auto population from environment variables (Priority: Set()/Flags/Config/Env/Default)
	// @see https://github.com/spf13/viper#working-with-environment-variables
	vp.AutomaticEnv()

	return &ViperConfigurer{vp: vp}
}

// SetConfigFile Sets config files @see: #SetConfig()
func (c *ViperConfigurer) SetConfigFile(defineFullConfigFile string, configFile string) error {
	// Check config files
	if defineFullConfigFile == "" || configFile == "" {
		return errors.New("DefineFullCofnigFile or configFile must not is empty")
	}
	if !(ExistsFileOrDir(defineFullConfigFile) && ExistsFileOrDir(configFile)) {
		errmsg := fmt.Sprintf("No such defineFullCofnigFile(%s) or configFile(%s)", defineFullConfigFile, configFile)
		return errors.New(errmsg)
	}

	c.vp.SetConfigFile(defineFullConfigFile)

	// Note: some strange logic problems are found in the test. After automaticenv() is enabled,
	// the parsing is based on the key defined in the configuration file??? For example: environment
	// variable: DATASOURCE.URI=uri111 (the default correct rule) must be configured at the same time
	// config.yml There are also: datasource.uri=uri222 In order to get the expected value of uri111,
	// otherwise, if the configuration file config.yml There is no definition datasource.uri You will not
	// be able to get the correct value of uri111 https://github.com/spf13/viper/blob/master/viper.go#L1904

	// Load parse the real runtime configuration file
	if cf, err := os.Open(configFile); err != nil {
		return err
	} else {
		c.vp.MergeConfig(cf)
		cf.Close()
	}

	return nil
}

// SetConfig Sets config @see: #SetConfigFile()
func (c *ViperConfigurer) SetConfig(defineFullConfigContent string, configFile string) error {
	tmpFullConfigName := "defineFullConfig.yaml"
	filename, err2 := CreateWriteTmpFile(tmpFullConfigName, defineFullConfigContent)
	if err2 != nil {
		return err2
	}
	return c.SetConfigFile(filename, configFile)
}

// SetEnvPrefix ...
func (c *ViperConfigurer) SetEnvPrefix(envPrefix string) {
	c.vp.SetEnvPrefix(envPrefix)
}

// Parse ...
func (c *ViperConfigurer) Parse(config interface{}) error {
	if err := c.vp.ReadInConfig(); err != nil {
		return err
	}
	if err := c.vp.Unmarshal(config); err != nil {
		return err
	}
	return nil
}

// GetViper ...
func (c *ViperConfigurer) GetViper() *viper.Viper {
	return c.vp
}
