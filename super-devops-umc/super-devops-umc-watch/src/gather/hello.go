package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"github.com/kylelemons/go-gypsy/yaml"
	"github.com/shirou/gopsutil/cpu"
	"github.com/shirou/gopsutil/disk"
	"github.com/shirou/gopsutil/mem"
	"github.com/shirou/gopsutil/net"
	"io/ioutil"
	"net/http"
	"regexp"
	"strconv"
	"strings"
	"time"
)

//#全局变量(默认配置)
//数据提交地址
var serverUri string = "http://localhost:14046/umc/basic"
//频率,多少毫秒执行一次
var delay time.Duration = 10000
//网卡
var netCard string = "eth0"
//配置文件路径
var confPath string = "conf.yml"

//gather port
var port string = "22,6380"

//#返回
//id
var id string = "UNKNOW";



//初始化
func init()  {
	//get conf path
	flag.StringVar(&confPath, "p", "conf.yml", "conf path")
	flag.Parse()
	//flag.Usage()//usage
	fmt.Println("confPath="+confPath)
	//读取配置--read config
	config, err := yaml.ReadFile(confPath)
	if err != nil {
		fmt.Println(err)
	}else{
		serverUri,err = (config.Get("server-uri"))
		delayb,_ := (config.GetInt("physical.delay"))
		delay = time.Duration(delayb)
		netCard,err = (config.Get("physical.net"))
		port,_ = config.Get("physical.gatherPort")
	}
	fmt.Printf("config:serverUri=%v  delay=%v  net=%v\n",serverUri,time.Duration.String(delay),netCard)

	//init
	getId()


}

//主函数
func main() {
	//死循环
	/*for true {
		get()
		time.Sleep(delay * time.Millisecond)
	}*/
	//get()

	/*go memThread()
	go cpuThread()
	go diskThread()
	go netThread()*/

	/*for true {
		time.Sleep(100000 * time.Millisecond)
	}*/

	cpuThread()

}

//mem
func memThread()  {
	for true {
		var result Mem
		v, _ := mem.VirtualMemory()
		fmt.Printf("Total: %v, Free:%v, UsedPercent:%f%%\n", v.Total, v.Free, v.UsedPercent)
		//fmt.Println(v)

		result.Id = id
		result.Type = "mem"
		result.Mem = v

		fmt.Println("result = "+String(result))
		post("mem",result)
		time.Sleep(delay * time.Millisecond)
	}
}

//cpu
func cpuThread()  {
	for true {
		var result Cpu
		p, _ := cpu.Percent(0, false)
		//p, _ := cpu.Times(true)

		fmt.Println(p)
		/*pa, _ := cpu.Percent(10000* time.Millisecond, true)
		fmt.Println(pa)*/
		result.Id = id
		result.Type = "cpu"
		result.Cpu = p
		post("cpu",result)
		time.Sleep(delay * time.Millisecond)
	}
}

//disk
func diskThread()  {

	for true {
		var result Disk
		disks := getDisk()
		fmt.Println(disks)
		result.Id = id
		result.Type = "disk"
		result.Disks = disks
		post("disk",result)
		time.Sleep(delay * time.Millisecond)
	}

}

//net
func netThread()  {
	ports := strings.Split(port, ",")
	for true {
		var result NetInfos
		//n, _ := net.IOCounters(true)
		//fmt.Println(n)
		//te, _ := net.Interfaces()
		//fmt.Println(te)
		var n []NetInfo
		for _, p := range ports {
			re := getNet(p)
			res := strings.Split(re, " ")
			if(len(res)==9){
				var netinfo NetInfo
				netinfo.Port,_ = strconv.Atoi(p)
				netinfo.Up,_ = strconv.Atoi(res[0])
				netinfo.Down,_ = strconv.Atoi(res[1])
				netinfo.Count,_ = strconv.Atoi(res[2])
				netinfo.Estab,_ = strconv.Atoi(res[3])
				netinfo.CloseWait,_ = strconv.Atoi(res[4])
				netinfo.TimeWait,_ = strconv.Atoi(res[5])
				netinfo.Close,_ = strconv.Atoi(res[6])
				netinfo.Listen,_ = strconv.Atoi(res[7])
				netinfo.Closing,_ = strconv.Atoi(res[8])
				n = append(n, netinfo)
			}
		}
		result.Id = id
		result.Type = "net"
		result.NetInfo = n
		post("net",result)
		time.Sleep(delay * time.Millisecond)
	}
}

