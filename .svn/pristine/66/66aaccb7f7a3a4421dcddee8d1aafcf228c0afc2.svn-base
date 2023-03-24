
# 测试脚本：exec
function menu() {
    echo
    echo "=============显示菜单============="
    echo "| 1 - 桌面图标、配置文件和桌面背景打tar包"
    echo "| 2 - 遍历desktop路径下png图片路径,生成无包名icon_config文件"
    echo "| 3 - 查看设备内当前配置信息"
    echo "| 4 - 打印设备当前安装应用列表"
    echo "| 5 - 获取设备安装应用包名和名称"
    echo "| 6 - 生效桌面配置的图标"
    echo "| 7 - 输入本地tar包路径并生效"
    echo "| 8 - 拷贝文件到设备内"
    echo "| q - 退出"
    echo "---------------------------------"
    read -p "请选择菜单：" -n 1 n
    echo
    if [[ "$n" = "1" ]]; then
        adb shell tar -cvf desktop-icons-default.tar /data/local/setting/desktop /data/local/setting/default_workspace_5x4.xml /data/local/tmp/desktop/default.png
        mkdir -p /tmp/desktop
        adb pull desktop-icons-default.tar /tmp/desktop
        adb shell rm -fr desktop-icons-default.tar
    elif [ "$n" = "2" ]; then
        i=0
        content=""
        # 获取路径下所有png图片路径
        for line in `adb shell du -a /data/local/setting/desktop | grep png | awk '{print $2}'`
        do
            ((i++))
            fileNameNoExtension=$(basename $line .png)
#            echo "$fileNameNoExtension $line"
#            echo "{\"pkg\":\"\",\"icon\":\"$line\",\"appname\":\"$fileNameNoExtension\"}"
            if [ $i = 1 ]; then
                content="{\"pkg\":\"\",\"icon\":\"$line\",\"appname\":\"$fileNameNoExtension\"}"
            else
                content="$content,{\"pkg\":\"\",\"icon\":\"$line\",\"appname\":\"$fileNameNoExtension\"}"
            fi
        done
#        echo $content
        headCfg="{\"enableIconRoundCorner\":true,\"iconRoundCornerSize\":30,\"enableIconBackgroundColor\":false,\"iconBackgroundColor\":\"#FFFFFF\",\"enableIconReplace\":true,\"appsdetails\":[$content]}"
        echo "$headCfg\c" > /tmp/icon_config.json
        subl /tmp/icon_config.json
    elif [ "$n" = "3" ]; then
        adb shell cat /data/local/setting/desktop/icon_config.json | jq '.appsdetails'
#        adb shell cat /data/local/setting/desktop/icon_config.json | jq '.appsdetails[]'
#        adb shell cat /data/local/setting/desktop/icon_config.json | jq "[.appsdetails[] | {pkg:.pkg,appname:.appname}]"
    elif [ "$n" = "4" ]; then
#        echo "---------------------------所有应用:"
#        adb shell pm list packages | awk -F\: '{print $2}'
        echo "---------------------------系统应用:"
        adb shell pm list packages -s | awk -F\: '{print $2}'
        echo "---------------------------第三方应用:"
        adb shell pm list packages -3 | awk -F\: '{print $2}'
    elif [ "$n" = "5" ]; then
      adb install -r -t /Users/tomchen/project/android/work/smartWork/_ssh/apk-tool/pmListPackages.apk
#        echo "打印第三方应用信息"
#        adb shell am startservice -a my.pm.list.packages --es extra third
#        echo "打印系统应用信息"
#        adb shell am startservice -a my.pm.list.packages --es extra system
        echo "打印全部应用信息"
        adb shell am startservice -a my.pm.list.packages --es extra all
        adb pull /sdcard/appsInfo.txt /tmp
        adb shell rm -fr /sdcard/appsInfo.txt
        adb uninstall com.jen.demo
        subl /tmp/appsInfo.txt
    elif [ "$n" = "q" ]; then
        echo 退出
        return
    elif [ "$n" = "6" ]; then
        adb shell rm -fr /data/local/config/launcher3
        adb shell am force-stop com.cyjh.huawei.launcher3
        adb shell pm clear com.cyjh.huawei.launcher3
        adb shell input keyevent 3
    elif [ "$n" = "7" ]; then
        read -p "请输入tar包路径：" tarFile
        fileName=`basename $tarFile`
        destPath=/data/local/tmp/$fileName
        echo $fileName
        echo $destPath
        adb push $tarFile $destPath
        adb shell tar -xvf $destPath -C /
        adb shell rm -fr /data/local/config/launcher3
        adb shell am force-stop com.cyjh.huawei.launcher3
        adb shell pm clear com.cyjh.huawei.launcher3
        adb shell input keyevent 3
    elif [ "$n" = "8" ]; then
        if [ -f /Users/tomchen/Documents/AndroidStudio/DeviceExplorer/meizu-m3_note-127.0.0.1_5038/data/local/setting/default_workspace_5x4.xml ]; then
            adb push /Users/tomchen/Documents/AndroidStudio/DeviceExplorer/meizu-m3_note-127.0.0.1_5038/data/local/setting/default_workspace_5x4.xml /data/local/setting/default_workspace_5x4.xml
        fi
        if [ -f /Users/tomchen/Documents/AndroidStudio/DeviceExplorer/meizu-m3_note-127.0.0.1_5038/data/local/setting/desktop/icon_config.json ]; then
            adb push /Users/tomchen/Documents/AndroidStudio/DeviceExplorer/meizu-m3_note-127.0.0.1_5038/data/local/setting/desktop/icon_config.json /data/local/setting/desktop/icon_config.json
        fi
    else
        echo "other"
    fi
    menu
}
menu
