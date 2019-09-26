using System;
using System.Collections.Generic;
using System.Text;

namespace CcsDemo.Configurations
{
    public class Config
    {
        public readonly static string _endpoint = "https://bluecloudccs.21vbluecloud.com:443/services/sms/messages?api-version=2018-10-01";
        //测试ccs账号名称
        public readonly static string _testCcsAccount = "testacc";
        //密钥名称
        public readonly static string _keyName = "full";
        //密钥
        public readonly static string _keyValue = "";
        //测试模板名称
        public readonly static string _testTemplateName = "demo";
        //测试手机号
        public readonly static string _testMobile = "";
        //下发扩展码，两位纯数字
        public readonly static string _testExtend = "08";
    }
}
