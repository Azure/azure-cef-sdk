const yargs = require('yargs');
const { sendMessage, sendSMSCallBack } = require('./api/sms');


yargs.command('send', 'make a get HTTPs request', function (_yargs) {
    return _yargs.option('url', {
        alias: 'u',
        default: 'https://localhost/'
    }).array('body.phoneNumber')    ;
}, function (argv) {

    const {
        body: content
    } = argv;

    console.log(argv);


    /*
      {
        "phoneNumber": [
            "13810829076"
        ],
        "extend": "10",
        "messageBody": {
            "templateName": "MySMSTemplate001",
            "templateParam": {
            "username": "user1",
            "productname": "123"
            }
        }
        }
    */
    const {
        phoneNumber,
        extend,
        messageBody
    } = content;


    if (!phoneNumber
        || !extend
        || !messageBody
    ) {
        console.log('The input content is not right!');
        process.exit();
    }

    const { templateName, templateParam} = messageBody;

    // if (!templateName
    //     || !templateParam
    // ) {
    //     console.log('The input messageBody is not right!');
    //     process.exit();
    // }


    // console.log(argv.url);
    return sendMessage(content).then(res =>{
        /*
        msgtype=2&phone=13400000000 &reqid=16011514475100000001&extend=&receivetime=20160101081 220&sendtime=20160101081218&state=0
        yyyyMMddHHmmss
        */
       
    //    const time = new Date();
    //    const {messageId: reqid} = res;
    //     return sendSMSCallBack(acctountName,
    //         'Yuexin', 'jiwagkey', {
    //         msgtype: 2,
    //         phone: phoneNumber[0],
    //         reqid: reqid,
    //         extend: '',
    //         receivetime: `${time.getFullYear()}${time.getMonth()}${time.getDay()}${time.getHours()}${time.getMinutes()}00`,
    //         sendtime: `${time.getFullYear()}${time.getMonth()}${time.getDay()}${time.getHours()}${time.getMinutes()}00`,
    //         state: 0
    //     });
    });
}
)
.help()
.argv;