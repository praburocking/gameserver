var nodemailer = require('nodemailer');
const config=require('./util/config')


console.log("mail config",config);
var transporter = nodemailer.createTransport({
  host:config.MAIL_DOMAIN, // hostname
  secureConnection: false, // TLS requires secureConnection to be false
  port: config.MAIL_PORT, // port for secure SMTP
  tls: {
     ciphers:'SSLv3'
  },
  auth: {
    user: config.MAIL_ID,
    pass: config.MAIL_PASS
  }
});

var mailOptions = {
  from: config.MAIL_ID,
  to: 'prabumohan96@gmail.com',
  subject: 'Sending Email using Node.js',
  html: 'That was easy!'
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
    mailOptions.html=text;
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