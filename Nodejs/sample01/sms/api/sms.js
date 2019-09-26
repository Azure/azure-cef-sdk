const { httpClientSAS, } = require('./../../http/client');


function sendMessage(message) {
    
    
    const url = `services/sms/messages`;
    
    return httpClientSAS.request({
        // `url` is the server URL that will be used for the request
        url: url,
        // `method` is the request method to be used when making the request
        method: 'post', // default
        params: {
            'api-version': '2018-10-01'
        },
        data: message
    
    }).then(res => {
        console.log(res.data);
        return res.data;
    }, err => {
        console.log(err.response.data);
    });
    
}


module.exports = {
    sendMessage
};
