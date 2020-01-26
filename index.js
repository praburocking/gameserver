const app=require('./app')
const http= require('http')
//const config=require('./util/config')
//const scheduler=require('./services/scheduler/core/scheduler')
//const slack=require('./services/notification/slack/slack')
const moment=require('moment')
const dotenv=require('dotenv')

const server = http.createServer(app)


//const scheduled_job=scheduler.ScheduleJob({},"sendnotification",{seconds:30},new Date(),"sendnotification",12);
//scheduler.runScheduler()

server.listen(process.env.PORT, () => {
  console.log(`Server running on port ${process.env.PORT}`)
})