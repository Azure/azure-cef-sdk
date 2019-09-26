const axios = require('axios');
const https = require('https');
const fs = require('fs');

const path = require('path');
const { generateSasTokenNoSR } = require('./sas');


const httpsAgent = new https.Agent({
    rejectUnauthorized: false
});

var httpClientSAS = null;

var jsonFile = fs.readFileSync(path.resolve(__dirname, './config.json'));
if (jsonFile) {
    const sampleParameters = JSON.parse(jsonFile);

    const { ccs } = sampleParameters;
    const { account, key, role, endpoint } = ccs;

    const signature = generateSasTokenNoSR(key, role, 20);

    httpClientSAS = axios.create({
        baseURL: endpoint,
        // timeout: 10000,
        headers: {
            'X-Custom-Header': 'foobar',
            "Content-Type": "application/json",
            "Account": account,
            'Authorization': signature,
        },
        httpsAgent: httpsAgent,
    });


} else {
    console.log('File not found, falling back to defaults: ' + parametersFile);
    process.exit();
}




module.exports = {
    httpClientSAS
};






