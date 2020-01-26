const express = require('express')
const bodyParser = require('body-parser')
const cors=require('cors')
// const morgan=require('morgan')
// const fs = require('fs')
//const path = require('path')
//const custmidware=require('./util/middleware')
const authorization=require('./services/authorization/core/authorization')

//controllers
 const signupRouter = require('./services/authorization/core/controllers/signup')

const loginRouter=require('./services/authorization/core/controllers/login')
const logoutRouter=require('./services/authorization/core/controllers/logout')
// const heroRouter=require('./controllers/hero')
const paymentRouter=require('./services/payment/core/controllers/payments')


//logs req and response
// var accessLogStream = fs.createWriteStream(path.join(__dirname, 'access.log'), { flags: 'a' })


const app = express()

app.use(express.static('build'))
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
// app.use(morgan('combined', { stream: accessLogStream }))
app.use(authorization);
app.use('/api/login',loginRouter);
app.use('/api/logout',logoutRouter);
app.use('/api/signup',signupRouter);
// app.use('/api/hero',heroRouter);
app.use('/api/pay',paymentRouter);


// app.get('/', (req, res) => {
//   res.send('<h1>Hello World this is rocking!</h1>')
// })

module.exports=app;