//提交数据
func post(ty string,v interface{}) {
	data := String(v)
	request, _ := http.NewRequest("POST", serverUri+"/"+ty,strings.NewReader(data))
	//json
	request.Header.Set("Content-Type", "application/json")
	//post数据并接收http响应
	resp,err :=http.DefaultClient.Do(request)
	if err!=nil{
		fmt.Printf("post data error:%v\n",err)
	}else {
		fmt.Println("post a data successful.")
		respBody,_ :=ioutil.ReadAll(resp.Body)
		fmt.Printf("response data:%v\n",string(respBody))
	}
}

func getDisk()  []DiskInfo {
	partitionStats, _ := disk.Partitions(false)
	var disks [] DiskInfo
	for _, value := range partitionStats {
		var disk1 DiskInfo
		mountpoint := value.Mountpoint
		usageStat,_ := disk.Usage(mountpoint)
		disk1.PartitionStat = value
		disk1.Usage = *usageStat
		disks = append(disks, disk1)
	}
	return disks
}

type Mem struct {
	Id string `json:"id"`
	Type string `json:"type"`
	Mem *mem.VirtualMemoryStat `json:"memInfo"`
}

type Cpu struct {
	Id string `json:"id"`
	Type string `json:"type"`
	Cpu []float64 `json:"cpu"`
}

type Disk struct {
	Id string `json:"id"`
	Type string `json:"type"`
	Disks []DiskInfo `json:"diskInfos"`
}

type DiskInfo struct {
	PartitionStat disk.PartitionStat `json:"partitionStat"`
	Usage disk.UsageStat `json:"usage"`
}

type NetInfos struct {
	Id string `json:"id"`
	Type string `json:"type"`
	NetInfo []NetInfo `json:"netInfos"`
}

type NetInfo struct {
	Port int `json:"port"`
	Up int `json:"up"`
	Down int `json:"down"`
	Count int `json:"count"`
	Estab int `json:"estab"`
	CloseWait int `json:"closeWait"`
	TimeWait int `json:"timeWait"`
	Close int `json:"close"`
	Listen int `json:"listen"`
	Closing int `json:"closing"`
}


func  String(v interface{}) string {
	s, err := json.Marshal(v)
	if err!=nil{
		fmt.Printf("Marshal data error:%v\n",err)
	}
	return string(s)
}

func getId()  {
	nets,_ :=net.Interfaces()
	var found bool = false
	for _, value := range nets {
		if(strings.EqualFold(netCard,value.Name)){
			hardwareAddr := value.HardwareAddr
			fmt.Println("found net card:"+hardwareAddr)
			id = hardwareAddr
			reg := regexp.MustCompile(`(2(5[0-5]{1}|[0-4]\d{1})|[0-1]?\d{1,2})(\.(2(5[0-5]{1}|[0-4]\d{1})|[0-1]?\d{1,2})){3}`)
			for _, addr := range value.Addrs {
				add := addr.Addr
				if(len(reg.FindAllString(add, -1))>0){
					fmt.Println("found ip "+add)
					found = true
					//id = add+" "+id
					id = add
					break
				}
			}
		}
	}
	if(!found){
		panic("net found ip,Please check the net conf")
	}
	/*str := "10.0.0.26"
	matched, err := regexp.MatchString("(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}", str)
	fmt.Println(matched, err)*/
}

