var nodemailer = require('nodemailer');
const config=require('./util/config')


console.log("mail config",config);
var transporter = nodemailer.createTransport({
  host: "smtp-mail.outlook.com", // hostname
  secureConnection: false, // TLS requires secureConnection to be false
  port: 587, // port for secure SMTP
  tls: {
     ciphers:'SSLv3'
  },
  auth: {
    user: config.MAIL_ID,
    pass: config.MAIL_PASS
  }
});

var mailOptions = {
  from: 'prabumohan96@outlook.in',
  to: 'prabumohan96@gmail.com',
  subject: 'Sending Email using Node.js',
  text: 'That was easy!'
};

const sendmail=(to,subject,text)=>{
  if(to)
  {
    mailOptions.to=to;
  }
  if(subject)
  {
    mailOptions.subject=subject;
  }
  if(text)
  {
    mailOptions.text=text;
  }
transporter.sendMail(mailOptions, function(error, info){
  if (error) {
    console.log(error);
  } else {

    console.log('Email sent: ' + info.response);
  }
});
}
module.exports=sendmail