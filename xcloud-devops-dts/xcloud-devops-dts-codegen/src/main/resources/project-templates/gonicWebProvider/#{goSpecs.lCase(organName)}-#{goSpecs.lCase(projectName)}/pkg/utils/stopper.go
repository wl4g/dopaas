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
	"context"
	"os"
	"os/signal"
	"syscall"
)

// Stopper ...
type Stopper struct {
	sigs          []os.Signal
	cancelContext context.Context
	cancelFunc    context.CancelFunc
	handlers      []StopHandlerFunc
}

// StopHandlerFunc ...
type StopHandlerFunc func()

// NewDefault ...
func NewDefault(ctx context.Context, handlers ...StopHandlerFunc) *Stopper {
	// Default listening signals
	sigs := []os.Signal{syscall.SIGINT, syscall.SIGKILL, syscall.SIGTERM, syscall.SIGQUIT}
	stopper := &Stopper{sigs: sigs, handlers: handlers}
	stopper.cancelContext, stopper.cancelFunc = context.WithCancel(ctx)
	return stopper
}

// NewStopper ...
func NewStopper(ctx context.Context, sigs []os.Signal, handlers ...StopHandlerFunc) *Stopper {
	stopper := &Stopper{sigs: sigs, handlers: handlers}
	stopper.cancelContext, stopper.cancelFunc = context.WithCancel(ctx)
	return stopper
}

// WaitForExit ...
func (stopper *Stopper) WaitForExit() {
	go func() {
		stop := make(chan os.Signal, 1)
		signal.Notify(stop, syscall.SIGINT, syscall.SIGKILL, syscall.SIGTERM, syscall.SIGQUIT)
		// Blocking waiting
		<-stop

		// Call stopping handlers
		stopper.callStoppingHandlers()

		// Context cancel
		stopper.cancelFunc()
	}()

	select {
	case <-stopper.cancelContext.Done():
	}
}

// GetCancelContext ...
func (stopper *Stopper) GetCancelContext() *context.Context {
	return &stopper.cancelContext
}

// Stop ...
func (stopper *Stopper) Stop() {
	stopper.cancelFunc()
}

func (stopper *Stopper) callStoppingHandlers() {
	for _, handlerFunc := range stopper.handlers {
		handlerFunc()
	}
}
