using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net;
using System.Text;

using CcsDemo.Configurations;

namespace CcsDemo
{
    class Program
    {
        
        static void Main(string[] args)
        {
            try
            {
                //请求数据
                var requestData = new RequestData();
                requestData.PhoneNumber = new List<string>() { Config._testMobile };
                requestData.ExtendCode = Config._testExtend;
                requestData.MessageBody = new MessageBody();
                requestData.MessageBody.TemplateName = Config._testTemplateName;
                requestData.MessageBody.TemplateParam = new Dictionary<string, string>();

                var req = (HttpWebRequest)WebRequest.Create(Config._endpoint);
                req.ContentType = "application/json";
                req.Method = "POST";
                string jsonPayload = JsonConvert.SerializeObject(requestData);
                var data = Encoding.UTF8.GetBytes(jsonPayload);
                req.ContentLength = data.Length;
                using (var stream = req.GetRequestStream())
                {
                    stream.Write(data, 0, data.Length);
                }

                //ccs account name
                req.Headers.Add("Account", Config._testCcsAccount);

                //获取token
                var token = AuthorizationHelper.CreateSASToken(Config._keyValue, Config. _keyName, TimeSpan.FromSeconds(30));
                req.Headers.Add("Authorization", token);

                var webResponse = req.GetResponse();
                Console.WriteLine("Send Success!");
                Console.ReadLine();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Send Error:" + ex.Message);
                Console.ReadLine();
            }
        }
    }
}
