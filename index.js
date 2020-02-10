const app=require('./app')
const http= require('http')
//const config=require('./util/config')
const scheduler=require('./services/scheduler/core/scheduler')
const moment=require('moment')
const dotenv=require('dotenv')
const sendmail=require('./services/notification/mail/mail')
const server = http.createServer(app)


//starting the scheduler.
scheduler.runScheduler()

server.listen(process.env.PORT, () => {
  console.log(`Server running on port ${process.env.PORT}`)
})