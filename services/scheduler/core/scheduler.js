const cronJob=require('cron').CronJob
const Scheduler=require('../models/scheduler')
const funct=require('../util/funct')
const async=require('async')
const moment=require('moment')
const sendSlackMsg= require('../../notification/slack/core/slack')
let cronTime="*/15 * * * * *"

const runCurrentJobs=async (job,funct_keys,callback) =>{
 if ( funct_keys.includes(job.funct) && typeof funct[job.funct] === "function") {
     try{
        funct[job.funct].apply(this,job.params)
        if(job.remaining_cycle>0 && job.repeat_interval && job.repeat_interval!==null)
            {
                await Scheduler.update({ _id: job.id }, { remaining_cycle:job.remaining_cycle-1, next_time:moment(job.next_time).utc().add(job.repeat_interval)});
            }
         else
            {
            let updatedSch=await Scheduler.update({ _id: job.id }, { is_completed: true });
            callback()
            }
     }
     catch(exp1)
     {
         console.log("exception in running the job",exp1)
         await sendSlackMsg({userid:job.userid,message:"exception while running " + job.funct + " function, exp=>"+exp1,type:"Exception"});
         callback(exp1);
     }
   }
   else {
     await sendSlackMsg({userid:job.userid,message:"could not find " + job.funct + " function",type:"Exception"});
     callback("could not find " + job.funct + " function")
   }
}

const onTick=async ()=>
{
    try{
        console.log("Started processing at: ",new Date().toString())
    let current_jobs=await Scheduler.find({is_completed:false}).where("next_time").lt(moment().utc().format())
    current_jobs=current_jobs.map(job=>job.toJSON());
    const funct_keys=Object.keys(funct);
    async.eachLimit(
        current_jobs,
        15,
        (job,callback)=>runCurrentJobs(job,funct_keys,callback),
        (err)=>{
                if(err)
                     {
                         console.log("error while running the jobs=>",err);
                     }
                }
    )
    console.log("Done processing at: ",new Date().toString())
    }
    catch(exp)
    {
        await sendSlackMsg({message:" exception while runing job=>"+exp,type:"Exception"});
    }
}
let onComplete=null;
let start=true;
let timeZone='Europe/London'


const runScheduler=()=>
{
new cronJob(cronTime,onTick,onComplete,start,timeZone);

}

//schedule job using this function
const scheduleJob=async (params,user,repeat_interval,time,funct,remaining_cycle)=>
{
try{
   if( typeof params==="object" && typeof user==="string" && typeof repeat_interval==="object" && typeof time==="object" && typeof funct==="string" && typeof remaining_cycle==="number")
   {
const scheduler=Scheduler({
    is_completed:false,
    repeat_interval:repeat_interval,
    params:params,
    user:user,
    next_time:moment(time).utc().format(),
    funct:funct,
    remaining_cycle:remaining_cycle
});
const savedScheduler=await scheduler.save();
return savedScheduler.toJSON()
   }
   throw new Error('parameter mistype');
}
catch(exp)
{
    await sendSlackMsg({ userid:user,message:" exception while scheduling jobs=>"+exp,type:"Exception"});
}
}

module.exports ={runScheduler,scheduleJob};