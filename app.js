const express = require('express')
const bodyParser = require('body-parser')
const cors=require('cors')
const path = require('path')
const fileupload = require('express-fileupload')
const authorization=require('./services/authorization/core/authorization')
//controllers
const signupRouter = require('./services/authorization/core/controllers/signup')

const loginRouter=require('./services/authorization/core/controllers/login')
const logoutRouter=require('./services/authorization/core/controllers/logout')

const paymentRouter=require('./services/payment/core/controllers/payments')
const forgotRouter=require('./services/authorization/core/controllers/forgotPasword')
const verifyUserRouter=require('./services/authorization/core/controllers/verifyUser')
const fileRouter=require('./app/controllers/fileRouter')
const File=require('./app/models/uploads')
//const busboy = require('connect-busboy');


//logs req and response

const app = express()
app.use(
  express.json({
    verify: function(req, res, buf) {
      if (req.originalUrl.startsWith("/api/pay/stripe/webhook")) {
        req.rawBody = buf.toString();
      }
    }
  })
);

app.use(bodyParser.json())
app.use(cors())
// app.use(busboy({
//   highWaterMark: 2 * 1024 * 1024, 
// })); 

app.use('/api/',fileupload());
app.use('/api/',authorization);
app.use('/api/login',loginRouter);
app.use('/api/logout',logoutRouter);
app.use('/api/signup',signupRouter);
app.use('/api/pay',paymentRouter);
app.use('/api/forgotpassword',forgotRouter.forgotPassRouter);
app.use('/api/resetpass',forgotRouter.resetPasswordRouter);
app.use('/api/verifyuser',verifyUserRouter);
app.use('/api/file',fileRouter);
app.use(express.static('build'))

app.get('*', (req,res) =>{
  console.log(__dirname+'/build/index.html');
  res.sendFile(path.join(__dirname+'/build/index.html'));
});

module.exports=app;