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
    process=$1 # 当前进度
    whole=$2 # 总进度数
    # 百分比比值(小数)
    percent_ratio=`awk BEGIN'{printf "%.2f", ('$process'/'$whole')}'`
    # 百分比数值
    percent=`awk BEGIN'{printf "%d", (100*'$percent_ratio')}'`
    let index=$((${process}%4))
    arr=( "|" "/" "-" "\\" )
    bar='>'
    for((i=0;i<($percent-1)/2;i++))
    do
        bar="="$bar
    done
    printf "[%-50s][%d%%][%3d/%03d][%c]\r" $bar $percent $process $whole "${arr[$index]}"
}

whole=200
process=0
while [ $process -lt $whole ]
do
    let process++
    processBar $process $whole
    sleep 0.1
done
printf "\n"


[=================================================>][100%][200/200][|]

*/