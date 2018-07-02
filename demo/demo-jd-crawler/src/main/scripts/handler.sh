 #!/bin/bash
 #--------------------------------------------------------------------
 #作者：zhijun.zhang
 #日期：2012-06-11
 #参数：{start|stop|restart}
 #功能：对handler程序的启停
  #-------------------------------------------------------------------

JAR="${project.build.finalName}.jar"

function start(){
    echo "开始启动 ...."
    source /etc/profile
    num=`ps -ef|grep java |grep $JAR|wc -l`
    echo "进程数:$num"
    if [ "$num" = "0" ] ; then
       CONFIG_PATH=`cd ../config && pwd`
       eval nohup java -Xmx2048m -jar -DconfigPath=$CONFIG_PATH ../$JAR >> /dev/null 2>&1 &
       echo "启动成功...."
       touch ../../log/crawler.log
       tail -f ../../log/crawler.log
    else
       echo "进程已经存在，启动失败，请检查....."
       exit 0
    fi
}

function stop(){
   echo "开始stop ....."
   num=`ps -ef|grep java |grep $JAR|wc -l`
   if [ "$num" != "0" ] ; then
     ps -ef|grep java|grep $JAR|awk '{print $2;}'|xargs kill -9
     echo "进程已经关闭..."
   else
     echo "服务未启动，无需停止..."
   fi
}


function restart(){
 echo "begin stop process ..."
 stop
 echo "process stoped,and starting ..."
 start
 echo "started ..."
}

if [ "$#" = 1 ] ; then
   cmd=$1
   case "$cmd" in
      "start")
         start
         exit 0
       ;;
      "stop")
         stop
         exit 0
        ;;
      "restart")
          restart
          exit 0
        ;;
      *)
          echo "用法： $0 {start|stop|restart}"
          exit 1
       ;;
     esac
else
  echo "参数不正确，使用方法: $0 {start|stop|restart}"
  exit 0
fi
