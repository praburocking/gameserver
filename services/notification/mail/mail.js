var nodemailer = require('nodemailer');

var transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: 'prabu14it134@pec.com',
    pass: '******'
  }
});

var mailOptions = {
  from: 'prabumohan96@gmail.com',
  to: 'prabu14it134@pec.com',
  subject: 'Sending Email using Node.js',
  text: 'That was easy!'
};

const sendmail=()=>{
transporter.sendMail(mailOptions, function(error, info){
  if (error) {
    console.log(error);
  } else {
    console.log('Email sent: ' + info.response);
  }
});
}
module.exports=sendmail