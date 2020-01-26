how to use:
1.import scheduler from scheduler/core

2. to start the scheduler=>scheduler.runScheduler()
the scheduler will start looking in the shceduler table for the jobs to run, the next_run should be greater than current time.

3.to Schedule a job
to schedule a job we can use scheduler.ScheduleJob function parameters
params=>Jsonarray of the function params
userid=>user_id as string, if it is some other common scheduler please mention it here it will help us to debug if there is any exception.
interval=>time interval in jsonobject, for format please refer momentjs wiki
next_date=>time when the scheduler has to run need to be as a date object, you can provide date in local format, no need to convert it into GMT, we will do it for you...
funct=>function name in String
repeat=> how many times you want the current function to repeat with the current interval
NOTE: before you use any function for scheduling purpose you need to add that function in util/funct  =>funct object with the same function name. we will fetch your function from there. 

sample job scheduling:
scheduler.ScheduleJob({},"sendnotification",{seconds:30},new Date(),"sendnotification",12);
