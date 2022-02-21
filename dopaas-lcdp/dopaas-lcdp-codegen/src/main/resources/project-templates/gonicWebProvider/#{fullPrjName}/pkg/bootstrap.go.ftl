// ${watermark}

${copyright}
package main

import (
	"context"
	"flag"
)

var (
	${projectName?uncap_first} = &${projectName?cap_first}{}
)

func main() {
	var conf string
	// Pars configuration
	flag.StringVar(&conf, "c", "", "${projectName?cap_first} configuration path")
	flag.Parse()
	// flag.Usage()

	// Start server...
	${projectName?uncap_first}.StartServe(context.Background(), conf)
}
