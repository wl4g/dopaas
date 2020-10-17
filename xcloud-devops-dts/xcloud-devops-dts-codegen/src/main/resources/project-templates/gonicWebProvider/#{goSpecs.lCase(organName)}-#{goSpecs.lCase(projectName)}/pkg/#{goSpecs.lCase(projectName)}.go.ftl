// ${watermark}

${copyright}
package main

import (
	"context"
	"strings"
	"time"
	admin "${organName}-${projectName}/pkg/admin"
	config "${organName}-${projectName}/pkg/config"
	logging "${organName}-${projectName}/pkg/logging"
<#if moduleMap?exists>
    <#list moduleMap?keys as moduleName>
        <#list moduleMap[moduleName] as table>
	${moduleName?lower_case} "${organName}-${projectName}/pkg/modules/${moduleName}"
        </#list>
    </#list>
</#if>
	"${organName}-${projectName}/pkg/utils"

	"go.uber.org/zap"

	ginzap "github.com/gin-contrib/zap"
	"github.com/gin-gonic/gin"
)

// ${projectName?cap_first} ...
type ${projectName?cap_first} struct {
	stopper *utils.Stopper
}

// StartServe ...
func (${projectName?uncap_first} *${projectName?cap_first}) StartServe(ctx context.Context, conf string) {
	${projectName?uncap_first}.stopper = utils.NewDefault(ctx, func() {
		logging.Main.Info("Stopping ...")
		// TODO Closing some resources gracefully
		// ...
	})

	// Init global config.
	config.InitGlobalConfig(conf)

	// Init zap logger.
	logging.InitZapLogger()

	// Start webserver...
	go ${projectName?uncap_first}.startWebServer()

	// Start admin server
	go admin.ServeStart()

	// Waiting for system exit
	${projectName?uncap_first}.stopper.WaitForExit()
}

// startWebServer ...
func (${projectName?uncap_first} *${projectName?cap_first}) startWebServer() *gin.Engine {
	logging.Main.Info("${projectName?cap_first} server starting...")

	// Create gin engine.
	engine := gin.New()

	// Sets gin runtim mode.
	// gin.SetMode(gin.ReleaseMode)

	// Sets gin http cors policy.
	corsConfig := config.GlobalConfig.Server.Cors
	corsHolder := utils.CorsHolder{
		AllowOrigins:     strings.Split(corsConfig.AllowOrigins, ","),
		AllowMethods:     strings.Split(corsConfig.AllowMethods, ","),
		AllowHeaders:     strings.Split(corsConfig.AllowHeaders, ","),
		AllowCredentials: corsConfig.AllowCredentials,
		ExposeHeaders:    strings.Split(corsConfig.ExposeHeaders, ","),
		MaxAge:           corsConfig.MaxAge,
	}
	corsHolder.RegisterCorsProcessor(engine)

	// Sets gin http other configuration.
	engine.Use(func(c *gin.Context) {
		c.Set("Content-Type", "application/json")
	})

	// Sets gin http logger.
	zapLogger := logging.Main.GetZapLogger()
	engine.Use(ginzap.Ginzap(zapLogger, time.RFC3339, true))
	engine.Use(ginzap.RecoveryWithZap(zapLogger, true))

	// Sets gin http handlers
	${projectName?uncap_first}.registerHTTPHandlers(engine)

	err := engine.Run(config.GlobalConfig.Server.Listen) // Default listen on 0.0.0.0:8080.
	if err != nil {
		logging.Receive.Panic("error", zap.Error(err))
	}

	return engine
}

// registerHTTPHandlers ...
func (${projectName?uncap_first} *${projectName?cap_first}) registerHTTPHandlers(engine *gin.Engine) {
	// Register all dispatch handlers.
<#if moduleMap?exists>
    <#list moduleMap?keys as moduleName>
        <#list moduleMap[moduleName] as table>
	engine.POST(${moduleName?lower_case}.Default${table.entityName?cap_first}APIAddURI, ${moduleName?lower_case}.Add${entityName?cap_first}Func)
	engine.POST(${moduleName?lower_case}.Default${table.entityName?cap_first}APIDeleteURI, ${moduleName?lower_case}.Delete${entityName?cap_first}Func)
	engine.POST(${moduleName?lower_case}.Default${table.entityName?cap_first}APIUpdateURI, ${moduleName?lower_case}.Update${entityName?cap_first}Func)
	engine.GET(${moduleName?lower_case}.Default${table.entityName?cap_first}APIQueryURI, ${moduleName?lower_case}.Query${entityName?cap_first}Func)
        </#list>
    </#list>
</#if>
}
