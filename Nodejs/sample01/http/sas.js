
const crypto = require('crypto');


const generateSasTokenNoSR = function (signingKey, keyName, expiresInMins) {


    const Schema = "SharedAccessSignature";
    const SignKey = "sig";
    const KeyNameKey = "skn";
    const ExpiryKey = "se";
    // Set expiration in seconds
    var expires = (Date.now() / 1000) + expiresInMins * 60;
    expires = Math.ceil(expires);

    // the order of keys is importrant 
    const mapping = {
        [ExpiryKey]: expires, 
        [KeyNameKey]: keyName
    };

    const toSign  = Object.keys(mapping).map(key => {
        return `${key}=${encodeURIComponent(mapping[key])}`;
    }).join('&');

    // Use crypto
    var hmac = crypto.createHmac('sha256', Buffer.from(signingKey, 'utf8'));
    hmac.update(toSign);
    var base64UriEncoded = encodeURIComponent(hmac.digest('base64'));
    const _token = `${Schema} ${SignKey}=${base64UriEncoded}&${toSign}`;
    // console.log(_token);
    // Construct authorization string
    return _token;
};



module.exports = { generateSasTokenNoSR };