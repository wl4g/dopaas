public class Test1 {

	public static void main(String[] args) throws Exception {
		Process ps = Runtime.getRuntime()
				.exec("printf '[%-100s][%d%%][%3d/%3d][\\e[43;46;1m%c\\e[0m]' '=========>' 90 10 100 '/'");
		ps.waitFor();
		int n = 0;
		byte[] buffer = new byte[1024];
		while (-1 != (n = ps.getInputStream().read(buffer))) {
			System.out.write(buffer, 0, n);
		}
	}

}


/*


#!/bin/bash

processBar()
{
    let process=$1
    let whole=$2
    let index=$((${process}%4))
    arr=( "|" "/" "-" "\\" )
    bar='>'
    for((i=0;i<process-1;i++))
    do
        bar="="$bar
    done
    printf "[%-100s][%d%%][%3d/%03d][\e[43;46;1m%c\e[0m]\r" $bar $process $process $whole "${arr[$index]}"
}

whole=100
process=0
while [ $process -lt $whole ]
do
    let process++
    processBar $process $whole
    sleep 0.1
done
printf "\n"



[===============================================================================>][100%][100/100][|]


*/