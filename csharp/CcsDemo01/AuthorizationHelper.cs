using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace CcsDemo
{
    internal class AuthorizationHelper
    {
        /// <summary>
        /// create token
        /// </summary>
        /// <param name="key">密钥：密钥分为两种：-full: 可以用于 REST API 和设备端 SDK，-device: 只能用于设备端 SDK</param>
        /// <param name="keyName">full/device</param>
        /// <param name="timeout">超时时间</param>
        /// <returns></returns>
        public static string CreateSASToken(string key, string keyName, TimeSpan timeout)
        {
            const string Schema = "SharedAccessSignature";
            const string SignKey = "sig";
            const string KeyNameKey = "skn";
            const string ExpiryKey = "se";

            var values = new Dictionary<string, string>
            {
                { KeyNameKey, keyName },
                { ExpiryKey, (DateTimeOffset.UtcNow + timeout).ToUnixTimeSeconds().ToString() }
            };

            var signContent = string.Join("&", values
                .Where(pair => pair.Key != SignKey)
                .OrderBy(pair => pair.Key)
                .Select(pair => $"{pair.Key}={HttpUtility.UrlEncode(pair.Value)}"));

            string sign;
            using (var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(key)))
            {
                sign = Convert.ToBase64String(hmac.ComputeHash(Encoding.UTF8.GetBytes(signContent)));
            }

            return $"{Schema} {SignKey}={HttpUtility.UrlEncode(sign)}&{signContent}";
        }
    }
}
