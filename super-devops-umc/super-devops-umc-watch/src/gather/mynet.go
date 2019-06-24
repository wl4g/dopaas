package main

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"os"
	"os/exec"
	"strings"
)


var commandPath = "./netCommand.txt"

var command string

//var sumCommand = "ss -n sport == 22|awk '{sumup += $3};{sumdo += $4};END {print sumup,sumdo}'"


/*func main()  {
	var port string = "22"
	get(port)
}*/

func getNet(port string) string {
	if(command==""){
		command = ReadAll(commandPath)
	}
	command2 := strings.Replace(command, "#{port}", port, -1)
	s,_ := exec_shell(command2)
	fmt.Println(s)
	return s
}


//阻塞式的执行外部shell命令的函数,等待执行完毕并返回标准输出
func exec_shell(s string) (string, error){
	//函数返回一个*Cmd，用于使用给出的参数执行name指定的程序
	cmd := exec.Command("/bin/bash", "-c", s)
	//读取io.Writer类型的cmd.Stdout，再通过bytes.Buffer(缓冲byte类型的缓冲器)将byte类型转化为string类型(out.String():这是bytes类型提供的接口)
	var out bytes.Buffer
	cmd.Stdout = &out
	//Run执行c包含的命令，并阻塞直到完成。  这里stdout被取出，cmd.Wait()无法正确获取stdin,stdout,stderr，则阻塞在那了
	err := cmd.Run()
	checkErr(err)
	return out.String(), err
}

//错误处理函数
func checkErr(err error) {
	if err != nil {
		fmt.Println(err)
		panic(err)
	}
}

func ReadAll(filePth string) (string) {
	f, err := os.Open(filePth)
	if err != nil {
		panic(err)
	}
	s,_ := ioutil.ReadAll(f)
	return string(s)
}
