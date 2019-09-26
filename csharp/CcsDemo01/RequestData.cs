using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace CcsDemo
{
    public class RequestData
    {
        /// <summary>
        /// 接收手机号
        /// </summary>
        [JsonProperty("phoneNumber")]
        public List<string> PhoneNumber { get; set; }
        /// <summary>
        /// 下发扩展码。2 位纯数字
        /// </summary>
        [JsonProperty("extend")]
        public string ExtendCode { get; set; }
        [JsonProperty("messageBody")]
        public MessageBody MessageBody { get; set; }
    }

    public class MessageBody
    {
        /// <summary>
        /// 短信模板名称
        /// </summary>
        [JsonProperty("templateName")]
        public string TemplateName { get; set; }
        /// <summary>
        /// 短信模板参数，和模板中变量一一对应,没有变量则不需要
        /// </summary>
        [JsonProperty("templateParam")]
        public Dictionary<string,string> TemplateParam { get; set; }
    }
}
