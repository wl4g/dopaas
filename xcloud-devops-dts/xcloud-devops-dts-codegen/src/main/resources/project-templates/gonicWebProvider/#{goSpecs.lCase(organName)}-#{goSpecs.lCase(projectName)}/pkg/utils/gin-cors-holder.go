/**
 * Copyright 2017 ~ 2025 the original author or author<Wanglsir@gmail.com, 983708408@qq.com>.
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
	"container/list"
	"net/http"
	"strconv"
	"strings"

	"github.com/gin-gonic/gin"
)

// CorsHolder CORS enhanced processor based on gin framework,
// such as: support for https://*.console.example.com Wildcard configuration.
type CorsHolder struct {
	AllowOrigins     []string `yaml:"allow-origins"`
	AllowCredentials bool     `yaml:"allow-credentials"`
	AllowMethods     []string `yaml:"allow-methods"`
	AllowHeaders     []string `yaml:"allow-headers"`
	ExposeHeaders    []string `yaml:"exposes-headers"`
	MaxAge           int      `yaml:"max-age"` // Seconds
}

// RegisterCorsProcessor ...
func (holder *CorsHolder) RegisterCorsProcessor(engine *gin.Engine) {
	engine.Use(holder.createCorsHandlerFunc())
}

// createCorsHandlerFunc ...
func (holder *CorsHolder) createCorsHandlerFunc() gin.HandlerFunc {
	return func(c *gin.Context) {
		method := c.Request.Method
		origin := c.Request.Header.Get("Origin")
		var headerNames []string
		for k := range c.Request.Header {
			headerNames = append(headerNames, k)
		}

		// Unconditional pass
		if method == "OPTIONS" {
			c.JSON(http.StatusOK, "Cors OPTIONS Request")
		}

		// Sets default access control policy for CORS requests
		if origin != "" {
			c.Header("Access-Control-Allow-Origin", holder.matchCorsOrigin(origin))
			c.Header("Access-Control-Allow-Credentials", strconv.FormatBool(holder.AllowCredentials))
			c.Header("Access-Control-Allow-Methods", holder.matchCorsMethod(method))
			c.Header("Access-Control-Allow-Headers", holder.matchCorsHeaders(headerNames))
			c.Header("Access-Control-Expose-Headers", JoinAll(holder.ExposeHeaders, ",")) // 跨域关键设置让浏览器可以解析
			c.Header("Access-Control-Max-Age", strconv.Itoa(holder.MaxAge))
		}

		// Execute the next handler.
		c.Next()
	}
}

// matchCorsOrigin ...
func (holder *CorsHolder) matchCorsOrigin(requestOrigin string) string {
	if requestOrigin == "" {
		return ""
	}
	if holder.AllowOrigins == nil || len(holder.AllowOrigins) <= 0 {
		return ""
	}

	if StringsContains(holder.AllowOrigins, AllAllow) {
		/**
		 * Note: Chrome will prompt: </br>
		 * The value of the 'Access-Control-Allow-Origin' header in the
		 * response must not be the wildcard '*' when the request's
		 * credentials mode is 'include'. The credentials mode of
		 * requests initiated by the XMLHttpRequest is controlled by the
		 * withCredentials attribute.
		 */
		if !holder.AllowCredentials {
			return AllAllow // Rejected
		} else {
			return requestOrigin
		}
	}

	for _, allowedOrigin := range holder.AllowOrigins {
		if strings.EqualFold(requestOrigin, allowedOrigin) {
			return requestOrigin
		}
		// e.g: allowedOrigin => "http://*.aa.mydomain.com"
		if IsSameWildcardOrigin(allowedOrigin, requestOrigin, true) {
			return requestOrigin
		}
	}
	return ""
}

// matchCorsHeaders ...
func (holder *CorsHolder) matchCorsHeaders(requestHeaders []string) string {
	if requestHeaders == nil || len(requestHeaders) <= 0 {
		return ""
	}
	if holder.AllowHeaders == nil || len(holder.AllowHeaders) <= 0 {
		return ""
	}

	allowAnyHeader := StringsContains(holder.AllowHeaders, AllAllow)
	result := list.New()
	for _, requestHeader := range requestHeaders {
		if requestHeader != "" {
			requestHeader = strings.TrimSpace(requestHeader)
			if allowAnyHeader {
				result.PushBack(requestHeader)
			} else {
				for _, allowedHeader := range holder.AllowHeaders {
					// e.g: allowedHeader => "X-Iam-*"
					if strings.Contains(allowedHeader, AllAllow) {
						allowedHeaderPrefix := allowedHeader[strings.Index(allowedHeader, AllAllow)+1 : len(allowedHeader)-1]
						if strings.EqualFold(requestHeader, allowedHeaderPrefix) {
							result.PushBack(requestHeader)
							break
						}
					} else if strings.EqualFold(requestHeader, allowedHeader) {
						result.PushBack(requestHeader)
						break
					}
				}
			}
		}
	}
	return JoinAll(ListToStringArray(result), ",")
}

// matchCorsMethod ...
func (holder *CorsHolder) matchCorsMethod(requestMethod string) string {
	if requestMethod == "" {
		return ""
	}
	if holder.AllowMethods == nil || len(holder.AllowMethods) <= 0 {
		return requestMethod
	}
	if StringsContains(holder.AllowMethods, requestMethod) {
		return JoinAll(holder.AllowMethods, ",")
	}
	return ""
}
