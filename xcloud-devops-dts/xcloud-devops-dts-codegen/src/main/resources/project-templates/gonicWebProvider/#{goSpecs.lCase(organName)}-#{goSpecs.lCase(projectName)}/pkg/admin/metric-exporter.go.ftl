// ${watermark}

${copyright}
package admin

import (
	"fmt"
	"net/http"
	"runtime"
	"strconv"

	acl "${organName?lower_case}-${projectName?lower_case}/pkg/acl"
	config "${organName?lower_case}-${projectName?lower_case}/pkg/config"
	logging "${organName?lower_case}-${projectName?lower_case}/pkg/logging"
	<#-- TODO remove -->
	<#-- ssh2 "${organName?lower_case}-${projectName?lower_case}/pkg/modules/ssh2"
	store "${organName?lower_case}-${projectName?lower_case}/pkg/modules/ssh2/store" -->
	utils "${organName?lower_case}-${projectName?lower_case}/pkg/utils"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"github.com/shirou/gopsutil/mem"
	"go.uber.org/zap"
)

type adminMetricExporter struct {
	mysqlConnActiveDesc       *prometheus.Desc
	<#-- ssh2DispatchersActiveDesc *prometheus.Desc -->
	goroutinesDesc            *prometheus.Desc
	threadsDesc               *prometheus.Desc
	vTotalMemDesc             *prometheus.Desc
	vFreeMemDesc              *prometheus.Desc
}

// @see https://www.jianshu.com/p/5db23a280e1d
func newAdminMetricExporter() *adminMetricExporter {
	// Gets current IP
	ip, _ := utils.GetFirstNonLoopbackIP()

	return &adminMetricExporter{
		mysqlConnActiveDesc: prometheus.NewDesc(
			"mysql_active_conn", "Current active connections of MySQL",
			[]string{"dbconnectstr"},               // 动态tag字段名
			prometheus.Labels{"host": ip.String()}, // 静态tag字段及值(key=value)
		),
		<#-- ssh2DispatchersActiveDesc: prometheus.NewDesc(
			"ssh2_active_dispatch_channels", "Current active channels of SSH2 dispatcher",
			[]string{"user"},
			prometheus.Labels{"host": ip.String()},
		), -->
		goroutinesDesc: prometheus.NewDesc(
			"goroutines_num",
			"Number of goroutines",
			nil, nil),
		threadsDesc: prometheus.NewDesc(
			"threads_num",
			"Number of threads",
			nil, nil),
		vTotalMemDesc: prometheus.NewDesc(
			"virtual_total_memory",
			"Size of virtual total memory",
			nil, nil),
		vFreeMemDesc: prometheus.NewDesc(
			"virtual_free_memory",
			"Size of virtual free memory",
			nil, nil),
	}
}

func (that *adminMetricExporter) Describe(ch chan<- *prometheus.Desc) {
	ch <- that.mysqlConnActiveDesc
	<#-- ch <- that.ssh2DispatchersActiveDesc  -->
	ch <- that.goroutinesDesc
	ch <- that.threadsDesc
	ch <- that.vTotalMemDesc
	ch <- that.vFreeMemDesc
}

func (that *adminMetricExporter) Collect(ch chan<- prometheus.Metric) {
	<#-- stat := store.GetDelegate().Stat()
	activeConns, _ := strconv.ParseFloat(strconv.Itoa(stat.ActiveConns), 64)
	ch <- prometheus.MustNewConstMetric(
		that.mysqlConnActiveDesc,
		prometheus.GaugeValue,
		activeConns,
		stat.DbConnectStr,
	) -->

	actives, _ := strconv.ParseFloat(strconv.Itoa(ssh2.GetStatDispatchers().Len()), 64)
	ch <- prometheus.MustNewConstMetric(
		that.ssh2DispatchersActiveDesc,
		prometheus.GaugeValue,
		actives,
		acl.GetPrincipal(),
	)

	ch <- prometheus.MustNewConstMetric(
		that.goroutinesDesc,
		prometheus.GaugeValue,
		float64(runtime.NumGoroutine()),
	)

	num, _ := runtime.ThreadCreateProfile(nil)
	ch <- prometheus.MustNewConstMetric(
		that.threadsDesc,
		prometheus.GaugeValue,
		float64(num),
	)

	vm, _ := mem.VirtualMemory()
	ch <- prometheus.MustNewConstMetric(
		that.vTotalMemDesc,
		prometheus.GaugeValue,
		float64(vm.Total)/1e9,
	)
	ch <- prometheus.MustNewConstMetric(
		that.vFreeMemDesc,
		prometheus.GaugeValue,
		float64(vm.Free)/1e9,
	)
}

// ServeStart ...
func ServeStart() {
	registry := prometheus.NewRegistry()

	exporter := newAdminMetricExporter()
	if err1 := registry.Register(exporter); err1 != nil {
		panic(fmt.Sprintf("Failed to register admin exporter. err: %v", err1))
	}
	logging.Main.Info("Starting prometheus exporter...", zap.String("Listen", config.GlobalConfig.Admin.Listen))

	http.Handle("/metrics", promhttp.HandlerFor(registry, promhttp.HandlerOpts{}))
	if err2 := http.ListenAndServe(config.GlobalConfig.Admin.Listen, nil); err2 != nil {
		panic(fmt.Sprintf("Failed to start admin prometheus exporter. err: %v", err2))
	}
}
