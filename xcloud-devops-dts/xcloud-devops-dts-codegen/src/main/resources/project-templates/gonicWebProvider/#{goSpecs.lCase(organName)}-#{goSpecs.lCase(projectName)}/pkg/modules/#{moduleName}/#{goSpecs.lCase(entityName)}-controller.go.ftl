// ${watermark}

${copyright}
package ssh2

import (
	"net/http"
	"strconv"
	service "${organName}-${projectName}/pkg/modules/${moduleName}/service"

	"github.com/gin-gonic/gin"
)

// Add${entityName?cap_first}Func ...
func Add${entityName?cap_first}Func(c *gin.Context) {
    // TODO (for example)
	//name := c.PostForm("name")
	//address := c.PostForm("address")
    //
	//session := new(service.SessionBean)
	//session.Name = name
	//session.Address = address
	//id := service.GetDelegate().SaveSession(session)
    //
	//c.JSON(http.StatusOK, gin.H{
	//	"status": "OK",
	//	"id":     id,
	//})
}

// Delete${entityName?cap_first}Func ...
func Delete${entityName?cap_first}Func(c *gin.Context) {
    // TODO (for example)
    //idStr := c.PostForm("id")
    //id, _ := strconv.ParseInt(idStr, 10, 64)
    //service.GetDelegate().DeleteSession(id)
    //c.JSON(http.StatusOK, gin.H{
    //  "status": "OK",
    //})
}

// Update${entityName?cap_first}Func ...
func Update${entityName?cap_first}Func(c *gin.Context) {
    // TODO (for example)
    //name := c.PostForm("name")
    //address := c.PostForm("address")
    // ...
}

// Query${entityName?cap_first}Func ...
func Query${entityName?cap_first}Func(c *gin.Context) {
    // TODO (for example)
	//sessions := service.GetDelegate().QuerySessionList()
	//c.JSON(http.StatusOK, gin.H{
	//	"status":   "OK",
	//	"sessions": sessions,
	//})
}


const (
	// Default${entityName?cap_first}APIBaseURI for base URI.
	Default${entityName?cap_first}APIBaseURI = "/${entityName?lower_case}/"

	// Default${entityName?cap_first}APIUpdateURI for update URI.
	Default${entityName?cap_first}APIUpdateURI = Default${entityName?cap_first}APIBaseURI + "update"

	// Default${entityName?cap_first}APIQueryURI for query URI.
	Default${entityName?cap_first}APIQueryURI = Default${entityName?cap_first}APIBaseURI + "list"

	// Default${entityName?cap_first}APIAddURI create URI.
	Default${entityName?cap_first}APIAddURI = Default${entityName?cap_first}APIBaseURI + "create"

	// Default${entityName?cap_first}APIDeleteURI for delete URI.
	Default${entityName?cap_first}APIDeleteURI = Default${entityName?cap_first}APIBaseURI + "delete"
)
